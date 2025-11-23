def call(String channel, String message) {
    stage('Notify Slack') {
        slackSend(channel: channel, message: message)
    }
}
