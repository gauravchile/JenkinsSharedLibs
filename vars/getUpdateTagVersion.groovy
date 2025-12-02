// vars/getUpdateTagVersion.groovy
def call(Map args = [:]) {
    // -------------------------------------------------------------------------
    // 1️⃣ Resolve directory (defaults to most common Helm path)
    // -------------------------------------------------------------------------
    def yamlDir = args.get('yamlDir', '')
    if (!yamlDir?.trim()) {
        if (fileExists('helm')) {
            yamlDir = sh(script: "find helm -type d -maxdepth 2 | head -1", returnStdout: true).trim()
        } else if (fileExists('manifests')) {
            yamlDir = 'manifests'
        } else {
            yamlDir = '.'
        }
    }

    echo "🧭 Scanning for Kubernetes/Helm manifests in: ${yamlDir}"

    // -------------------------------------------------------------------------
    // 2️⃣ Detect last image tag from YAMLs (values.yaml, templates, etc.)
    // -------------------------------------------------------------------------
    def currentTag = sh(
        script: "grep -E 'image:' ${yamlDir}/*.yaml 2>/dev/null | head -1 | awk '{print \$2}' | cut -d':' -f2 || echo 'v0.0.0'",
        returnStdout: true
    ).trim()

    def sanitized = currentTag.replaceAll('[^0-9v\\.]', '')
    if (!sanitized) sanitized = 'v0.0.0'

    // -------------------------------------------------------------------------
    // 3️⃣ Parse current version
    // -------------------------------------------------------------------------
    def parts = sanitized.replace('v', '').tokenize('.')
    while (parts.size() < 3) { parts << '0' }

    def major = parts[0].isInteger() ? parts[0].toInteger() : 0
    def minor = parts[1].isInteger() ? parts[1].toInteger() : 0
    def patch = parts[2].isInteger() ? parts[2].toInteger() : 0

    // -------------------------------------------------------------------------
    // 4️⃣ Determine bump type from latest commit
    // -------------------------------------------------------------------------
    def commitMsg = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
    echo "📝 Last commit message: '${commitMsg}'"

    if (commitMsg =~ /(?i)breaking change|!/) {
        major++; minor = 0; patch = 0
        echo "🔺 Detected MAJOR bump (breaking change)"
    } else if (commitMsg =~ /(?i)^feat:/) {
        minor++; patch = 0
        echo "🔹 Detected MINOR bump (feature)"
    } else if (commitMsg =~ /(?i)^fix:/) {
        patch++
        echo "🔸 Detected PATCH bump (fix)"
    } else {
        patch++
        echo "⚪ Default PATCH bump (no semantic keyword found)"
    }

    // -------------------------------------------------------------------------
    // 5️⃣ Build new semver
    // -------------------------------------------------------------------------
    def newVersion = "v${major}.${minor}.${patch}"
    echo "🔖 Current tag: ${currentTag} → New tag: ${newVersion}"

    return newVersion
}
