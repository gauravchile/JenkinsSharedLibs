def call(Map args = [:]) {
    stage('Checkout Code') {
        def repo = args.repo ?: env.GIT_URL
        def branch = args.branch ?: 'main'
        retry(2) {
            checkout([$class: 'GitSCM',
                branches: [[name: branch]],
                extensions: [[$class: 'CloneOption', shallow: true, depth: 10]],
                userRemoteConfigs: [[url: repo]]
            ])
        }
    }
}
