/**
 * docker_push.groovy
 * -------------------
 * Universal reusable Docker image push helper for Jenkins pipelines.
 *
 * Usage:
 *   docker_push("<imageName>", "<imageTag>")
 *   OR
 *   docker_push("<imageName>", "<imageTag>", "<credentialsId>")
 *
 * Defaults:
 *   credentialsId = 'dockerhub-creds'
 *   pushLatest = true
 */

def call(String imageName, String imageTag = 'latest', String credentials = 'dockerhub-creds') {
    call([
        imageName  : imageName,
        imageTag   : imageTag,
        credentials: credentials,
        pushLatest : true
    ])
}

/**
 * Internal overload for map-style calls.
 * Supports both positional and map-based invocation.
 */
def call(Map config = [:]) {
    def imageName   = config.imageName ?: error("❌ docker_push: 'imageName' is required")
    def imageTag    = config.imageTag ?: 'latest'
    def credentials = config.credentials ?: 'dockerhub-creds'
    def pushLatest  = config.get('pushLatest', true)

    stage("Push Docker Image: ${imageName}:${imageTag}") {
        echo "🚀 Preparing to push → ${imageName}:${imageTag}"

        withCredentials([usernamePassword(
            credentialsId: credentials,
            usernameVariable: 'DOCKER_USERNAME',
            passwordVariable: 'DOCKER_PASSWORD'
        )]) {
            sh """
                echo "\$DOCKER_PASSWORD" | docker login -u "\$DOCKER_USERNAME" --password-stdin
                echo "Pushing ${imageName}:${imageTag} ..."
                docker push ${imageName}:${imageTag}
                ${pushLatest ? "docker tag ${imageName}:${imageTag} ${imageName}:latest && docker push ${imageName}:latest" : ""}
                docker logout
            """
        }

        echo "✅ Successfully pushed ${imageName}:${imageTag}${pushLatest ? ' and latest' : ''}."
    }
}
