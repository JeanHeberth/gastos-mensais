pipeline {
    agent any

    tools {
        jdk 'JDK21'
    }

    environment {
        CODECOV_TOKEN = credentials('CODECOV_TOKEN_GASTOS_MENSAIS')
        GITHUB_TOKEN  = credentials('GITHUB_TOKEN')
        TOMCAT_WEBAPPS = 'C:\\apache-tomcat-11.0.11\\webapps'
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Clonando repositório..."
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'gradlew clean build -x test'
            }
        }

        stage('Unit Tests - Service') {
            steps {
                bat 'gradlew test --tests "br.com.gastosmensais.service.*"'
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }

        stage('Integration Tests') {
            steps {
                bat 'gradlew test --tests "br.com.gastosmensais.controller.*"'
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }

        stage('Reports & Coverage') {
            steps {
                bat 'gradlew jacocoTestReport -x jacocoTestCoverageVerification'
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
                bat '''
                    echo Baixando Codecov para Windows...
                    curl -L -o codecov.exe https://uploader.codecov.io/latest/windows/codecov.exe

                    echo Enviando cobertura...
                    codecov.exe -t %CODECOV_TOKEN% -f build\\reports\\jacoco\\test\\jacocoTestReport.xml
                '''
            }
        }

        stage('Deploy WAR to Tomcat Windows') {
            when {
                expression {
                    def gitBranch = (env.GIT_BRANCH ?: '').toLowerCase()
                    return gitBranch == 'origin/main' || gitBranch == 'origin/master'
                }
            }

            steps {
                bat '''
                    echo Procurando arquivo WAR em %WORKSPACE%\\build\\libs

                    for %%F in ("%WORKSPACE%\\build\\libs\\*.war") do (
                        echo WAR encontrado: %%~nxF

                        echo Copiando para Tomcat...
                        copy /Y "%%~fF" "%TOMCAT_WEBAPPS%\\%%~nxF"

                        echo Deploy concluido em %TOMCAT_WEBAPPS%\\%%~nxF
                        exit /b 0
                    )

                    echo ERRO: Nenhum WAR encontrado em %WORKSPACE%\\build\\libs
                    exit /b 1
                '''
            }
        }
    }

    post {
        always {
            echo 'Pipeline concluído.'
        }

        success {
            echo 'Todos os stages executados com sucesso!'
        }

        failure {
            echo 'Falha detectada no pipeline. Verifique os logs.'
        }
    }
}