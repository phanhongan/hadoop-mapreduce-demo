# Simple Dockerfile for WordCount Learning Example
FROM maven:3.9.5-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy the entire project
COPY . .

# Build the WordCount module
RUN mvn clean compile dependency:copy-dependencies -pl wordcount -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the compiled classes and dependencies
COPY --from=builder /app/wordcount/target/classes /app/classes
COPY --from=builder /app/wordcount/target/dependency /app/dependency

# Create input and output directories
RUN mkdir -p /app/input /app/output

# Default command (use the local test version)
CMD ["java", "-cp", "/app/classes:/app/dependency/*", "solution.WordCountLocalTest"]

# Entry point
ENTRYPOINT ["java", "-cp", "/app/classes:/app/dependency/*", "solution.WordCountLocalTest"]

