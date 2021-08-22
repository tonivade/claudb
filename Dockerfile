FROM eclipse-temurin:8-jdk AS builder
WORKDIR /opt/claudb
COPY . /opt/claudb
RUN ./gradlew clean build -x test

FROM eclipse-temurin:8-jdk
WORKDIR /root
COPY --from=builder /opt/claudb/app/build/libs/claudb-app-2.0-SNAPSHOT-all.jar claudb.jar
EXPOSE 7081
CMD ["java", "-jar", "claudb.jar", "-h", "0.0.0.0", "-p", "7081"]
