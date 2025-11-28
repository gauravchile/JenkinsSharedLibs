def call(Map config = [:]) {
    /*
      sonarQualityGateCheck.groovy
      -----------------------------
      Dynamic SonarQube Quality Gate handler:
        - Fails pipeline on main/release branches.
        - Marks build UNSTABLE (non-blocking) on feature/dev branches.
        - Logs all results and sets env.SONARQUBE_STATUS.
    */

    def timeoutMinutes = config.get('timeoutMinutes', 2)

    stage('SonarQube Quality Gate') {
        script {
            timeout(time: timeoutMinutes, unit: 'MINUTES') {
                def qg = waitForQualityGate()
                env.SONARQUBE_STATUS = qg.status

                def branch = env.BRANCH_NAME ?: sh(
                    script: "git rev-parse --abbrev-ref HEAD",
                    returnStdout: true
                ).trim()

                echo "Branch detected: ${branch}"
                echo "Quality Gate status: ${qg.status}"

                if (branch == 'main' || branch.startsWith('release/')) {
                    if (qg.status != 'OK') {
                        error "❌ SonarQube Quality Gate failed on ${branch}: ${qg.status}"
                    }
                    echo "✅ Quality Gate passed on ${branch}: ${qg.status}"
                } else if (branch.startsWith('feature/') || branch.startsWith('dev/')) {
                    if (qg.status != 'OK') {
                        echo "⚠️ Quality Gate warning on ${branch}: ${qg.status} (non-blocking)"
                        currentBuild.result = 'UNSTABLE'
                    } else {
                        echo "✅ Quality Gate passed on ${branch}: ${qg.status}"
                    }
                } else {
                    if (qg.status != 'OK') {
                        echo "⚠️ Quality Gate warning on unrecognized branch (${branch}): ${qg.status} (non-blocking)"
                        currentBuild.result = 'UNSTABLE'
                    } else {
                        echo "✅ Quality Gate passed: ${qg.status}"
                    }
                }
            }
        }
    }
}
