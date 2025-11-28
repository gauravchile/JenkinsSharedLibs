def call(Map config = [:]) {
    /*
      sonarQualityGateCheck.groovy
      -----------------------------
      Smart Quality Gate handler:
        - Fails pipeline on main/release branches.
        - Marks build UNSTABLE on feature/dev branches.
        - Stores gate status in env.SONARQUBE_STATUS.
    */

    def timeoutMinutes = config.get('timeoutMinutes', 2)

    stage('SonarQube Quality Gate') {
        script {
            timeout(time: timeoutMinutes, unit: 'MINUTES') {
                def qg = waitForQualityGate()
                env.SONARQUBE_STATUS = qg.status

                // Detect branch name (fallback if BRANCH_NAME not set)
                def branch = env.BRANCH_NAME ?: sh(
                    script: "git rev-parse --abbrev-ref HEAD",
                    returnStdout: true
                ).trim()

                echo "Branch detected: ${branch}"
                echo "Quality Gate status: ${qg.status}"

                // Production enforcement on main/release
                if (branch == 'main' || branch.startsWith('release/')) {
                    if (qg.status != 'OK') {
                        error "❌ SonarQube Quality Gate failed on ${branch}: ${qg.status}"
                    } else {
                        echo "✅ Quality Gate passed on ${branch}: ${qg.status}"
                    }
                }
                // Soft enforcement on feature/dev branches
                else if (branch.startsWith('feature/') || branch.startsWith('dev/')) {
                    if (qg.status != 'OK') {
                        echo "⚠️ Quality Gate warning on ${branch}: ${qg.status} (non-blocking)"
                        currentBuild.result = 'UNSTABLE'
                    } else {
                        echo "✅ Quality Gate passed on ${branch}: ${qg.status}"
                    }
                }
                // Default (if branch not matched)
                else {
                    if (qg.status != 'OK') {
                        echo "⚠️ Quality Gate warning on unknown branch (${branch}): ${qg.status} (non-blocking)"
                        currentBuild.result = 'UNSTABLE'
                    } else {
                        echo "✅ Quality Gate passed: ${qg.status}"
                    }
                }
            }
        }
    }
}
