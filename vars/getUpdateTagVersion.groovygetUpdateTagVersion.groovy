def call(Map args = [:]) {
    // Read from pipeline params/env or defaults
    def yamlDir = args.yamlDir ?: (binding.hasVariable('params') && params.MANIFEST_DIR ? params.MANIFEST_DIR : detectManifestDir())
    def bumpType = args.bump ?: (binding.hasVariable('params') && params.BUMP ? params.BUMP : 'patch')

    echo "🧭 Scanning for Kubernetes/Helm manifests in: ${yamlDir}"

    // 1️⃣ Find relevant YAMLs (Kubernetes + Helm)
    def k8sFiles = sh(
        script: """
          find ${yamlDir} -type f \\( -name '*.yaml' -o -name '*.yml' \\) |
          grep -Ev '(docker-compose|skaffold|config)' |
          xargs grep -lE '(^kind: (Deployment|StatefulSet|DaemonSet|CronJob|Job|Pod)|^image:|values)' || true
        """,
        returnStdout: true
    ).trim().split('\n').findAll { it }

    if (!k8sFiles) error "No valid Kubernetes or Helm YAMLs found under ${yamlDir}"

    // 2️⃣ Extract current tag
    def imageLine = sh(
        script: "grep -E 'image:' ${k8sFiles[0]} | head -1 | awk '{print \$2}'",
        returnStdout: true
    ).trim()

    def currentTag = imageLine.tokenize(':')[-1].replace('v','')
    def parts = currentTag.tokenize('.')
    if (parts.size() < 3) error "Invalid SemVer format: ${currentTag}"

    def (major, minor, patch) = parts.collect { it.toInteger() }

    // 3️⃣ Bump version
    switch (bumpType) {
        case 'major': major++; minor = 0; patch = 0; break
        case 'minor': minor++; patch = 0; break
        default: patch++
    }

    def newTag = "v${major}.${minor}.${patch}"
    echo "Version bumped: ${currentTag} → ${newTag}"

    // 4️⃣ Update all valid YAMLs
    k8sFiles.each { file ->
        sh """
          if grep -q 'image:' ${file}; then
            sed -i "s|image: .*|image: docker.io/gauravchile/edgewave:${newTag}|" ${file}
          fi
        """
    }

    // 5️⃣ Automatically set Jenkins build name
    currentBuild.displayName = "#${env.BUILD_NUMBER} - ${newTag}"
    echo "✅ Version: ${newTag} (scanned from ${yamlDir})"

    return newTag
}

// 🔍 Auto-detect manifest directory
def detectManifestDir() {
    def found = sh(
        script: "find . -type d -name 'manifests' -o -name 'k8s' -o -name 'deploy' | head -1",
        returnStdout: true
    ).trim()
    if (!found) error "Cannot auto-detect manifest directory — pass yamlDir or define params.MANIFEST_DIR"
    return found
}
