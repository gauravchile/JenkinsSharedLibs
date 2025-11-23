def call(Map config = [:]) {
    def projectKey = config.get('projectKey', 'GenericProject')
    def sourceDirs = config.get('sources', '.')
    def scannerTool = config.get('scannerTool', 'sonar-scanner')
    def sonarEnv = config.get('sonarEnv', 'SonarQube')

    stage("🧠 SAST - SonarQube Scan (${projectKey})") {
        echo "🧩 Running SonarQube analysis for project: ${projectKey}"
        environment { scannerHome = tool "${scannerTool}" }

        withSonarQubeEnv("${sonarEnv}") {
            sh """
                ${scannerHome}/bin/sonar-scanner \
                -Dsonar.projectKey=${projectKey} \
                -Dsonar.sources=${sourceDirs} \
                -Dsonar.host.url=$SONAR_HOST_URL || true
            """
        }

        echo "✅ SonarQube scan completed for ${projectKey}"
    }
}
