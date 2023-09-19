FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /builder
COPY gradlew gradlew.bat settings.gradle ./
COPY gradle ./gradle
COPY build.gradle ./
COPY src ./src
RUN chmod +x ./gradlew && ./gradlew build -x test

FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /builder/build/libs/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
