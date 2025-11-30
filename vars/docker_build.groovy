/**
 * Builds a Docker image with optional Docker Hub authentication.
 * Usage:
 *   docker_build('docker.io/user/app', 'v1.0.0', '--build-arg BUILD_COLOR=blue')
 */
def call(String imageName, String imageTag = 'latest', String buildArgs = '', String credentials = 'dockerhub-creds') {
    call([
        imageName  : imageName,
        imageTag   : imageTag,
        context    : '.',
        dockerfile : 'Dockerfile',
        buildArgs  : buildArgs,
        noCache    : false,
        credentials: credentials
    ])
}

/**
 * Internal map-style overload.
 */
def call(Map config = [:]) {
    def imageName   = config.imageName ?: error("❌ docker_build: 'imageName' is required")
    def imageTag    = config.imageTag ?: 'latest'
    def context     = config.context ?: '.'
    def dockerfile  = config.dockerfile ?: 'Dockerfile'
    def buildArgs   = config.buildArgs ?: ''
    def noCacheOpt  = config.get('noCache', false) ? '--no-cache' : ''
    def credentials = config.credentials ?: 'dockerhub-creds'

    stage("Build Docker Image: ${imageName}:${imageTag}") {
        echo "🏗️ Building Docker image → ${imageName}:${imageTag}"
        echo "📂 Context: ${context}"
        echo "🧱 Dockerfile: ${dockerfile}"
        if (buildArgs) echo "⚙️ Build args: ${buildArgs}"

        // 🔐 Login to Docker Hub before build to avoid 401 errors
        withCredentials([usernamePassword(
            credentialsId: credentials,
            usernameVariable: 'DOCKER_USERNAME',
            passwordVariable: 'DOCKER_PASSWORD'
        )]) {
            sh '''
                echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                docker build ${noCacheOpt} \
                  -t ${imageName}:${imageTag} \
                  -f ${dockerfile} ${buildArgs} ${context}
                docker logout
            '''
        }

        echo "✅ Successfully built ${imageName}:${imageTag}"
    }
}
