# Hadoop MapReduce Demo Documentation

Welcome to the comprehensive documentation for the Hadoop MapReduce Demo project. This guide covers all aspects of the project from setup to advanced usage.

## 📚 Table of Contents

1. [Getting Started](getting-started.md)
2. [Architecture Overview](architecture.md)
3. [Examples Guide](examples.md)
4. [Deployment Guide](deployment.md)
5. [Performance Tuning](performance.md)
6. [Troubleshooting](troubleshooting.md)
7. [API Reference](api.md)

## 🚀 Quick Start

### Prerequisites

- **Java**: 17 or higher
- **Maven**: 3.9 or higher
- **Docker**: 20.10 or higher (optional)
- **Memory**: At least 4GB RAM available

### Installation

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd hadoop-mapreduce-demo
   ```

2. **Build the project**:
   ```bash
   ./scripts/build.sh
   ```

3. **Run WordCount example**:
   ```bash
   ./scripts/run-wordcount.sh
   ```

## 🏗️ Project Structure

```
hadoop-mapreduce-demo/
├── docs/                    # Documentation
├── scripts/                 # Utility scripts
├── docker/                  # Docker configuration
├── input/                   # Sample input data
├── output/                  # Job output (generated)
├── logs/                    # Application logs
├── wordcount/              # WordCount example
├── averagewordlength/       # Average word length example
├── log_file_analysis/       # Log processing example
├── partitioner/             # Custom partitioning example
├── counters/                # Counter usage example
├── createsequencefile/      # Sequence file examples
├── inputformat/             # Custom input format examples
├── mrunit/                  # Unit testing examples
├── toolrunner/              # Command-line tool examples
├── writables/               # Custom Writable types
├── combiner/                # Combiner optimization examples
└── logging/                 # Logging framework examples
```

## 🔧 Core Components

### 1. WordCount Example
The classic MapReduce example demonstrating basic word frequency counting.

**Key Features**:
- Efficient word splitting with compiled regex
- Proper error handling and logging
- Performance optimizations
- Comprehensive monitoring

**Usage**:
```bash
mvn exec:java -pl wordcount \
  -Dexec.mainClass="solution.WordCount" \
  -Dexec.args="input/shakespeare.txt output/wordcount"
```

### 2. Log Analysis Example
Real-world log processing with custom parsing and aggregation.

**Key Features**:
- Custom log entry parsing
- Date-based aggregation
- Error rate monitoring
- Performance metrics

### 3. Custom Partitioning
Demonstrates custom data partitioning strategies for balanced processing.

**Key Features**:
- Month-based partitioning
- Load balancing
- Custom partitioner implementation
- Performance testing

## 🐳 Docker Deployment

### Local Development
```bash
# Build and run with Docker Compose
docker-compose up --build

# Run individual container
docker run -v $(pwd)/input:/app/input \
           -v $(pwd)/output:/app/output \
           hadoop-mapreduce-demo
```

### Production Deployment
```bash
# Build production image
docker build -t hadoop-mapreduce-demo:latest .

# Run with production settings
docker run -d \
  --name hadoop-mapreduce \
  --memory=4g \
  --cpus=2 \
  -v /data/input:/app/input \
  -v /data/output:/app/output \
  hadoop-mapreduce-demo:latest
```

## ☁️ Cloud Deployment

### AWS EMR
```bash
# Deploy to EMR cluster
./scripts/deploy-emr.sh

# Submit jobs
aws emr add-steps --cluster-id <cluster-id> \
  --steps file://steps/wordcount-step.json
```

### Google Cloud Dataproc
```bash
# Create cluster
gcloud dataproc clusters create hadoop-demo \
  --region=us-central1 \
  --zone=us-central1-a

# Submit job
gcloud dataproc jobs submit hadoop \
  --cluster=hadoop-demo \
  --region=us-central1 \
  --jar=gs://your-bucket/wordcount.jar \
  -- input.txt output/
```

## 📊 Monitoring and Logging

### Logging Configuration
The project uses SLF4J + Logback for structured logging:

- **Console**: Development and debugging
- **File**: Production logging with rotation
- **Levels**: Configurable per package
- **Format**: Structured with timestamps

### Metrics and Counters
Built-in Hadoop counters for monitoring:

- **WordMapper**: Lines processed, words emitted, errors
- **SumReducer**: Words processed, total count, errors
- **Custom**: Application-specific metrics

### Performance Monitoring
- Job execution time
- Memory usage
- I/O statistics
- Network utilization

## 🧪 Testing

### Unit Testing
```bash
# Run all tests
mvn test

# Run specific test suite
mvn test -Dtest=TestWordCount

# Run with coverage
mvn test jacoco:report
```

### Integration Testing
```bash
# Run with MRUnit
mvn test -Dtest=*IntegrationTest

# Run with local Hadoop
mvn test -Dtest=*LocalTest
```

### Performance Testing
```bash
# Benchmark with different data sizes
./scripts/benchmark.sh

# Compare different configurations
./scripts/performance-test.sh
```

## 🔍 Troubleshooting

### Common Issues

1. **Out of Memory Errors**
   - Increase heap size: `-Xmx4g`
   - Reduce reducer count
   - Enable compression

2. **Slow Performance**
   - Check input format
   - Optimize partitioner
   - Enable combiners
   - Use appropriate compression

3. **Build Failures**
   - Verify Java version (17+)
   - Check Maven version (3.9+)
   - Clear Maven cache: `mvn clean`

### Debug Mode
```bash
# Enable debug logging
export HADOOP_ROOT_LOGGER=DEBUG,console

# Run with verbose output
mvn exec:java -Dexec.args="-Dlog.level=DEBUG input output"
```

## 📈 Performance Optimization

### Mapper Optimizations
- Use compiled regex patterns
- Reuse objects
- Enable compression
- Optimize input format

### Reducer Optimizations
- Balance partition distribution
- Use appropriate reducer count
- Enable speculative execution
- Optimize memory settings

### General Optimizations
- Enable compression (Snappy, Gzip)
- Use combiners for local aggregation
- Optimize shuffle and sort
- Configure appropriate memory settings

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create feature branch
3. Make changes
4. Add tests
5. Submit pull request

### Code Style
- Follow Java coding conventions
- Add comprehensive documentation
- Include unit tests
- Use proper logging

### Testing Requirements
- All new code must have tests
- Maintain >80% code coverage
- Integration tests for new features
- Performance benchmarks for optimizations

## 📞 Support

### Getting Help
- **Issues**: [GitHub Issues](https://github.com/your-repo/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-repo/discussions)
- **Wiki**: [Project Wiki](https://github.com/your-repo/wiki)

### Community
- **Slack**: Join our community channel
- **Mailing List**: Subscribe to updates
- **Blog**: Read latest news and tutorials

## 📄 License

This project is licensed under the Apache License 2.0. See [LICENSE](../LICENSE) for details.

## 🙏 Acknowledgments

- Apache Hadoop community
- Contributors and maintainers
- Sample data providers
- Open source community
