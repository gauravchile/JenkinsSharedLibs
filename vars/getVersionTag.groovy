def call(String manualTag = '') {
    // Trim input safely
    manualTag = manualTag?.trim()

    // If a manual tag is given, use it directly
    if (manualTag) {
        echo "Mode: Manual | Version Tag: ${manualTag}"
        return manualTag
    }

    // Otherwise, use auto-semantic version bump
    def newTag = getNextSemanticVersion(autoTag: true)
    echo "Mode: Auto | Version Tag: ${newTag}"
    return newTag
}
