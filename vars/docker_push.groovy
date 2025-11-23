def call(String imageName, String tag = 'latest', String registry = '') {
    stage("Docker Push: ${imageName}:${tag}") {
        def fullImage = registry ? "${registry}/${imageName}:${tag}" : "${imageName}:${tag}"
        echo "📤 Pushing Docker image → ${fullImage}"
        sh """
            docker push ${fullImage}
        """
        echo "✅ Successfully pushed ${fullImage}"
    }
}
