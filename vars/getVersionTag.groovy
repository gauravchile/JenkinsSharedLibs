def call(Map config = [:]) {
    def manualTag  = config.get('manualTag', '')?.trim()
    def autoGitTag = config.get('autoGitTag', false)
    def gitCred    = config.get('gitCred', '')

    if (manualTag) {
        env.VERSION_MODE = 'manual'
        env.PREVIOUS_TAG = sh(script: "git describe --tags --abbrev=0 || echo 'v1.0.0'", returnStdout: true).trim()
        env.BUMP_TYPE    = 'manual'
        echo "Mode: Manual | Version Tag: ${manualTag}"
        return [tag: manualTag, mode: 'manual']
    }

    def newTag = getNextSemanticVersion(autoTag: true)
    echo "Mode: Auto | Version Tag: ${newTag}"

    if (autoGitTag) {
        if (!gitCred) error "❌ gitCred must be provided when autoGitTag = true"
        echo "Creating Git tag ${newTag}..."
        sshagent (credentials: [gitCred]) {
            sh """
                git config user.name "Jenkins CI"
                git config user.email "ci@jenkins.local"
                git tag -a ${newTag} -m "Automated release: ${newTag}"
                git push origin ${newTag}
            """
        }
        echo "✅ Git tag ${newTag} pushed successfully."
    }

    env.VERSION_MODE = 'auto'
    return [tag: newTag, mode: 'auto']
}
