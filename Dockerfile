FROM openjdk:12
VOLUME /tmp
ARG VERSION=0.0.1
ARG JAR_FILE=/target/shortURL-${VERSION}-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
