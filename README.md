# Hadoop MapReduce Learning Project

A comprehensive collection of Hadoop MapReduce examples designed for learning and understanding big data processing concepts.

## 🎯 Learning Objectives

- **Understand MapReduce**: Learn the fundamental concepts of distributed data processing
- **Practice with Examples**: Work through 12 different MapReduce patterns and use cases
- **Modern Development**: Use current Hadoop 3.x APIs and Java 17
- **Hands-on Experience**: Build and run real MapReduce jobs
- **Real-World Applications**: Understand how these patterns are used in production systems
- **Industry Relevance**: Learn concepts that apply to modern big data technologies

## 🚀 Features

- **Word Count**: Classic MapReduce example for counting word frequencies
- **Average Word Length**: Demonstrates custom Writable types and aggregation
- **Log Analysis**: Real-world log processing with IP validation
- **Custom Partitioning**: Month-based data distribution across reducers
- **Counters**: Using Hadoop counters for job statistics
- **Sequence Files**: Working with Hadoop's binary file format
- **Custom Input Formats**: Fixed-width column text processing
- **MRUnit Testing**: Unit testing for MapReduce jobs
- **ToolRunner**: Command-line argument handling
- **Combiners**: Local aggregation optimization
- **Custom Writables**: Creating custom data types
- **Logging**: Comprehensive logging throughout jobs

## 🛠️ Technology Stack

- **Hadoop**: 3.4.0 (latest stable)
- **Java**: 17 LTS
- **Maven**: 3.9.x
- **SLF4J + Logback**: Modern logging
- **JUnit 5**: Unit testing framework
- **MRUnit**: MapReduce testing framework

## 🌟 Why MapReduce Still Matters Today

While newer technologies like Apache Spark have gained popularity, MapReduce remains highly relevant for learning and understanding big data concepts:

### **Foundation for Modern Technologies**
- **Apache Spark**: Built on similar distributed computing principles
- **Apache Flink**: Stream processing uses similar patterns
- **Cloud Platforms**: AWS EMR, Google Dataproc, Azure HDInsight all support MapReduce
- **Data Lakes**: Many organizations still use MapReduce for batch processing

### **Industry Applications**
- **Financial Services**: Risk analysis, fraud detection, regulatory reporting
- **Healthcare**: Medical data processing, drug discovery, patient analytics
- **E-commerce**: Recommendation systems, inventory management, customer analytics
- **Media & Entertainment**: Content analysis, user behavior tracking, recommendation engines

### **Learning Value**
- **Distributed Systems**: Understanding how data is processed across multiple machines
- **Scalability**: Learning to design systems that handle massive datasets
- **Data Processing Patterns**: Mastering aggregation, filtering, and transformation techniques
- **Performance Optimization**: Understanding bottlenecks and optimization strategies

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.9+
- Basic understanding of Java programming

**For detailed prerequisites and setup instructions, see the [Tutorial](docs/TUTORIAL.md).**

## 🚀 Quick Start

### 1. Clone and Build
```bash
git clone <repository-url>
cd hadoop-mapreduce-demo
./scripts/build.sh
```

### 2. Run WordCount Example
```bash
./scripts/run-wordcount.sh
```

### 3. Explore Other Examples
```bash
# Build all modules to examine source code
mvn clean compile

# For full Hadoop execution (requires cluster setup):
# mvn exec:java -pl wordcount -Dexec.mainClass="solution.WordCount" -Dexec.args="input/shakespeare.txt output/wordcount"
```

## 📁 Project Structure

```
├── wordcount/                 # Basic word counting example
├── averagewordlength/         # Average word length calculation
├── log_file_analysis/         # Log processing examples
├── partitioner/               # Custom partitioning examples
├── counters/                  # Counter usage examples
├── createsequencefile/        # Sequence file creation
├── inputformat/               # Custom input format examples
├── mrunit/                    # Unit testing examples
├── toolrunner/                # Command-line tool examples
├── writables/                 # Custom Writable types
├── combiner/                  # Combiner optimization examples
├── logging/                   # Logging framework examples
├── input/                     # Sample input data
├── scripts/                   # Build and utility scripts
├── docker/                    # Docker configuration
└── docs/                      # Documentation
```

## 🔧 Quick Examples

### Word Count
The classic MapReduce example for counting word frequencies in text files.

### Log Analysis
Real-world log processing with IP validation and error handling.

### Custom Partitioning
Month-based data distribution across reducers for parallel processing.

**For detailed explanations, code examples, and real-world applications, see the [Tutorial](docs/TUTORIAL.md).**

## 🧪 Testing

For learning purposes, focus on examining the source code and running the working examples:

```bash
# Run the working WordCount example
./scripts/run-wordcount.sh

# Build and examine the source code
mvn clean compile
```

**Note**: The MRUnit tests have compatibility issues with Java 17. For learning MapReduce concepts, examining the source code and running the working examples is more valuable.

## 📚 Learning Path

### 🎯 Comprehensive Tutorial Available!
**NEW**: Check out our [Tutorial](docs/TUTORIAL.md) for a structured learning experience with:
- **Real-world applications** for each example
- **Practical context** explaining why each pattern matters
- **Progressive difficulty** from beginner to advanced
- **Hands-on exercises** and assessment checkpoints
- **Industry use cases** from companies like Google, Netflix, and Amazon

**Recommended approach**: Follow the [Tutorial](docs/TUTORIAL.md) for a structured learning experience with detailed explanations, real-world applications, and hands-on exercises.

## 🐳 Docker (Optional)

For containerized development:

```bash
# Build WordCount example
docker build -f docker/wordcount.Dockerfile -t hadoop-wordcount .

# Run WordCount
docker run -v $(pwd)/input:/app/input -v $(pwd)/output:/app/output \
  hadoop-wordcount input/shakespeare.txt output/wordcount-docker
```

## 📖 Documentation

- **[Tutorial](docs/TUTORIAL.md)**: Complete learning guide with real-world applications
- [Examples Guide](docs/examples.md): In-depth explanation of each example
- [Testing Guide](docs/testing.md): How to test MapReduce jobs
- [Troubleshooting](docs/troubleshooting.md): Common issues and solutions

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

### GitHub Workflows

This project includes comprehensive GitHub Actions workflows:

- **CI/CD Pipeline**: Automated build, test, and quality checks on every push and PR
- **Release Management**: Automated release creation and artifact packaging
- **Dependency Updates**: Weekly security scans and dependency updates
- **PR Validation**: Automated validation of pull requests

See [.github/workflows/README.md](.github/workflows/README.md) for detailed workflow documentation.

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Apache Hadoop community
- Contributors and maintainers
- Sample data providers (Project Gutenberg)

