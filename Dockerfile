# Dockerfile para backend Spring Boot (WAR) rodando em Tomcat
# Base: Tomcat 11 + JDK 21 (imagem oficial)

FROM tomcat:11.0.11-jdk21-temurin

# Variável para nome do WAR (ajustável via build args se necessário)
ARG WAR_FILE=gastos-mensais.war

# Remove aplicações padrão do Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia o WAR gerado pelo Gradle para o Tomcat
COPY build/libs/${WAR_FILE} /usr/local/tomcat/webapps/${WAR_FILE}

# Expondo a porta padrão do Tomcat
EXPOSE 8089

# Comando padrão: inicia o Tomcat
CMD ["catalina.sh", "run"]
