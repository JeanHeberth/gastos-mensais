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

        stage('Checkout') {
            steps {
                echo "🔄 Clonando o repositório..."
                checkout scm
            }
        }

        stage('Build WAR') {
            steps {
                script {
                    echo "📦 Gerando arquivo WAR..."

                    if (isUnix()) {
                        sh './gradlew clean bootWar -x test'
                    } else {
                        bat 'gradlew.bat clean bootWar -x test'
                    }
                }
            }
        }

        stage('Unit Tests - Service') {
            steps {
                script {
                    echo "🧪 Executando testes unitários da camada Service..."

                    if (isUnix()) {
                        sh './gradlew test --tests "br.com.gastosmensais.service.*"'
                    } else {
                        bat 'gradlew.bat test --tests "br.com.gastosmensais.service.*"'
                    }
                }
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }

        stage('Integration Tests - Controller') {
            steps {
                script {
                    echo "🔗 Executando testes de integração da camada Controller..."

                    if (isUnix()) {
                        sh './gradlew test --tests "br.com.gastosmensais.controller.*"'
                    } else {
                        bat 'gradlew.bat test --tests "br.com.gastosmensais.controller.*"'
                    }
                }
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }

        stage('Reports & Coverage') {
            steps {
                script {
                    echo "📊 Gerando relatório Jacoco..."

                    if (isUnix()) {
                        sh './gradlew jacocoTestReport -x jacocoTestCoverageVerification'
                    } else {
                        bat 'gradlew.bat jacocoTestReport -x jacocoTestCoverageVerification'
                    }
                }
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'

                    publishHTML(target: [
                        reportDir: 'build/reports/jacoco/test/html',
                        reportFiles: 'index.html',
                        reportName: 'Jacoco Coverage Report',
                        keepAll: true,
                        alwaysLinkToLastBuild: true,
                        allowMissing: true
                    ])
                }
            }
        }

        stage('Upload Coverage to Codecov') {
            steps {
                script {
                    echo "☁️ Enviando cobertura para Codecov..."

                    if (isUnix()) {
                        sh '''
                            curl -Os https://uploader.codecov.io/latest/macos/codecov
                            chmod +x codecov
                            ./codecov -t "$CODECOV_TOKEN" -f build/reports/jacoco/test/jacocoTestReport.xml
                        '''
                    } else {
                        bat '''
                            curl -L -o codecov.exe https://uploader.codecov.io/latest/windows/codecov.exe
                            codecov.exe -t %CODECOV_TOKEN% -f build\\reports\\jacoco\\test\\jacocoTestReport.xml
                        '''
                    }
                }
            }
        }

        stage('Deploy WAR to Tomcat') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "🚀 Publicando WAR no Tomcat..."

                    if (isUnix()) {
                        sh '''
                            WAR_ORIGEM="build/libs/gastos-mensais.war"
                            TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-/opt/homebrew/opt/tomcat/libexec/webapps}"

                            echo "Copiando WAR..."
                            cp "$WAR_ORIGEM" "$TOMCAT_WEBAPPS/gastos-mensais.war"

                            echo "Reiniciando Tomcat..."
                            brew services restart tomcat || true
                        '''
                    } else {
                        bat '''
                            set WAR_ORIGEM=build\\libs\\gastos-mensais.war

                            if "%TOMCAT_WEBAPPS%"=="" (
                                set TOMCAT_WEBAPPS=C:\\apache-tomcat-11.0.11\\webapps
                            )

                            echo Copiando WAR...
                            copy /Y "%WAR_ORIGEM%" "%TOMCAT_WEBAPPS%\\gastos-mensais.war"

                            echo Reiniciando Tomcat...
                            net stop Tomcat11
                            net start Tomcat11
                        '''
                    }
                }
            }
        }
    }

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