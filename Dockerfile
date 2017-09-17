FROM openjdk:8-jdk-alpine

WORKDIR /app/tinydb

COPY . /app/tinydb

RUN ./gradlew clean fatJar

EXPOSE 7081

CMD ["java", "-Dlogback.configurationFile=/app/tinydb/src/test/resources/logback.xml", "-jar", "/app/tinydb/build/libs/tinydb-all-0.14.0-SNAPSHOT.jar", "-h", "0.0.0.0", "-p", "7081"]
