FROM openjdk:17-jdk-slim

ENV LANG=C.UTF-8

RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y sqlite3 curl && \
    apt-get clean

WORKDIR /rest-api-kt

COPY out/production/rest-api-kt/ /rest-api-kt/

RUN curl -L -o /rest-api-kt/sqlite-jdbc.jar https://github.com/xerial/sqlite-jdbc/releases/download/3.36.0.3/sqlite-jdbc-3.36.0.3.jar && \
    curl -L -o /rest-api-kt/kotlin-stdlib.jar https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/2.0.21/kotlin-stdlib-2.0.21.jar

EXPOSE 8080

CMD ["java", "-cp", ".:/rest-api-kt/sqlite-jdbc.jar:/rest-api-kt/kotlin-stdlib.jar", "MainKt"]