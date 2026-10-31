# Use Maven to build the application
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Skip tests for faster image build (tests already run locally)
RUN mvn -B -DskipTests clean package

# Run stage – lightweight JDK runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy the jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
# Spring Boot defaults to port 8080, but we configured 8081 in properties.
ENTRYPOINT ["java","-jar","app.jar"]
