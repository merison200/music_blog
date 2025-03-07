# Use the official OpenJDK 17 image as the base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml first to cache dependencies
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies to cache them in Docker layers
RUN ./mvnw dependency:go-offline

# Copy the rest of the project
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN chown -R appuser:appgroup /app

# Switch to the non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Run the application with the built JAR
CMD ["java", "-jar", "target/*.jar"]