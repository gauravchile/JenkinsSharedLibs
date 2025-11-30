/**
 * Pushes a Docker image to the registry.
 * Usage:
 *   docker_push('docker.io/user/app', 'v1.0.0')
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
 * Internal map-style overload.
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
                docker push ${imageName}:${imageTag}
                ${pushLatest ? "docker tag ${imageName}:${imageTag} ${imageName}:latest && docker push ${imageName}:latest" : ""}
                docker logout
            """
        }

        echo "✅ Successfully pushed ${imageName}:${imageTag}${pushLatest ? ' and latest' : ''}."
    }
}
