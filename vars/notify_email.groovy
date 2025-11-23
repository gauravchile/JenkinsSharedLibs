def call(String to, String subject, String body) {
    stage('Notify Email') {
        emailext(
            to: to,
            subject: subject,
            body: """
                <h3>${subject}</h3>
                <p>${body}</p>
                <p>Project: ${JOB_NAME}</p>
                <p>Build: <a href="${BUILD_URL}">#${BUILD_NUMBER}</a></p>
            """,
            mimeType: 'text/html',
            attachLog: true
        )
    }
}
