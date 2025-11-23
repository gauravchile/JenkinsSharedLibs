def call(String dir = 'reports') {
    stage('Generate Reports') {
        sh "mkdir -p ${dir}"
        junit '**/test-results/*.xml'
    }
}
