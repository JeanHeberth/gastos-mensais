def runByOs(String unixCmd, String windowsCmd) {
    if (isUnix()) {
        sh unixCmd
    } else if ((env.OS ?: '').toLowerCase().contains('windows')) {
        bat windowsCmd
    } else {
        error("Nó incompatível para execução do pipeline. NODE_NAME=${env.NODE_NAME}, OS=${env.OS}")
    }
}

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
                    runByOs('./gradlew clean bootWar -x test', 'gradlew.bat clean bootWar -x test')
                }
            }
        }

        stage('Unit Tests - Service') {
            steps {
                script {
                    echo "🧪 Executando testes unitários da camada Service..."
                    runByOs('./gradlew test --tests "br.com.gastosmensais.service.*"', 'gradlew.bat test --tests "br.com.gastosmensais.service.*"')
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
                    runByOs('./gradlew test --tests "br.com.gastosmensais.controller.*"', 'gradlew.bat test --tests "br.com.gastosmensais.controller.*"')
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
                    runByOs('./gradlew jacocoTestReport -x jacocoTestCoverageVerification', 'gradlew.bat jacocoTestReport -x jacocoTestCoverageVerification')
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

                    runByOs(
                        '''
                            curl -Os https://uploader.codecov.io/latest/macos/codecov
                            chmod +x codecov
                            ./codecov -t "$CODECOV_TOKEN" -f build/reports/jacoco/test/jacocoTestReport.xml
                        '''.stripIndent(),
                        '''
                            curl -L -o codecov.exe https://uploader.codecov.io/latest/windows/codecov.exe
                            codecov.exe -t %CODECOV_TOKEN% -f build\\reports\\jacoco\\test\\jacocoTestReport.xml
                        '''.stripIndent()
                    )
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

                    runByOs(
                        '''
                            WAR_ORIGEM="build/libs/gastos-mensais.war"
                            TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-/opt/homebrew/opt/tomcat/libexec/webapps}"

                            echo "Copiando WAR..."
                            cp "$WAR_ORIGEM" "$TOMCAT_WEBAPPS/gastos-mensais.war"

                            echo "Reiniciando Tomcat..."
                            brew services restart tomcat || true
                        '''.stripIndent(),
                        '''
                            set WAR_ORIGEM=build\\libs\\gastos-mensais.war

                            if "%TOMCAT_WEBAPPS%"=="" (
                                set TOMCAT_WEBAPPS=C:\\apache-tomcat-11.0.11\\webapps
                            )

                            echo Copiando WAR...
                            copy /Y "%WAR_ORIGEM%" "%TOMCAT_WEBAPPS%\\gastos-mensais.war"

                            echo Reiniciando Tomcat...
                            net stop Tomcat11
                            net start Tomcat11
                        '''.stripIndent()
                    )
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