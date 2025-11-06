pipeline {
    agent any

    tools {
        jdk 'JDK21'
    }

    environment {
        PROJECT_NAME = 'gastos-mensais'
        CODECOV_TOKEN = credentials('CODECOV_TOKEN_GASTOS_MENSAIS') // configure no Jenkins → Credenciais
    }

    stages {
        stage('Checkout') {
            steps {
                echo "📦 Iniciando checkout do código-fonte..."
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "🏗️ Executando build Gradle..."
                bat 'gradlew clean build -x test'
            }
        }

        stage('Test') {
            steps {
                echo "🧪 Executando testes..."
                bat 'gradlew test jacocoTestReport'
            }
        }

        stage('Reports & Coverage') {
            steps {
                script {
                    echo "📊 Gerando relatórios de cobertura Jacoco..."
                    if (isUnix()) {
                        sh './gradlew jacocoTestReport -x jacocoTestCoverageVerification'
                    } else {
                        bat 'gradlew jacocoTestReport -x jacocoTestCoverageVerification'
                    }
                }
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                    publishHTML(target: [
                        reportDir: 'build/reports/jacoco/test/html',
                        reportFiles: 'index.html',
                        reportName: 'Jacoco Coverage Report'
                    ])
                }
            }
        }

        stage('Upload Coverage to Codecov') {
            steps {
                script {
                    echo "☁️ Enviando relatório de cobertura para Codecov..."
                    if (isUnix()) {
                        sh 'curl -s https://codecov.io/bash | bash -s -- -t ${CODECOV_TOKEN}'
                    } else {
                        bat '''
                            echo Baixando Codecov para Windows...
                            curl -L -o codecov.exe https://uploader.codecov.io/latest/windows/codecov.exe
                            echo Enviando relatório de cobertura...
                            codecov.exe -t %CODECOV_TOKEN_GASTOS_MENSAIS% -f build\\reports\\jacoco\\test\\jacocoTestReport.xml
                        '''
                    }
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo "📁 Arquivando artefatos..."
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline concluído com sucesso para ${env.PROJECT_NAME}!"
        }
        failure {
            echo "❌ Falha detectada no pipeline. Verifique os logs."
        }
        always {
            echo "🧹 Finalizando execução do pipeline."
        }
    }
}