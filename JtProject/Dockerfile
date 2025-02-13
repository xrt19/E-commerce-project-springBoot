# Stage 1: Build aplikasi menggunakan Maven
FROM maven:3.8.5-openjdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Buat image runtime yang ramping
FROM openjdk:11-jre-slim
WORKDIR /app
# Salin file JAR hasil build
COPY --from=build /app/target/*.jar app.jar
# Salin folder views agar tersedia di container
COPY src/main/webapp/views /views
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
