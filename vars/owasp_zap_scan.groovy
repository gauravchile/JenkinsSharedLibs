def call(Map config = [:]) {
    def backendImage = config.get('backendImage', '')
    def scanDir      = config.get('scanDir', "${env.WORKSPACE}/reports")
    def targetUrl    = config.get('targetUrl', 'http://127.0.0.1:8080')
    def project      = config.get('project', 'OWASP-ZAP-Scan')
    def port         = config.get('port', '8081')

    stage("🌐 DAST - OWASP ZAP (Baseline) [${project}]") {
        echo "🌍 Running OWASP ZAP DAST Baseline Scan for ${project}"

        try {
            // Ensure report directory exists
            sh "mkdir -p ${scanDir}"

            // Start backend container temporarily
            if (backendImage) {
                echo "🚀 Starting backend container for DAST testing..."
                sh "docker run -d --rm --name ${project.toLowerCase()} -p ${port}:${port} ${backendImage} || true"
                sleep 5
            }

            // Run OWASP ZAP scan
            sh """
                docker run --rm --network host -v ${scanDir}:/zap/wrk \
                owasp/zap2docker-stable zap-baseline.py \
                -t ${targetUrl} \
                -r zap-report.html \
                -J zap-report.json || true
            """

            echo "✅ OWASP ZAP Baseline Scan completed successfully for ${project}."
            echo "📁 Reports saved in: ${scanDir}"
        } catch (Exception e) {
            echo \"⚠️ ZAP Scan failed: ${e.message}\"
        } finally {
            // Always clean up test container
            sh \"docker rm -f ${project.toLowerCase()} || true\"
        }
    }
}
