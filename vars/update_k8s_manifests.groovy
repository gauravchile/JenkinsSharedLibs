def call(String manifestDir = 'k8s', String imageTag = 'latest') {
    stage('Update K8s Manifests') {
        sh "find ${manifestDir} -type f -name '*.yaml' -exec sed -i 's|:latest|:${imageTag}|g' {} +"
    }
}
