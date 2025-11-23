def call(String repoUrl, String branch = 'main', String credentialsId = '') {
    stage('Clone Repository') {
        echo "📥 Cloning ${repoUrl} (branch: ${branch})"
        def cfg = [
            $class: 'GitSCM',
            branches: [[name: branch]],
            userRemoteConfigs: [[url: repoUrl]]
        ]
        if (credentialsId?.trim()) cfg.userRemoteConfigs[0].credentialsId = credentialsId
        checkout(cfg)
    }
}
