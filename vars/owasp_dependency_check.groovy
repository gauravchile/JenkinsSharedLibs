def call(Map config = [:]) {
    // Default values if not passed from the pipeline
    def scanPath   = config.get('scanPath', '.')
    def reportDir  = config.get('reportDir', "${env.WORKSPACE}/reports")
    def project    = config.get('project', 'DependencyCheck-Project')

    stage("📦 SCA - OWASP Dependency Check (${project})") {
        echo "🔍 Running OWASP Dependency-Check..."
        sh """
            mkdir -p ${reportDir}
            docker run --rm \
                -v "${WORKSPACE}:/src" \
                -v "${reportDir}:/report" \
                owasp/dependency-check:latest \
                --scan /src/${scanPath} \
                --format JSON \
                --out /report \
                --project ${project} || true
        """
        echo "✅ OWASP Dependency Check complete for ${project}. Reports stored in ${reportDir}"
    }
}
