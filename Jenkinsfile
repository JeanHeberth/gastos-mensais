pipeline {
    agent any

    tools {
        // Nome do JDK configurado no Jenkins (Gerenciar Jenkins → Ferramentas Globais)
        jdk 'JDK21'
    }

    environment {
        // Define variável para uso em logs ou integrações futuras
        PROJECT_NAME = 'gastos-mensais'
        CODECOV_TOKEN = credentials('CODECOV')
        GITHUB_TOKEN = credentials('GITHUB_TOKEN')
        ORG_GRADLE_JAVA_HOME = "${env.JAVA_HOME}"
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
                echo "🏗️  Executando build Gradle..."
                // Em ambiente Windows, usamos 'bat' no lugar de 'sh'
                bat 'gradlew clean build -x test'
            }
        }

        stage('Test') {
            steps {
                echo "🧪 Executando testes..."
                bat 'gradlew test'
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo "📁 Arquivando artefatos gerados..."
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }

        // =========================================================
        // ☁️ UPLOAD TO CODECOV
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
    }

    post {
        success {
            echo "✅ Pipeline concluído com sucesso para ${env.PROJECT_NAME}!"
        }
        failure {
            echo "❌ Falha detectada no pipeline de ${env.PROJECT_NAME}. Verifique os logs."
        }
        always {
            echo "🧹 Finalizando execução do pipeline."
        }
    }
}