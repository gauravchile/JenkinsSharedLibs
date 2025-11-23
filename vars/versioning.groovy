def call(boolean includeSha = true) {
    stage('Versioning') {
        def date = sh(script: "date +%Y.%m.%d", returnStdout: true).trim()
        def sha = sh(script: "git rev-parse --short HEAD || echo no-git", returnStdout: true).trim()
        def ver = "v${date}.${env.BUILD_NUMBER}"
        if (includeSha && sha != 'no-git') ver += "-${sha}"
        echo "🏷️ Build Version: ${ver}"
        return ver
    }
}
