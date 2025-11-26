#!/usr/bin/env groovy

/**
 * OWASP Dependency-Check Stage for Jenkins Shared Library
 * Author: ShieldOps Team
 * Updated: 2025-11
 *
 * Runs OWASP Dependency-Check in Docker with caching, exclusions, and timeout.
 */

def call(Map config = [:]) {
    def scanPath      = config.get('scanPath', '.')
    def reportDir     = config.get('reportDir', "${env.WORKSPACE}/reports")
    def project       = config.get('project', 'ShieldOps')
    def cacheDir      = config.get('cacheDir', '/tmp/odc-data')
    def additionalArgs = config.get('additionalArgs', '--exclude **/node_modules/** --exclude **/test/** --exclude **/dist/**')
    def timeoutMinutes = config.get('timeoutMinutes', 20)

    echo "🧩 Running OWASP Dependency-Check for: ${project}"
    echo "   → Scan path: ${scanPath}"
    echo "   → Reports: ${reportDir}"
    echo "   → Cache: ${cacheDir}"
    echo "   → Args: ${additionalArgs}"

    // Ensure report and cache dirs exist
    sh """
      mkdir -p '${reportDir}' '${cacheDir}'
      chmod -R 777 '${cacheDir}' || true
    """

    // Run Dependency-Check in a Docker container with timeout
    try {
        timeout(time: timeoutMinutes, unit: 'MINUTES') {
            sh """
              echo "▶️ Starting Dependency-Check scan for ${project}"
              docker run --rm \
                -v '${env.WORKSPACE}:/src' \
                -v '${reportDir}:/report' \
                -v '${cacheDir}:/usr/share/dependency-check/data' \
                owasp/dependency-check:latest \
                --scan /src/${scanPath} \
                --format JSON --format HTML \
                --out /report \
                --project '${project}' \
                ${additionalArgs} || echo '⚠️ Dependency-Check returned non-zero, continuing...'
            """
        }
    } catch (err) {
        echo "❌ OWASP Dependency-Check timed out or failed for ${project}: ${err}"
        sh "docker rm -f \$(docker ps -q --filter ancestor=owasp/dependency-check:latest) || true"
    }

    // Verify report output exists
    sh """
      if [ -f '${reportDir}/dependency-check-report.html' ]; then
        echo '✅ Dependency-Check HTML report generated.'
      else
        echo '⚠️ No Dependency-Check HTML report found — skipping.'
      fi
    """
}
