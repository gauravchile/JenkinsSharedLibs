def call(Map config = [:]) {
    def manifestDir = config.manifestDir ?: error("❌ update_k8s_manifests: 'manifestDir' is required.")
    def imageTag    = config.imageTag ?: error("❌ update_k8s_manifests: 'imageTag' is required.")
    def imageName   = config.imageName ?: ''

    stage("☸️ Update K8s Manifests") {
        echo "📄 Updating image tags in ${manifestDir} to :${imageTag}"

        if (imageName) {
            // Replace tag only for a specific image name
            sh """
                find ${manifestDir} -type f -name '*.yaml' -exec \
                sed -i 's|${imageName}:.*|${imageName}:${imageTag}|g' {} +
            """
        } else {
            // Replace all :latest tags
            sh """
                find ${manifestDir} -type f -name '*.yaml' -exec \
                sed -i 's|:latest|:${imageTag}|g' {} +
            """
        }

        echo "✅ Kubernetes manifests updated successfully with tag: ${imageTag}"
    }
}
