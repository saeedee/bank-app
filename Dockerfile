# Use an official Maven image to create a build artifact
FROM maven:3.8.1-openjdk-17-slim AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml file and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Package the application
RUN mvn package -DskipTests

FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Define an argument for the JAR file
ARG JAR_FILE=/app/target/banking-app-0.0.1-SNAPSHOT.jar

# Copy the JAR file into the image
COPY --from=build ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
