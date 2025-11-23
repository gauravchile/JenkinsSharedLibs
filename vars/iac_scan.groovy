def call(String dir = '.') {
    stage('IaC Scan') {
        sh "tfsec ${dir} || true"
    }
}
