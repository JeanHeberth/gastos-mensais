pipeline {
    agent any

    tools {
        // Nome do JDK configurado no Jenkins (Gerenciar Jenkins → Ferramentas Globais)
        jdk 'JDK21'
    }

    environment {
        // Define variável para uso em logs ou integrações futuras
        PROJECT_NAME = 'gastos-mensais'
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
