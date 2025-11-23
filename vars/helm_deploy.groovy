/**
 * helm_deploy.groovy
 * Universal Helm-based Kubernetes deployment helper.
 *
 * Parameters (Map):
 *   chartDir     - Required: Path to Helm chart (e.g. ./helm/ShieldOps)
 *   releaseName  - Optional: Helm release name (default: 'app-release')
 *   namespace    - Required: Kubernetes namespace
 *   imageRepo    - Required: Docker image repository (e.g. docker.io/user/app)
 *   imageTag     - Required: Docker image tag (e.g. backend-123)
 *   uiImageRepo  - Optional: Separate repo for UI image (if applicable)
 *   uiImageTag   - Optional: Separate tag for UI image (if applicable)
 *   timeout      - Optional: Helm wait timeout (default: 180s)
 */

def call(Map config = [:]) {
    def chartDir    = config.chartDir ?: error("❌ helm_deploy: 'chartDir' is required.")
    def releaseName = config.releaseName ?: 'app-release'
    def namespace   = config.namespace ?: error("❌ helm_deploy: 'namespace' is required.")
    def imageRepo   = config.imageRepo ?: error("❌ helm_deploy: 'imageRepo' is required.")
    def imageTag    = config.imageTag ?: error("❌ helm_deploy: 'imageTag' is required.")
    def uiImageRepo = config.uiImageRepo ?: imageRepo
    def uiImageTag  = config.uiImageTag ?: imageTag
    def timeout     = config.timeout ?: '180s'

    stage("☸️ Helm Deploy (${releaseName})") {
        echo "🚀 Deploying ${releaseName} via Helm in namespace '${namespace}'..."

        try {
            sh """
                helm upgrade --install ${releaseName} ${chartDir} \\
                  --namespace ${namespace} \\
                  --create-namespace \\
                  --set image.repository=${imageRepo} \\
                  --set image.tag=${imageTag} \\
                  --set ui.image.repository=${uiImageRepo} \\
                  --set ui.image.tag=${uiImageTag} \\
                  --wait --timeout ${timeout}
            """
            echo "✅ Helm deployment successful for ${releaseName} in namespace '${namespace}'!"
        } catch (Exception e) {
            echo "❌ Helm deployment failed: ${e.message}"
            currentBuild.result = 'FAILURE'
            throw e
        }
    }
}
