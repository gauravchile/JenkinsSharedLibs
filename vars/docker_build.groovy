def call(String imageName) {
    stage('Docker Build') {
        sh "docker build -t ${imageName}:latest ."
    }
}
