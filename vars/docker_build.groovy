def call(String imageName, String imageTag = 'latest', String buildArgs = '') {
    call([
        imageName : imageName,
        imageTag  : imageTag,
        context   : '.',
        dockerfile: 'Dockerfile',
        buildArgs : buildArgs,
        noCache   : false
    ])
}

/**
 * Internal overload for map-style calls.
 */
def call(Map config = [:]) {
    def imageName  = config.imageName ?: error("❌ docker_build: 'imageName' is required")
    def imageTag   = config.imageTag ?: 'latest'
    def context    = config.context ?: '.'
    def dockerfile = config.dockerfile ?: 'Dockerfile'
    def buildArgs  = config.buildArgs ?: ''
    def noCacheOpt = config.get('noCache', false) ? '--no-cache' : ''

    stage("Build Docker Image: ${imageName}:${imageTag}") {
        echo "🏗️ Building Docker image → ${imageName}:${imageTag}"
        echo "📂 Context: ${context}"
        echo "🧱 Dockerfile: ${dockerfile}"
        if (buildArgs) echo "⚙️ Build args: ${buildArgs}"

        sh """
            docker build ${noCacheOpt} \
              -t ${imageName}:${imageTag} \
              -f ${dockerfile} ${buildArgs} ${context}
        """

        echo "✅ Successfully built ${imageName}:${imageTag}"
    }
}
