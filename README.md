# 🛡️ ShieldOps Jenkins Shared Library

A **complete DevSecOps shared library** for Jenkins that automates CI/CD, container security, IaC validation, compliance checks, and reporting — all in one modular setup.

---

## 📁 Folder Structure

```
shieldops-shared-library/
├── README.md
└── vars/
    ├── clean_ws.groovy
    ├── clone.groovy
    ├── checkout_code.groovy
    ├── setup_env.groovy
    ├── versioning.groovy
    ├── dependency_check.groovy
    ├── lint_code.groovy
    ├── run_tests.groovy
    ├── code_coverage.groovy
    ├── static_analysis.groovy
    ├── trivy_scan.groovy
    ├── snyk_scan.groovy
    ├── secret_scan.groovy
    ├── iac_scan.groovy
    ├── compliance_check.groovy
    ├── docker_build.groovy
    ├── docker_push.groovy
    ├── update_k8s_manifests.groovy
    ├── helm_deploy.groovy
    ├── terraform_apply.groovy
    ├── generate_reports.groovy
    ├── notify_slack.groovy
    ├── notify_email.groovy
    ├── rollback_deploy.groovy
    ├── health_check.groovy
    └── backup_configs.groovy
```

---

## ⚙️ Setup in Jenkins

1. Go to **Manage Jenkins → Configure System → Global Pipeline Libraries**.
2. Add a new library:

   * **Name:** `shieldops`
   * **Default version:** `main`
   * **Retrieval method:** Modern SCM → Git → Enter your repo URL.
3. Load the library in your Jenkinsfile:

   ```groovy
   @Library('shieldops') _
   ```

---

## 🧩 Available Functions

| Category           | Script                                                                                                      | Description                                 |
| ------------------ | ----------------------------------------------------------------------------------------------------------- | ------------------------------------------- |
| 🧹 Workspace       | `clean_ws.groovy`                                                                                           | Cleans Jenkins workspace                    |
| 📦 SCM             | `clone.groovy`, `checkout_code.groovy`                                                                      | Clones or checks out source code            |
| ⚙️ Environment     | `setup_env.groovy`                                                                                          | Loads environment variables from `.env`     |
| 🏷️ Versioning     | `versioning.groovy`                                                                                         | Generates semantic version tags             |
| 🧪 Testing         | `run_tests.groovy`, `lint_code.groovy`, `code_coverage.groovy`                                              | Runs tests and linters                      |
| 🔍 Static Analysis | `static_analysis.groovy`, `dependency_check.groovy`                                                         | Performs code quality scans                 |
| 🛡️ Security       | `trivy_scan.groovy`, `snyk_scan.groovy`, `secret_scan.groovy`, `iac_scan.groovy`, `compliance_check.groovy` | Conducts DevSecOps security checks          |
| 🐳 Container       | `docker_build.groovy`, `docker_push.groovy`                                                                 | Builds and pushes Docker images             |
| ☸️ Deployment      | `helm_deploy.groovy`, `update_k8s_manifests.groovy`                                                         | Deploys to Kubernetes using Helm            |
| ☁️ Infrastructure  | `terraform_apply.groovy`                                                                                    | Provisions infra via Terraform              |
| 📊 Reporting       | `generate_reports.groovy`                                                                                   | Publishes test and scan reports             |
| 📬 Notifications   | `notify_slack.groovy`, `notify_email.groovy`                                                                | Sends build status notifications            |
| 🔁 Recovery        | `rollback_deploy.groovy`, `backup_configs.groovy`                                                           | Rollbacks and backups configs               |
| 🩺 Monitoring      | `health_check.groovy`                                                                                       | Performs app health checks after deployment |

---

## 🧱 Example Jenkinsfile

```groovy
@Library('shieldops') _

pipeline {
    agent any
    environment {
        REGISTRY = 'ghcr.io/gaurav'
        IMAGE_NAME = 'shieldops-app'
        NAMESPACE = 'shieldops'
        SLACK_CHANNEL = '#devsecops'
        APP_URL = 'http://shieldops-app.local/health'
    }

    stages {
        stage('Clean') { steps { clean_ws() } }
        stage('Clone') { steps { clone('https://github.com/gaurav/shieldops.git') } }
        stage('Setup Env') { steps { setup_env('.env') } }
        stage('Versioning') { steps { script { env.VERSION = versioning() } } }

        stage('Test & Lint') {
            parallel {
                stage('Lint') { steps { lint_code('eslint .') } }
                stage('Tests') { steps { run_tests('npm test') } }
                stage('Coverage') { steps { code_coverage('coverage') } }
            }
        }

        stage('Security Scans') {
            parallel {
                stage('Trivy') { steps { trivy_scan(IMAGE_NAME) } }
                stage('Snyk') { steps { snyk_scan('.') } }
                stage('Secrets') { steps { secret_scan('.') } }
                stage('IaC') { steps { iac_scan('.') } }
                stage('Compliance') { steps { compliance_check('policies/cis.yml') } }
            }
        }

        stage('Build & Push') {
            steps {
                docker_build(IMAGE_NAME)
                docker_push(IMAGE_NAME, REGISTRY)
            }
        }

        stage('Deploy') {
            steps {
                update_k8s_manifests('k8s', VERSION)
                helm_deploy(IMAGE_NAME, './helm/shieldops', NAMESPACE)
            }
        }

        stage('Post-Deploy Checks') {
            steps {
                health_check(APP_URL)
                generate_reports('reports')
            }
        }
    }

    post {
        success {
            notify_slack(SLACK_CHANNEL, "✅ ShieldOps pipeline succeeded — version ${VERSION}")
            notify_email('team@shieldops.io', 'Build Success', "Version ${VERSION} deployed successfully.")
            backup_configs()
        }
        failure {
            notify_slack(SLACK_CHANNEL, '❌ ShieldOps pipeline failed!')
            rollback_deploy(NAMESPACE, IMAGE_NAME)
        }
    }
}
```

---

## 🔐 Integrated Security Tools

| Tool          | Function                 |
| ------------- | ------------------------ |
| **Trivy**     | Image vulnerability scan |
| **Snyk**      | Dependency scan          |
| **Gitleaks**  | Secret detection         |
| **tfsec**     | Terraform IaC scan       |
| **Conftest**  | Policy compliance        |
| **OWASP DC**  | Dependency analysis      |
| **SonarQube** | Static code analysis     |

---

## 📢 Notifications

* **Slack Alerts** via `notify_slack.groovy`
* **Email Alerts** via `notify_email.groovy`
* **JUnit & HTML Reports** via `generate_reports.groovy`

---

## 🧠 Author

**Gaurav Chile**
Linux System Administrator | DevOps & Security Engineer
🛡️ *ShieldOps – Secure CI/CD, Simplified.*
