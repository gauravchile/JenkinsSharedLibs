def call(String url) {
    stage('Health Check') {
        sh "curl -f ${url} || exit 1"
        echo "✅ Health check passed for ${url}"
    }
}
