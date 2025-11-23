def call(String command = 'eslint .') {
    stage('Lint Code') {
        echo "🧹 Running lint check..."
        sh command
    }
}
