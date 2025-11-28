def call(Map config = [:]) {
    /*
      getVersionTag.groovy
      --------------------
      Universal version handler for Jenkins pipelines.
      Supports manual and auto semantic versioning.
      Optionally creates and pushes a Git tag.

      Parameters:
        manualTag   - (String) optional manual version tag (e.g. 'v2.1.0')
        autoGitTag  - (Boolean) whether to auto-tag Git (default: false)
        gitCred     - (String) Jenkins credential ID for SSH Git push
    */

    def manualTag  = config.get('manualTag', '')?.trim()
    def autoGitTag = config.get('autoGitTag', false)
    def gitCred    = config.get('gitCred', '')

    // 1️⃣ Manual version if provided
    if (manualTag) {
        echo "Mode: Manual | Version Tag: ${manualTag}"
        return [tag: manualTag, mode: 'manual']
    }

    // 2️⃣ Auto Semantic Version
    def newTag = getNextSemanticVersion(autoTag: true)
    echo "Mode: Auto | Version Tag: ${newTag}"

    // 3️⃣ Optional Git tagging
    if (autoGitTag) {
        if (!gitCred) {
            error "❌ gitCred must be provided when autoGitTag = true"
        }
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

    return [tag: newTag, mode: 'auto']
}
