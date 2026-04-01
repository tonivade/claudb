FROM eclipse-temurin:11-jdk AS builder
WORKDIR /opt/claudb
COPY . /opt/claudb
RUN ./gradlew clean build -x test

FROM eclipse-temurin:11-jdk
WORKDIR /root
COPY --from=builder /opt/claudb/app/build/libs/claudb-app-*-all.jar claudb.jar
EXPOSE 7081
CMD ["java", "-jar", "claudb.jar", "-h", "0.0.0.0", "-p", "7081"]
