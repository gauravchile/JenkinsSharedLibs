def call(String dir = '/var/lib/jenkins/') {
    stage('Backup Configs') {
        def backupFile = "backup-${env.BUILD_NUMBER}.tar.gz"
        sh "tar -czf ${backupFile} ${dir}"
        archiveArtifacts artifacts: backupFile
    }
}
