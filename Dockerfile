# Этап 1: Сборка на JDK 24
FROM maven:3.9-eclipse-temurin-24-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Этап 2: Запуск на JRE 24
FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]