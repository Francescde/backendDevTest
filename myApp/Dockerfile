FROM redis:latest AS redis
# Use an appropriate base image with Java and Maven pre-installed
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file to the container
COPY myApp/pom.xml .

# Copy the source code to the container
COPY myApp/src ./src

# Build the application with Maven
RUN mvn package -DskipTests

# Create a new image with a smaller footprint
FROM openjdk:17-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/myApp-0.0.1-SNAPSHOT.jar .

# Expose port 5000 for incoming requests
EXPOSE 5000

# Set the command to run the application
CMD ["java", "-jar", "myApp-0.0.1-SNAPSHOT.jar", "--server.port=5000"]
