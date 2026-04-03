# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17-focal AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jdk-focal
WORKDIR /app

# Add a non-root user for security
RUN groupadd -r microbank && useradd -r -g microbank microbank
USER microbank

# Copy the built jar from the builder stage
COPY --from=builder /app/target/microbank-*.jar app.jar

# HEALTHCHECK instruction for container monitoring
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

# Performance and memory optimization for Java
ENTRYPOINT ["java", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:+UseContainerSupport", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", \
            "app.jar"]

LABEL maintainer="MicroBank Dev Team"
LABEL version="0.0.1"
LABEL description="Core Banking Simulator Runtime Image"
