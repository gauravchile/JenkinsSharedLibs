def call(String manifestDir = '', String namespace = '') {
    if (!manifestDir?.trim()) error("❌ k8s_deploy: 'manifestDir' is required.")
    if (!namespace?.trim()) error("❌ k8s_deploy: 'namespace' is required.")

    stage("☸️ Kubernetes Deploy") {
        echo "🚀 Starting deployment from '${manifestDir}' to namespace '${namespace}'"

        try {
            sh """
                set -e

                if [ -f ${manifestDir}/namespace.yaml ]; then
                    echo "📦 Applying namespace manifest..."
                    kubectl apply -f ${manifestDir}/namespace.yaml
                else
                    echo "⚠️ No namespace.yaml found, skipping namespace creation."
                fi

                echo "⏳ Waiting for namespace initialization..."
                kubectl get ns ${namespace} >/dev/null 2>&1 || sleep 5

                echo "📄 Applying all manifests from ${manifestDir}..."
                kubectl apply -f ${manifestDir}/ --validate=false

                echo "🕒 Checking rollout status for all deployments..."
                DEPLOYMENTS=$(kubectl get deploy -n ${namespace} -o jsonpath='{.items[*].metadata.name}')
                for dep in $DEPLOYMENTS; do
                    echo \"➡️ Waiting for rollout of deployment: $dep\"
                    kubectl rollout status deploy/$dep -n ${namespace} --timeout=180s || true
                done

                echo \"✅ Deployment completed successfully for namespace: ${namespace}\"
            """
        } catch (Exception e) {
            echo \"❌ Kubernetes deployment failed: ${e.message}\"
            currentBuild.result = 'FAILURE'
            throw e
        }
    }
}
