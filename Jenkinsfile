stage('Deploy WAR to Tomcat') {
    when {
        anyOf {
            branch 'main'
            branch 'corrigindo-jenkins'
        }
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