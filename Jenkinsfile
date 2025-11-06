pipeline {
    agent any

    tools {
        jdk 'JDK21'
    }

    environment {
        CODECOV_TOKEN = credentials('CODECOV_TOKEN_GASTOS_MENSAIS')
        GITHUB_TOKEN = credentials('GITHUB_TOKEN')
    }

    stages {
        // =========================================================
        // 1️⃣ CHECKOUT
        // =========================================================
        stage('Checkout') {
            steps {
                echo "🔄 Clonando o repositório..."
                checkout scm
            }
        }

        // =========================================================
        // 2️⃣ BUILD
        // =========================================================
        stage('Build') {
            steps {
                script {
                    echo "⚙️ Executando build do projeto..."
                    if (isUnix()) {
                        sh './gradlew clean build -x test'
                    } else {
                        bat 'gradlew clean build -x test'
                    }
                }
            }
        }

        // =========================================================
        // 3️⃣ UNIT TESTS - SERVICE
        // =========================================================
        stage('Unit Tests - Service') {
            steps {
                script {
                    echo "🧪 Executando testes unitários da camada Service..."
                    if (isUnix()) {
                        sh './gradlew test --tests "br.com.blogqateste.service.*"'
                    } else {
                        bat 'gradlew test --tests "br.com.blogqateste.service.*"'
                    }
                }
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }

        // =========================================================
        // 4️⃣ INTEGRATION TESTS
        // =========================================================
        stage('Integration Tests') {
            steps {
                script {
                    echo "🔗 Executando testes de integração..."
                    if (isUnix()) {
                        sh './gradlew test --tests "br.com.blogqateste.integration.*"'
                    } else {
                        bat 'gradlew test --tests "br.com.blogqateste.integration.*"'
                    }
                }
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }

        // =========================================================
        // 5️⃣ REPORTS & COVERAGE
        // =========================================================
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

        // =========================================================
        // 6️⃣ UPLOAD TO CODECOV
        // =========================================================
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
                            codecov.exe -t %CODECOV_TOKEN% -f build\\reports\\jacoco\\test\\jacocoTestReport.xml
                        '''
                    }
                }
            }
        }

        // =========================================================
        // 7️⃣ DEPLOY WAR TO TOMCAT (Windows)
        // =========================================================
        stage('Deploy WAR to Tomcat') {
            steps {
                script {
                    echo "🚀 Copiando WAR para a pasta do Tomcat..."

                    // Caminhos configuráveis
                    def sourceWar = "build\\libs\\gastos-mensais.war"
                    def tomcatWebapps = "C:\\apache-tomcat-11.0.11\\webapps"

                    // Copia o WAR gerado para o Tomcat
                    bat """
                        echo Copiando arquivo WAR para o Tomcat...
                        copy /Y "${sourceWar}" "${tomcatWebapps}\\gastos-mensais.war"
                    """

                    // Reinicia o serviço Tomcat
                    bat """
                        echo Reiniciando serviço Tomcat...
                        net stop Tomcat11
                        net start Tomcat11
                    """
                }
            }
        }

        // =========================================================
        // 8️⃣ DEPLOY TO TOMCAT (Script-based)
        // =========================================================
        stage('Deploy to Tomcat via Script') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "🚀 Iniciando deploy automático no Tomcat 11..."
                    if (isUnix()) {
                        sh './scripts/deploy_tomcat.sh'
                    } else {
                        bat 'powershell -ExecutionPolicy Bypass -File deploy_tomcat.ps1'
                    }
                    echo "✅ Deploy finalizado com sucesso! WAR atualizado no Tomcat 🎯"
                }
            }
        }
    }
    // =========================================================
    // 🔄 POST ACTIONS
    // =========================================================
    post {
        always {
            echo '✅ Pipeline concluído.'
        }
        success {
            echo '🎉 Todos os stages executados com sucesso!'
        }
        failure {
            echo '❌ Falha detectada no pipeline. Verifique os logs.'
        }
    }
    }
}