FROM dockerhub.camara.gov.br/infra/openjdk:8u131-jre-alpine-4

LABEL br.leg.camara.secao=SEPOP

RUN mkdir -p /app/config
WORKDIR /app

ENV JAVA_OPTS=""

# para fazer debug remoto, adicionar ao JAVA_OPTS o seguinte: -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/servico-indexacao.jar

# diretório onde pode ser incluído application.properties específico sobrescrevendo propriedades default
VOLUME /app/config
# workdir do tomcat
VOLUME /tmp

COPY target/servico-indexacao.jar .
