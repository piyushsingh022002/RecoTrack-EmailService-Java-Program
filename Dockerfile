# 1. Use a Maven build image to compile
FROM maven:3.9.3-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy Maven/Gradle files
COPY pom.xml .
COPY src ./src

# Build the project (produces JAR in /app/target)
RUN mvn clean package -DskipTests

# 2. Use a lightweight JDK image for runtime
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
