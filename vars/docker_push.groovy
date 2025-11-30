def call(Map config = [:]) {
    def imageName   = config.imageName ?: error("❌ docker_push: 'imageName' is required")
    def imageTag    = config.imageTag ?: 'latest'
    def credentials = config.credentials ?: 'dockerhub-creds'
    def pushLatest  = config.get('pushLatest', true)

    stage("Push Docker Image: ${imageName}:${imageTag}") {
        echo "🚀 Pushing → ${imageName}:${imageTag}"

        withCredentials([usernamePassword(
            credentialsId: credentials,
            usernameVariable: 'DOCKER_USERNAME',
            passwordVariable: 'DOCKER_PASSWORD'
        )]) {
            sh """
                echo "\$DOCKER_PASSWORD" | docker login -u "\$DOCKER_USERNAME" --password-stdin
                docker push "${imageName}:${imageTag}"
                ${pushLatest ? "docker tag ${imageName}:${imageTag} ${imageName}:latest && docker push ${imageName}:latest" : ""}
                docker logout
            """
        }

        echo "✅ Pushed ${imageName}:${imageTag}"
    }
}
