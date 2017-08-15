FROM openjdk:8-jdk-alpine

WORKDIR /app/tinydb

ADD . /app/tinydb

RUN ./gradlew clean fatJar

EXPOSE 7081

CMD ["java", "-jar", "/app/tinydb/build/libs/tinydb-all-0.11.0-SNAPSHOT.jar", "-h", "0.0.0.0", "-p", "7081"]
