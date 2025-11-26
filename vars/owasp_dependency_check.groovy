def call(Map config = [:]) {
    def scanPath   = config.get('scanPath', '.')
    def reportDir  = config.get('reportDir', "${env.WORKSPACE}/reports")
    def project    = config.get('project', 'DependencyCheck-Project')

    stage("📦 SCA - OWASP Dependency Check (${project})") {
        echo "🔍 Running OWASP Dependency-Check..."

        sh """
            mkdir -p ${reportDir}

            docker run --rm \
                -v "${WORKSPACE}:/src" \
                owasp/dependency-check:latest \
                --scan /src/${scanPath} \
                --format JSON --format HTML \
                --out /src/reports \
                --project "${project}" || true
        """

        echo "✅ OWASP Dependency Check complete for ${project}. Reports stored in ${reportDir}"
        sh "ls -lh ${reportDir} || true"
    }
}
