# Use a base image with Java pre-installed
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the compiled Java application JAR file into the container
COPY target/your-java-application.jar /app

# Specify the command to run your Java application when the container starts
CMD ["java", "-jar", "your-java-application.jar"]
