def call(String dir = 'infra') {
    stage('Terraform Apply') {
        dir(dir) {
            sh "terraform init && terraform apply -auto-approve"
        }
    }
}
