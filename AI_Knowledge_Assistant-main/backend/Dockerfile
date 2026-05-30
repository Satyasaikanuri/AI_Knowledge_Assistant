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

ENTRYPOINT ["java",
"-XX:+UseSerialGC",
"-Xms128m",
"-Xmx320m",
"-XX:MaxMetaspaceSize=96m",
"-XX:ReservedCodeCacheSize=48m",
"-XX:+UseContainerSupport",
"-Djava.security.egd=file:/dev/./urandom",
"-jar",
"app.jar"]
