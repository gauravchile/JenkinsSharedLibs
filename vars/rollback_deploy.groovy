def call(String namespace, String deployment) {
    stage('Rollback Deployment') {
        sh "kubectl rollout undo deployment/${deployment} -n ${namespace}"
    }
}
