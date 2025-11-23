def call(String file = '.env') {
    stage('Setup Environment') {
        if (fileExists(file)) {
            def props = readFile(file).split('\n')
            props.each { line ->
                if (line?.trim() && !line.startsWith('#') && line.contains('=')) {
                    def kv = line.split('=', 2)
                    env[kv[0].trim()] = kv[1].trim()
                }
            }
            echo "✅ Environment variables loaded from ${file}"
        } else {
            echo "⚠️ No ${file} found"
        }
    }
}
