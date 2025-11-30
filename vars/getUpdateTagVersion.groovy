// vars/getUpdateTagVersion.groovy
def call(Map args = [:]) {
    def yamlDir = args.get('yamlDir', 'manifests/base')
    echo "🧭 Scanning for Kubernetes/Helm manifests in: ${yamlDir}"

    // Try to detect existing image tag in manifests (first occurrence)
    def currentTag = sh(
        script: "grep -E 'image:' ${yamlDir}/*.yaml | head -1 | awk '{print \$2}' | cut -d':' -f2",
        returnStdout: true
    ).trim()

    // Sanitize (remove color prefixes or anything non-numeric/semantic)
    def sanitized = currentTag.replaceAll('[^0-9v\\.]', '')

    // Default if nothing found
    if (!sanitized) sanitized = 'v0.0.0'

    // Split semver and bump patch
    def parts = sanitized.replace('v', '').tokenize('.')
    while (parts.size() < 3) { parts << '0' }

    def major = parts[0].isInteger() ? parts[0].toInteger() : 0
    def minor = parts[1].isInteger() ? parts[1].toInteger() : 0
    def patch = parts[2].isInteger() ? parts[2].toInteger() + 1 : 1

    def newVersion = "v${major}.${minor}.${patch}"
    echo "🔖 Detected current tag: ${currentTag} → New version: ${newVersion}"
    return newVersion
}
