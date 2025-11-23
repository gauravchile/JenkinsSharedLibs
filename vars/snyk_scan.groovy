def call(String dir = '.') {
    stage('Snyk Scan') {
        sh "snyk test --file=${dir}/package.json || true"
    }
}
