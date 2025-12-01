#!/usr/bin/env groovy
/**
 * collectSonarReport.groovy
 *
 * Universal helper to collect SonarQube metrics via REST API,
 * save summary files, and archive them in Jenkins.
 *
 * Usage (inside Jenkinsfile):
 *   collectSonarReport(
 *     sonarHost: 'http://localhost:9000',
 *     projectKey: 'edgewave',
 *     sonarCredId: 'sonar-token'
 *   )
 */

def call(Map config = [:]) {

    // Default configuration
    def sonarHost   = config.sonarHost   ?: error("Missing required param: sonarHost")
    def projectKey  = config.projectKey  ?: error("Missing required param: projectKey")
    def sonarCredId = config.sonarCredId ?: error("Missing required param: sonarCredId")

    // Metrics to collect
    def metrics = config.metrics ?: "bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density"

    echo "📄 [collectSonarReport] Collecting SonarQube metrics for project: ${projectKey}"

    withCredentials([string(credentialsId: sonarCredId, variable: 'SONAR_TOKEN')]) {
        def apiUrl = "${sonarHost}/api/measures/component?component=${projectKey}&metricKeys=${metrics}"

        sh """
          set -e
          curl -s -u ${SONAR_TOKEN}: "${apiUrl}" -o sonar-summary.json
        """

        // Convert JSON to readable summary
        sh '''
          if command -v jq >/dev/null 2>&1; then
            jq -r '.component.measures[] | "\\(.metric): \\(.value)"' sonar-summary.json > sonar-summary.txt
          else
            echo "jq not found — dumping raw JSON instead." > sonar-summary.txt
            cat sonar-summary.json >> sonar-summary.txt
          fi
        '''

        archiveArtifacts artifacts: 'sonar-summary.*', fingerprint: true

        echo "✅ [collectSonarReport] SonarQube summary collected and archived."
    }
}
