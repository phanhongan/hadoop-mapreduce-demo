# Hadoop MapReduce Learning Project

A comprehensive collection of Hadoop MapReduce examples designed for learning and understanding big data processing concepts.

## 🎯 Learning Objectives

- **Understand MapReduce**: Learn the fundamental concepts of distributed data processing
- **Practice with Examples**: Work through 12 different MapReduce patterns and use cases
- **Modern Development**: Use current Hadoop 3.x APIs and Java 17
- **Hands-on Experience**: Build and run real MapReduce jobs

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

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.9+
- Basic understanding of Java programming

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

## 🔧 Examples Explained

### Word Count
The classic MapReduce example that counts word frequencies in text files.

```java
// Mapper: Extract words from text
public void map(LongWritable key, Text value, Context context) {
    String[] words = value.toString().split("\\s+");
    for (String word : words) {
        context.write(new Text(word), new IntWritable(1));
    }
}

// Reducer: Sum up word counts
public void reduce(Text key, Iterable<IntWritable> values, Context context) {
    int sum = 0;
    for (IntWritable val : values) {
        sum += val.get();
    }
    context.write(key, new IntWritable(sum));
}
```

### Log Analysis
Process log files to extract meaningful insights:

```java
// Parse log entries and extract metrics
public void map(LongWritable key, Text value, Context context) {
    String[] parts = value.toString().split("\\s+");
    if (parts.length >= 4) {
        String ip = parts[0];
        String date = parts[3].substring(1, 12); // Extract date
        if (isValidIPAddress(ip)) {
            context.write(new Text(date), new IntWritable(1));
        }
    }
}
```

### Custom Partitioning
Partition data by month for parallel processing:

```java
public class MonthPartitioner extends Partitioner<Text, IntWritable> {
    @Override
    public int getPartition(Text key, IntWritable value, int numPartitions) {
        String month = extractMonth(key.toString());
        return month.hashCode() % numPartitions;
    }
}
```

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

1. **Start with WordCount**: Understand basic MapReduce concepts
2. **Explore Average Word Length**: Learn about custom Writable types
3. **Try Log Analysis**: Work with real-world data patterns
4. **Study Custom Partitioning**: Understand data distribution
5. **Practice with Counters**: Learn job monitoring
6. **Experiment with Sequence Files**: Work with binary data
7. **Build Custom Input Formats**: Understand data ingestion
8. **Write Tests**: Learn MRUnit testing
9. **Use ToolRunner**: Handle command-line arguments
10. **Optimize with Combiners**: Improve performance
11. **Create Custom Writables**: Build custom data types
12. **Add Logging**: Implement comprehensive logging

**Recommended approach**: Start with WordCount, then explore the source code of other examples to understand different MapReduce patterns.

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

- [Getting Started](docs/getting-started.md): Detailed setup instructions
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

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/your-repo/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-repo/discussions)
