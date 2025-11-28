/**
 * notify_email.groovy
 * -------------------
 * Universal Jenkins email helper with attached build summary.
 *
 * Features:
 * - HTML-formatted emails
 * - Attaches console log and summary file (build-summary.html)
 * - Works with success/unstable/failure notifications
 */

def call(String to, String subject, String body) {
    stage('Notify Email') {
        // Define build summary path
        def summaryFile = 'build-summary.html'

        // Create the summary HTML file
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
                h2 {
                  color: #2c7be5;
                }
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
                a {
                  color: #2c7be5;
                  text-decoration: none;
                }
              </style>
            </head>
            <body>
              <h2>${subject}</h2>
              <div class="section">
                ${body}
              </div>
              <div class="meta">
                <p><b>Project:</b> ${JOB_NAME}</p>
                <p><b>Build:</b> <a href="${BUILD_URL}">#${BUILD_NUMBER}</a></p>
                <p><b>Date:</b> ${new Date().format("yyyy-MM-dd HH:mm:ss")}</p>
              </div>
            </body>
            </html>
        """

        echo "📧 Sending HTML email to ${to} with build summary..."

        // Send the email with both attachments
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

        echo "✅ Email sent successfully with summary file."
    }
}
