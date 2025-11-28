def call(Map config = [:]) {
    // Services to check
    def services = config.get('services', ['frontend', 'backend'])
    def namespace = config.get('namespace', 'default')
    def defaultColor = config.get('defaultColor', 'blue')

    def detectedColors = [:]

    echo "🎨 Detecting active color(s) in namespace '${namespace}' for services: ${services.join(', ')}..."

    for (svc in services) {
        try {
            def color = sh(
                script: "kubectl get svc ${svc} -n ${namespace} -o jsonpath='{.spec.selector.color}' 2>/dev/null || echo ''",
                returnStdout: true
            ).trim()

            if (color) {
                detectedColors[svc] = color
                echo "✅ ${svc} active color: ${color}"
            } else {
                detectedColors[svc] = defaultColor
                echo "⚠️  No color found for ${svc}. Defaulting to '${defaultColor}'."
            }
        } catch (Exception e) {
            detectedColors[svc] = defaultColor
            echo "⚠️  Error checking ${svc}: ${e.getMessage()}. Defaulting to '${defaultColor}'."
        }
    }

    // Pick the first service’s color as reference (frontend usually)
    def mainService = services[0]
    def activeColor = detectedColors[mainService] ?: defaultColor
    def nextColor = (activeColor == 'blue') ? 'green' : 'blue'

    echo """
    ==============================
    Active Color Summary:
    ${detectedColors.collect { k, v -> " - ${k}: ${v}" }.join('\n')}
    Selected Active Color: ${activeColor}
    Selected Next Color:   ${nextColor}
    ==============================
    """

    return [
        activeColor: activeColor,
        nextColor: nextColor,
        all: detectedColors
    ]
}
