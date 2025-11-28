def call(Map config = [:]) {
    /*
      getNextSemanticVersion.groovy
      ------------------------------
      Auto-increments semantic version based on the latest Git tag
      and last commit message keywords:
        - "BREAKING CHANGE" or "major:" → MAJOR bump
        - "feat" or "feature:"          → MINOR bump
        - "fix" or "patch:"             → PATCH bump (default)

      Parameters:
        autoTag - (Boolean) optional, if true auto-tags Git (default: false)
    */

    def autoTag = config.get('autoTag', false)

    // 1️⃣ Get latest Git tag or default to v1.0.0
    def latestTag = sh(script: "git describe --tags --abbrev=0 || echo 'v1.0.0'", returnStdout: true).trim()
    def commitMsg = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()

    def version = latestTag.replace("v", "")
    def (major, minor, patch) = version.tokenize('.').collect { it.toInteger() }

    // 2️⃣ Determine bump type
    def bump = "patch"
    if (commitMsg =~ /(?i)(BREAKING CHANGE|major:)/) bump = "major"
    else if (commitMsg =~ /(?i)(feat|feature:)/)     bump = "minor"
    else if (commitMsg =~ /(?i)(fix|patch:)/)        bump = "patch"
    else echo "⚠️  No semantic keyword found in commit. Defaulting to PATCH bump."

    // 3️⃣ Increment version
    switch (bump) {
        case "major": major++; minor = 0; patch = 0; break
        case "minor": minor++; patch = 0; break
        default: patch++
    }

    def newTag = "v${major}.${minor}.${patch}"

    echo """
    ──────────────────────────────
    Commit Message : ${commitMsg}
    Detected Bump   : ${bump.toUpperCase()}
    Previous Tag    : ${latestTag}
    New Version     : ${newTag}
    ──────────────────────────────
    """

    // 4️⃣ Optionally tag Git
    if (autoTag) {
        sh """
            git config user.name "Jenkins CI"
            git config user.email "ci@jenkins.local"
            git tag -a ${newTag} -m "Auto version bump: ${newTag}"
            git push origin ${newTag} || true
        """
        echo "✅ Git tagged with ${newTag}"
    }

    return newTag
}
