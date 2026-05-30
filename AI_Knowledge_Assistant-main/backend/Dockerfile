# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Use PORT environment variable provided by Render
EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-Xms64m -Xmx128m -XX:+UseSerialGC -Xss512k -XX:MaxMetaspaceSize=90m -XX:MaxDirectMemorySize=64m -XX:ReservedCodeCacheSize=64m"

ENTRYPOINT ["java", "-jar", "app.jar"]
