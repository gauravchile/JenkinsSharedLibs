stage('☸️ Update Kubernetes Manifests') {
  steps {
    script {
      update_k8s_manifests(
        manifestDir: 'kubernetes/base',
        imageTag: "backend-${BUILD_NUMBER}",
        imageName: 'shieldops'
      )
    }
  }
}
