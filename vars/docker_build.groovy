def call(String imageName, String imageTag = 'latest', String context = '.', String dockerfile = 'Dockerfile') {
    stage("🐳 Build: ${imageName}:${imageTag}") {
        echo "📦 Building Docker image → ${imageName}:${imageTag}"
        echo "🧱 Context: ${context}, Dockerfile: ${dockerfile}"

        sh """
            docker build -t ${imageName}:${imageTag} -t ${imageName}:latest -f ${context}/${dockerfile} ${context}
        """

        echo "✅ Successfully built ${imageName}:${imageTag} and tagged as latest."
    }
}
