def call(Map config = [:]) {
    def imageName  = config.imageName ?: error("❌ docker_build: 'imageName' is required")
    def imageTag   = config.imageTag ?: 'latest'
    def context    = config.context ?: '.'
    def dockerfile = config.dockerfile ?: 'Dockerfile'
    def buildArgs  = config.buildArgs ?: ''
    def noCacheOpt = config.get('noCache', false) ? '--no-cache' : ''

    echo "🏗️ Building Docker image"
    echo "  → Image: ${imageName}:${imageTag}"
    echo "  → Dockerfile: ${dockerfile}"
    echo "  → Context: ${context}"
    if (buildArgs) echo "  → Build Args: ${buildArgs}"

    sh """
        set -e
        docker build ${noCacheOpt} \
          -t "${imageName}:${imageTag}" \
          -f "${dockerfile}" ${buildArgs} "${context}"
    """

    echo "✅ Successfully built ${imageName}:${imageTag}"
}
