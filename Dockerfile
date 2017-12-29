FROM openjdk:8-jdk-alpine

WORKDIR /app/claudb

COPY . /app/claudb

RUN ./gradlew clean fatJar

EXPOSE 7081

CMD ["java", "-jar", "build/libs/claudb-all-1.2.0-SNAPSHOT.jar", "-h", "0.0.0.0", "-p", "7081"]
