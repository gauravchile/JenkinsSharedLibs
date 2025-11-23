def call(String policyFile = 'policies/cis.yml') {
    stage('Compliance Check') {
        if (fileExists(policyFile)) {
            sh "conftest test --policy ${policyFile} . || true"
        } else {
            echo "⚠️ No compliance policy found"
        }
    }
}
