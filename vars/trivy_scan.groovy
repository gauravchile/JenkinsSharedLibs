def call(String imageName) {
    stage('Trivy Scan') {
        sh """
            trivy image --severity HIGH,CRITICAL --exit-code 0 --no-progress ${imageName} > trivy-report.txt
        """
        archiveArtifacts artifacts: 'trivy-report.txt'
    }
}
