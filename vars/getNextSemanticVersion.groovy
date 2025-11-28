def call(Map config = [:]) {
    // Optional config parameters
    def defaultStart = config.get('defaultStart', 'v1.0.0')
    def showSummary  = config.get('showSummary', true)

    // Fetch latest tag and last commit message
    def latestTag = sh(script: "git describe --tags --abbrev=0 || echo '${defaultStart}'", returnStdout: true).trim()
    def commitMsg = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()

    // Parse version numbers
    def version = latestTag.replace("v", "")
    def (major, minor, patch) = version.tokenize('.').collect { it.toInteger() }

    // Default bump = patch
    def bumpType = "patch"

    // Determine bump type from commit message
    if (commitMsg =~ /(?i)(BREAKING CHANGE|major:)/) {
        bumpType = "major"
    } else if (commitMsg =~ /(?i)(feat|feature:)/) {
        bumpType = "minor"
    } else if (commitMsg =~ /(?i)(fix|patch:)/) {
        bumpType = "patch"
    } else {
        echo "⚠️  No semantic keyword found. Defaulting to PATCH bump."
    }

    // Increment version
    switch (bumpType) {
        case "major":
            major += 1; minor = 0; patch = 0; break
        case "minor":
            minor += 1; patch = 0; break
        default:
            patch += 1
    }

    def newVersion = "v${major}.${minor}.${patch}"

    if (showSummary) {
        echo """
        -----------------------------
        Commit Message : ${commitMsg}
        Detected Bump   : ${bumpType}
        Previous Tag    : ${latestTag}
        New Version     : ${newVersion}
        -----------------------------
        """
    }

    // Optionally push tag if requested
    if (config.get('autoTag', false)) {
        sh """
          git tag ${newVersion}
          git push origin ${newVersion}
        """
        echo "✅ New tag pushed: ${newVersion}"
    }

    return newVersion
}
