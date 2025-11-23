def call(String srcDir = '.', String outDir = 'reports/dependency-check') {
    stage('Dependency Check') {
        sh """
            mkdir -p ${outDir}
            dependency-check.sh --scan ${srcDir} --format HTML --out ${outDir} || true
        """
        publishHTML(target: [reportDir: outDir, reportFiles: 'dependency-check-report.html', reportName: 'Dependency Check'])
    }
}
