# Dedicated Dockerfile for Uwritables Example
FROM maven:3.9.5-openjdk-17 AS builder

# Set working directory
WORKDIR /app

# Copy pom files
COPY pom.xml .
COPY writables/pom.xml ./writables/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY writables/ ./writables/

# Build the writables module
RUN mvn clean package -pl writables -am -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy built artifacts from builder stage
COPY --from=builder /app/writables/target/writables-*.jar /app/writables.jar

# Copy configuration files
COPY --from=builder /app/src/main/resources/logback.xml /app/logback.xml

# Create directories
RUN mkdir -p /app/input /app/output /app/logs

# Set ownership
RUN chown -R appuser:appuser /app

# Switch to app user
USER appuser

# Set environment variables
ENV JAVA_OPTS="-Xmx2g -Xms1g"
ENV HADOOP_CLASSPATH="/app/*"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD test -f /app/output/*/_SUCCESS || exit 1

# Default command
ENTRYPOINT ["java", "-jar", "/app/writables.jar"]

# Default arguments (can be overridden)
CMD ["input/shakespeare.txt", "output/writables"]
