def call(String release, String chart, String namespace = 'default') {
    stage('Helm Deploy') {
        sh "helm upgrade --install ${release} ${chart} -n ${namespace} --wait"
    }
}
