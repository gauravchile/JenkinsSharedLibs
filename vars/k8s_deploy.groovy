def call(String manifestDir = 'kubernetes/base', String namespace = 'default') {
    stage("Kubernetes Deploy") {
        echo "☸️ Deploying manifests from ${manifestDir} to namespace: ${namespace}"

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
                sleep 5

                echo "📦 Applying all Kubernetes manifests in ${manifestDir}..."
                kubectl apply -f ${manifestDir}/ --validate=false

                echo "🕒 Checking deployment rollout statuses..."
                DEPLOYMENTS=$(kubectl get deploy -n ${namespace} -o jsonpath='{.items[*].metadata.name}')
                for dep in $DEPLOYMENTS; do
                    echo "➡️ Waiting for deployment: $dep"
                    kubectl rollout status deploy/$dep -n ${namespace} --timeout=180s || true
                done

                echo "✅ Kubernetes deployment completed successfully for namespace: ${namespace}"
            """
        } catch (Exception e) {
            echo "❌ Kubernetes deployment failed: ${e.message}"
            currentBuild.result = 'FAILURE'
            throw e
        }
    }
}
