def call(String reportDir = 'coverage') {
    stage('Code Coverage') {
        sh "mkdir -p ${reportDir}"
        junit '**/test-results/*.xml'
        publishHTML(target: [reportDir: reportDir, reportFiles: 'index.html', reportName: 'Coverage Report'])
    }
}
