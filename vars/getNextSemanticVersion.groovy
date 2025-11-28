def call(Map config = [:]) {
    def autoTag = config.get('autoTag', false)

    def latestTag = sh(script: "git describe --tags --abbrev=0 || echo 'v1.0.0'", returnStdout: true).trim()
    def commitMsg = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()

    def version = latestTag.replace("v", "")
    def (major, minor, patch) = version.tokenize('.').collect { it.toInteger() }

    def bump = "patch"
    if (commitMsg =~ /(?i)(BREAKING CHANGE|major:)/) bump = "major"
    else if (commitMsg =~ /(?i)(feat|feature:)/)     bump = "minor"
    else if (commitMsg =~ /(?i)(fix|patch:)/)        bump = "patch"

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

    if (autoTag) {
        sh """
            git config user.name "Jenkins CI"
            git config user.email "ci@jenkins.local"
            git tag -a ${newTag} -m "Auto version bump: ${newTag}"
            git push origin ${newTag} || true
        """
        echo "✅ Git tagged with ${newTag}"
    }

    // Expose for downstream stages
    env.PREVIOUS_TAG = latestTag
    env.BUMP_TYPE    = bump
    env.VERSION_MODE = 'auto'

    return newTag
}
