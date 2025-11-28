/**
 * docker_build.groovy
 * -------------------
 * Universal reusable Docker build helper for Jenkins pipelines.
 *
 * Usage:
 *   docker_build("<imageName>", "<contextPath>", "<imageTag>")
 *   OR
 *   docker_build(imageName: "<image>", context: "<dir>", imageTag: "<tag>", buildArgs: "--build-arg KEY=value")
 *
 * Parameters:
 *   imageName   - Required: Full image name (e.g. docker.io/user/app)
 *   context     - Required: Build context or Dockerfile directory
 *   imageTag    - Optional: Tag (default: 'latest')
 *   buildArgs   - Optional: Additional build args (e.g., "--build-arg ENV=prod")
 *   dockerfile  - Optional: Custom Dockerfile path (default: Dockerfile in context)
 *   noCache     - Optional: Boolean (default: false)
 */

def call(String imageName, String context = '.', String imageTag = 'latest') {
    call([
        imageName : imageName,
        context   : context,
        imageTag  : imageTag,
        buildArgs : '',
        dockerfile: 'Dockerfile',
        noCache   : false
    ])
}

/**
 * Internal overload for map-style calls.
 */
def call(Map config = [:]) {
    def imageName  = config.imageName ?: error("❌ docker_build: 'imageName' is required")
    def context    = config.context ?: '.'
    def imageTag   = config.imageTag ?: 'latest'
    def buildArgs  = config.buildArgs ?: ''
    def dockerfile = config.dockerfile ?: 'Dockerfile'
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
