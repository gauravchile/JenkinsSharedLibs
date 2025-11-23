def call(String imageName, String tag = 'latest', String registry = '', String credentialsId = 'dockerhub-creds') {
    stage("Docker Push: ${imageName}:${tag}") {
        def fullImage = registry ? "${registry}/${imageName}:${tag}" : "${imageName}:${tag}"
        echo "📤 Preparing to push Docker image → ${fullImage}"

        withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
            sh '''
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            '''
            sh "docker push ${fullImage}"
            sh "docker logout"
        }

        echo "✅ Successfully pushed ${fullImage}"
    }
}
