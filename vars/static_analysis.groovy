def call(String sonarProjectKey) {
    stage('Static Analysis') {
        withSonarQubeEnv('SonarQube') {
            sh "sonar-scanner -Dsonar.projectKey=${sonarProjectKey}"
        }
    }
}
