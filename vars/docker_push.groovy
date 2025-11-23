/**
 * docker_push.groovy
 * Universal reusable Docker push helper.
 *
 * Parameters (Map):
 *   imageName   - Required: Full image name (e.g. docker.io/user/app)
 *   imageTag    - Optional: Image tag (default: 'latest')
 *   credentials - Optional: Jenkins credentials ID (default: 'dockerhub-creds')
 */

def call(Map config = [:]) {
    def imageName   = config.imageName ?: error("❌ docker_push: 'imageName' is required")
    def imageTag    = config.imageTag ?: 'latest'
    def credentials = config.credentials ?: 'dockerhub-creds'

    stage("📤 Push: ${imageName}:${imageTag}") {
        echo "📦 Pushing Docker image → ${imageName}:${imageTag}"

        withCredentials([usernamePassword(
            credentialsId: credentials,
            usernameVariable: 'DOCKER_USERNAME',
            passwordVariable: 'DOCKER_PASSWORD'
        )]) {
            sh """
                echo "\$DOCKER_PASSWORD" | docker login -u "\$DOCKER_USERNAME" --password-stdin
                docker push ${imageName}:${imageTag}
                docker push ${imageName}:latest
                docker logout
            """
        }

        echo "✅ Successfully pushed ${imageName}:${imageTag} and latest."
    }
}
