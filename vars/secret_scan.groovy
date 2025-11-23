def call(String dir = '.') {
    stage('Secret Scan') {
        sh "gitleaks detect --source=${dir} --report-format=json --report-path=gitleaks-report.json || true"
        archiveArtifacts artifacts: 'gitleaks-report.json'
    }
}
