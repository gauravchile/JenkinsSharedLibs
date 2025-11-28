def call(String to, String subject, String body) {
    stage('Notify Email') {
        def summaryFile = 'build-summary.html'

        writeFile file: summaryFile, text: """
            <html>
            <head>
              <style>
                body {
                  font-family: Arial, sans-serif;
                  color: #222;
                  background: #fafafa;
                  padding: 10px 20px;
                }
                h2 { color: #2c7be5; }
                .meta {
                  font-size: 13px;
                  color: #555;
                }
                .section {
                  margin: 12px 0;
                  padding: 10px;
                  background: #fff;
                  border: 1px solid #ddd;
                  border-radius: 6px;
                }
                a { color: #2c7be5; text-decoration: none; }
              </style>
            </head>
            <body>
              <h2>${subject}</h2>
              <div class="section">${body}</div>
              <div class="meta">
                <p><b>Project:</b> ${JOB_NAME}</p>
                <p><b>Build:</b> <a href="${BUILD_URL}">#${BUILD_NUMBER}</a></p>
                <p><b>Date:</b> ${new Date().format("yyyy-MM-dd HH:mm:ss")}</p>
              </div>
            </body>
            </html>
        """

        emailext(
            to: to,
            subject: subject,
            body: """
                <div style='font-family:Arial,sans-serif;'>
                  <h3 style='color:#2c7be5;'>${subject}</h3>
                  ${body}
                  <hr>
                  <p>Project: <b>${JOB_NAME}</b></p>
                  <p>Build: <a href="${BUILD_URL}">#${BUILD_NUMBER}</a></p>
                </div>
                <p><i>Attached: Build Summary (HTML) and Jenkins Console Log</i></p>
            """,
            mimeType: 'text/html',
            attachmentsPattern: 'build-summary.html',
            attachLog: true
        )
    }
}
