def call(String imageName, String registry) {
    stage('Docker Push') {
        sh """
            docker tag ${imageName}:latest ${registry}/${imageName}:latest
            docker push ${registry}/${imageName}:latest
        """
    }
}
