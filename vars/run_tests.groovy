def call(String testCommand = 'npm test') {
    stage('Run Tests') {
        try {
            sh testCommand
            echo "✅ Tests passed successfully"
        } catch (e) {
            echo "❌ Tests failed!"
            currentBuild.result = 'FAILURE'
            throw e
        }
    }
}
