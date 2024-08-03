# Use the official OpenJDK image (slim version for a smaller image size)
#FROM openjdk:11-jre-slim
FROM openjdk:17 

# Set the working directory inside the container
WORKDIR /usr/src/bootapp

# Copy the built application JAR file into the container
COPY ./jrtp03-mini-usermgmt-app.jar /usr/src/bootapp/

# Expose the port your Spring Boot app listens on (default is 8080)
EXPOSE 8082

# Define the command to run when the container starts
ENTRYPOINT ["java", "-jar", "/jrtp03-mini-usermgmt-app.jar"] 
