def call(Map config = [:]) {
    def scanPath   = config.get('scanPath', '.')
    def reportDir  = config.get('reportDir', "${env.WORKSPACE}/reports")
    def project    = config.get('project', 'DependencyCheck-Project')
    // persistent cache on the Jenkins node:
    def cacheDir   = config.get('cacheDir', '/var/lib/jenkins/odc-cache')

    stage("SCA - OWASP Dependency Check (${project})") {
        echo "Running OWASP Dependency-Check with cache: ${cacheDir}"

        sh """
          mkdir -p ${reportDir} ${cacheDir}
          docker run --rm \
            -v "${WORKSPACE}:/src" \
            -v "${reportDir}:/report" \
            -v "${cacheDir}:/usr/share/dependency-check/data" \
            owasp/dependency-check:latest \
            --scan "/src/${scanPath}" \
            --format JSON --format HTML \
            --out /report \
            --project "${project}" || true
          ls -lh ${reportDir} || true
        """
        echo "OWASP Dependency Check complete for ${project}. Reports stored in ${reportDir}"
    }
}
