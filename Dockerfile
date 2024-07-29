# Use the official OpenJDK image (slim version for a smaller image size)
FROM openjdk:11-jre-slim 

# Set the working directory inside the container
WORKDIR /app

# Copy the built application JAR file into the container
COPY target/user-management-api-*.jar app.jar

# Expose the port your Spring Boot app listens on (default is 8080)
EXPOSE 8080

# Define the command to run when the container starts
ENTRYPOINT ["java", "-jar", "/app.jar"] 
