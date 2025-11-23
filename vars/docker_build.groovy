def call(String imageName, String tag = 'latest', String registry = '') {
    stage("Docker Build: ${imageName}:${tag}") {
        def fullImage = registry ? "${registry}/${imageName}:${tag}" : "${imageName}:${tag}"
        echo "🐳 Building Docker image → ${fullImage}"
        sh """
            docker build -t ${fullImage} .
        """
        echo "✅ Successfully built ${fullImage}"
    }
}
