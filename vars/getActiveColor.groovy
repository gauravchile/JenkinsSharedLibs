def call(Map config = [:]) {
    def services     = config.get('services', ['frontend', 'backend'])
    def namespace    = config.get('namespace', 'default')
    def defaultColor = config.get('defaultColor', 'blue')

    def detected = [:]

    echo "Detecting active deployment colors in namespace '${namespace}' for services: ${services.join(', ')}"

    services.each { svc ->
        def color = ""
        try {
            color = sh(
                script: "kubectl get svc ${svc} -n ${namespace} -o jsonpath='{.spec.selector.color}' 2>/dev/null || true",
                returnStdout: true
            ).trim()

            if (!color) {
                color = defaultColor
                echo "No color found for ${svc}, defaulting to '${defaultColor}'"
            } else {
                echo "${svc} active color: ${color}"
            }
        } catch (err) {
            color = defaultColor
            echo "Error checking ${svc}: ${err.getMessage()} — defaulting to '${defaultColor}'"
        }
        detected[svc] = color
    }

    // Reference first service (usually frontend)
    def active = detected[services[0]] ?: defaultColor
    def next   = (active == 'blue') ? 'green' : 'blue'

    echo """
    ---- Active Color Summary ----
    ${detected.collect { k, v -> " - ${k}: ${v}" }.join('\n')}
    Active: ${active}
    Next:   ${next}
    --------------------------------
    """

    return [activeColor: active, nextColor: next, all: detected]
}
