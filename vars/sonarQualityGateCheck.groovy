def call(Map config = [:]) {
    def timeoutMinutes = config.get('timeoutMinutes', 2)

    stage('SonarQube Quality Gate') {
        script {
            timeout(time: timeoutMinutes, unit: 'MINUTES') {
                def qg = waitForQualityGate()
                env.SONARQUBE_STATUS = qg.status

                if (qg.status != 'OK') {
                    error "SonarQube Quality Gate failed: ${qg.status}"
                }

                echo "SonarQube Quality Gate passed: ${qg.status}"
            }
        }
    }
}
