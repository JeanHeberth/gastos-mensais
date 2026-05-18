pipeline {
    agent any

    tools {
        jdk 'JDK21'
    }

    environment {
        CODECOV_TOKEN = credentials('CODECOV_TOKEN_GASTOS_MENSAIS')
        GITHUB_TOKEN  = credentials('GITHUB_TOKEN')
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
                        sh './gradlew test --tests "br.com.gastosmensais.service.*"'
                    } else {
                        bat 'gradlew test --tests "br.com.gastosmensais.service.*"'
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
                        sh './gradlew test --tests "br.com.gastosmensais.controller.*"'
                    } else {
                        bat 'gradlew test --tests "br.com.gastosmensais.controller.*"'
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
        stage('Debug Branch') {
            steps {
                script {
                    echo "================ DEBUG BRANCH ================"
                    echo "BRANCH_NAME=${env.BRANCH_NAME}"
                    echo "CHANGE_BRANCH=${env.CHANGE_BRANCH}"
                    echo "CHANGE_TARGET=${env.CHANGE_TARGET}"
                    echo "GIT_BRANCH=${env.GIT_BRANCH}"
                    echo "NODE_NAME=${env.NODE_NAME}"
                    echo "OS=${env.OS}"
                    echo "============================================="
                }
            }
        }

        // =========================================================
        // 7️⃣ DEPLOY WAR TO TOMCAT (Main/Master only)
        // =========================================================
        stage('Deploy WAR to Tomcat') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                    allOf {
                        changeRequest()
                        expression { ['main', 'master'].contains(env.CHANGE_TARGET) }
                    }
                }
            }
            steps {
                script {
                    echo "🚀 Exportando WAR para o Tomcat (main/master)..."

                    if (isUnix()) {
                        sh '''
                            SOURCE_WAR="${WORKSPACE}/build/libs/gastos-mensais.war"
                            DEST_WAR="/opt/homebrew/opt/tomcat/libexec/webapps/gastos-mensais.war"

                            echo "Verificando WAR gerado em: $SOURCE_WAR"
                            [ -f "$SOURCE_WAR" ] || { echo "WAR nao encontrado em $SOURCE_WAR"; exit 1; }

                            echo "Copiando WAR para: $DEST_WAR"
                            cp "$SOURCE_WAR" "$DEST_WAR"

                            echo "Deploy concluido com sucesso em $DEST_WAR"
                        '''
                    } else if ((env.OS ?: '').toLowerCase().contains('windows')) {
                        bat '''
                            set SOURCE_WAR=%WORKSPACE%\\build\\libs\\gastos-mensais.war
                            if "%TOMCAT_WEBAPPS%"=="" (
                                set TOMCAT_WEBAPPS=C:\\apache-tomcat-11.0.11\\webapps
                            )
                            set DEST_WAR=%TOMCAT_WEBAPPS%\\gastos-mensais.war

                            echo Verificando WAR gerado em: %SOURCE_WAR%
                            if not exist "%SOURCE_WAR%" (
                                echo WAR nao encontrado em %SOURCE_WAR%
                                exit /b 1
                            )

                            echo Copiando WAR para: %DEST_WAR%
                            copy /Y "%SOURCE_WAR%" "%DEST_WAR%"
                        '''
                    } else {
                        error("Nó incompatível para deploy. NODE_NAME=${env.NODE_NAME}, OS=${env.OS}")
                    }
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
