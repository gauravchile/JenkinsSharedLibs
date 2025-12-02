// vars/getUpdateTagVersion.groovy
def call(Map args = [:]) {
    def yamlDir = args.get('yamlDir', 'helm/autoscalex')
    echo "🧭 Scanning manifests in: ${yamlDir}"

    // --- Step 1: Detect last image tag from values.yaml or templates
    def currentTag = sh(
        script: "grep -E 'image:' ${yamlDir}/*.yaml | head -1 | awk '{print \$2}' | cut -d':' -f2",
        returnStdout: true
    ).trim()

    def sanitized = currentTag.replaceAll('[^0-9v\\.]', '')
    if (!sanitized) sanitized = 'v0.0.0'

    // --- Step 2: Split current version into major.minor.patch
    def parts = sanitized.replace('v', '').tokenize('.')
    while (parts.size() < 3) { parts << '0' }

    def major = parts[0].isInteger() ? parts[0].toInteger() : 0
    def minor = parts[1].isInteger() ? parts[1].toInteger() : 0
    def patch = parts[2].isInteger() ? parts[2].toInteger() : 0

    // --- Step 3: Read last commit message for semantic keyword
    def commitMsg = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
    echo "📝 Last commit message: '${commitMsg}'"

    // --- Step 4: Determine version bump type
    if (commitMsg =~ /(?i)breaking change|!/) {
        major++
        minor = 0
        patch = 0
        echo "🔺 Detected MAJOR bump (breaking change)"
    } else if (commitMsg =~ /(?i)^feat:/) {
        minor++
        patch = 0
        echo "🔹 Detected MINOR bump (feature)"
    } else if (commitMsg =~ /(?i)^fix:/) {
        patch++
        echo "🔸 Detected PATCH bump (fix)"
    } else {
        patch++
        echo "⚪ Default PATCH bump (no semantic keyword found)"
    }

    // --- Step 5: Generate new version tag
    def newVersion = "v${major}.${minor}.${patch}"
    echo "🔖 Current tag: ${currentTag} → New tag: ${newVersion}"

    // --- Step 6: Return new version string
    return newVersion
}
