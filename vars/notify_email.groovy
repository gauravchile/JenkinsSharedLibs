def call(String to, String subject, String body) {
    stage('Notify Email') {
        emailext(to: to, subject: subject, body: body)
    }
}
