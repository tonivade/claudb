FROM openjdk:8-jdk-alpine

WORKDIR /app/claudb

COPY . /app/claudb

RUN ./gradlew clean fatJar

EXPOSE 7081

CMD ["java", "-Dlogback.configurationFile=src/test/resources/logback.xml", "-jar", "build/libs/claudb-all-0.14.0-SNAPSHOT.jar", "-h", "0.0.0.0", "-p", "7081"]
