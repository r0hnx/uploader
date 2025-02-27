# Use official OpenJDK base image
FROM eclipse-temurin:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR file from target directory (built using Maven)
COPY target/fileapp.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/app.jar"]