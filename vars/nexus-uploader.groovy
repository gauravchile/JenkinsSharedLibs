#!/usr/bin/env groovy
/**
 * Universal Nexus Artifact Uploader
 * ----------------------------------
 * Uploads any file(s) from a given directory to a Nexus raw (hosted) repository.
 *
 * ✅ Works with:
 *   - Trivy scan reports
 *   - Build artifacts (JAR, WAR, ZIP, TAR)
 *   - Logs, HTML reports, SBOMs, etc.
 *
 * 🔧 Example:
 *   groovy nexus-uploader.groovy \
 *     --nexus http://localhost:8081/repository/devops-artifacts \
 *     --user admin \
 *     --pass mypassword \
 *     --dir reports \
 *     --pattern *.json
 */

import groovy.cli.commons.CliBuilder

def cli = new CliBuilder(usage: 'groovy nexus-uploader.groovy [options]')
cli.h(longOpt: 'help', 'Show usage information')
cli._(longOpt: 'nexus', args: 1, argName: 'NEXUS_URL', 'Base URL of Nexus repository')
cli._(longOpt: 'user', args: 1, argName: 'USERNAME', 'Nexus username')
cli._(longOpt: 'pass', args: 1, argName: 'PASSWORD', 'Nexus password')
cli._(longOpt: 'dir',  args: 1, argName: 'DIRECTORY', 'Directory containing files to upload')
cli._(longOpt: 'pattern', args: 1, argName: 'PATTERN', 'File pattern to upload (default: *)')

def opts = cli.parse(args)
if (!opts || opts.h || !opts.nexus || !opts.user || !opts.pass || !opts.dir) {
    cli.usage()
    System.exit(1)
}

def nexusUrl = opts.nexus
def username = opts.user
def password = opts.pass
def uploadDir = new File(opts.dir)
def pattern = opts.pattern ?: "*"

if (!uploadDir.exists() || !uploadDir.isDirectory()) {
    println "❌ Directory not found: ${uploadDir.absolutePath}"
    System.exit(1)
}

println "📦 Starting upload to Nexus repository: ${nexusUrl}"
println "📁 Directory: ${uploadDir.absolutePath}"
println "🔍 Pattern: ${pattern}"
println "------------------------------------------"

def files = uploadDir.listFiles().findAll { it.name ==~ pattern.replace("*", ".*") }
if (!files) {
    println "⚠️  No files matching pattern '${pattern}' found in directory."
    System.exit(0)
}

files.each { file ->
    println "📤 Uploading: ${file.name}"
    def process = [
        'curl', '-s', '-w', 'HTTP %{http_code}\\n',
        '-u', "${username}:${password}",
        '--upload-file', file.absolutePath,
        "${nexusUrl}/${file.name}"
    ].execute()
    process.in.eachLine { println it }
    process.waitFor()
    if (process.exitValue() == 0) {
        println "✅ Uploaded successfully: ${file.name}\\n"
    } else {
        println "❌ Upload failed for ${file.name}\\n"
    }
}

println "🎯 Upload completed for ${files.size()} file(s)."
