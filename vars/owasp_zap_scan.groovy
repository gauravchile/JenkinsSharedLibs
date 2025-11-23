def call(Map config = [:]) {
    def backendImage = config.get('backendImage', '')
    def scanDir      = config.get('scanDir', "${env.WORKSPACE}/reports")
    def targetUrl    = config.get('targetUrl', 'http://127.0.0.1:8080')
    def project      = config.get('project', 'OWASP-ZAP-Scan')

    stage("🌐 DAST - OWASP ZAP (Baseline)") {
        echo "🌍 Running OWASP ZAP Baseline DAST scan for ${project}"

        try {
            // Start the backend container
            sh """
                docker run -d --rm --name ${project.toLowerCase()} -p 8081:8081 ${backendImage} || true
            """

            // Run OWASP ZAP Baseline Scan
            sh """
                docker run --rm --network host -v ${scanDir}:/zap/wrk \
                    owasp/zap2docker-stable zap-baseline.py \
                    -t ${targetUrl} -r zap-report.html -J zap-report.json || true
            """

            echo "✅ ZAP DAST scan completed. Reports stored at: ${scanDir}"
        } catch (Exception e) {
            echo "⚠️ OWASP ZAP scan failed: ${e.message}"
        } finally {
            sh "docker rm -f ${project.toLowerCase()} || true"
        }
    }
}
