# Build the application using Maven jdk 21
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy the backend source code and pom.xml
COPY . .

# Skip the test cases and build the application
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Run the application in lightweight JRE image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]