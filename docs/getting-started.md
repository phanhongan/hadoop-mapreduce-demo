# Getting Started Guide

Welcome to the Hadoop MapReduce Demo! This guide will help you get up and running quickly.

## 🎯 What You'll Learn

By the end of this guide, you'll be able to:
- Set up your development environment
- Build and run your first MapReduce job
- Understand the basic concepts
- Deploy to different environments

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

### Required Software
- **Java**: Version 17 or higher
- **Maven**: Version 3.9 or higher
- **Git**: For version control

### Optional Software
- **Docker**: For containerized deployment
- **AWS CLI**: For cloud deployment
- **IntelliJ IDEA** or **Eclipse**: For development

### System Requirements
- **RAM**: Minimum 4GB, recommended 8GB+
- **Storage**: At least 2GB free space
- **OS**: Windows, macOS, or Linux

## 🚀 Quick Start (5 minutes)

### Step 1: Clone the Repository
```bash
git clone <repository-url>
cd hadoop-mapreduce-demo
```

### Step 2: Verify Prerequisites
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check Git version
git --version
```

### Step 3: Build the Project
```bash
# Run the build script
./scripts/build.sh

# Or use Maven directly
mvn clean package
```

### Step 4: Run Your First Job
```bash
# Run WordCount example
./scripts/run-wordcount.sh

# Or use Maven
mvn exec:java -pl wordcount \
  -Dexec.mainClass="solution.WordCount" \
  -Dexec.args="input/shakespeare.txt output/wordcount"
```

### Step 5: View Results
```bash
# Check output files
ls -la output/wordcount/

# View top 10 most frequent words
sort -k2 -nr output/wordcount/part-r-00000 | head -10
```

🎉 **Congratulations!** You've successfully run your first MapReduce job!

## 🔍 Understanding What Happened

### The WordCount Job
1. **Input**: Shakespeare's complete works (text file)
2. **Mapper**: Splits text into words, emits (word, 1) pairs
3. **Reducer**: Aggregates counts for each word
4. **Output**: Word frequency list

### Key Concepts
- **MapReduce**: Programming model for processing large datasets
- **Mapper**: Processes input data and emits key-value pairs
- **Reducer**: Aggregates mapper output by key
- **Shuffle & Sort**: Framework automatically groups data by key

## 🏗️ Project Structure Explained

```
hadoop-mapreduce-demo/
├── wordcount/              # Your first example
│   ├── src/main/java/solution/
│   │   ├── WordCount.java      # Job driver
│   │   ├── WordMapper.java     # Mapper implementation
│   │   └── SumReducer.java     # Reducer implementation
│   └── pom.xml
├── input/                  # Sample data
│   └── shakespeare.txt     # Shakespeare's works
├── output/                 # Job results (generated)
├── scripts/                # Utility scripts
└── docs/                   # Documentation
```

## 🐳 Running with Docker

If you prefer containerized deployment:

```bash
# Build and run with Docker Compose
docker-compose up --build

# Or run individual container
docker run -v $(pwd)/input:/app/input \
           -v $(pwd)/output:/app/output \
           hadoop-mapreduce-demo
```

## 🔧 Development Workflow

### 1. Make Changes
Edit the source code in your preferred IDE:
- `WordMapper.java`: Modify word processing logic
- `SumReducer.java`: Change aggregation behavior
- `WordCount.java`: Adjust job configuration

### 2. Test Changes
```bash
# Run unit tests
mvn test

# Run specific test
mvn test -Dtest=TestWordCount
```

### 3. Build and Run
```bash
# Build changes
mvn clean package

# Run updated job
./scripts/run-wordcount.sh
```

### 4. Iterate
Repeat the cycle: make changes → test → build → run → analyze results

## 📊 Monitoring Your Jobs

### Logs
- **Console**: Real-time job progress
- **Files**: Detailed logs in `logs/` directory
- **Levels**: Adjust logging verbosity in `logback.xml`

### Counters
Built-in metrics for monitoring:
- Lines processed
- Words emitted
- Errors encountered
- Processing time

### Performance
Monitor resource usage:
- Memory consumption
- CPU utilization
- I/O operations
- Network traffic

## 🚨 Common Issues and Solutions

### Build Failures
```bash
# Clear Maven cache
mvn clean

# Update dependencies
mvn dependency:resolve

# Check Java version
java -version
```

### Runtime Errors
```bash
# Check input file exists
ls -la input/shakespeare.txt

# Verify output directory permissions
mkdir -p output/wordcount

# Enable debug logging
export HADOOP_ROOT_LOGGER=DEBUG,console
```

### Performance Issues
- Increase heap size: `-Xmx4g`
- Reduce reducer count
- Enable compression
- Use combiners

## 🔗 Next Steps

Now that you're comfortable with the basics:

1. **Explore Other Examples**:
   - `averagewordlength/`: Custom data types
   - `log_file_analysis/`: Real-world processing
   - `partitioner/`: Custom data distribution

2. **Learn Advanced Concepts**:
   - Custom input formats
   - Sequence files
   - Performance tuning
   - Cloud deployment

3. **Contribute**:
   - Add new examples
   - Improve documentation
   - Report issues
   - Submit pull requests

## 📚 Additional Resources

### Documentation
- [Architecture Overview](architecture.md)
- [Examples Guide](examples.md)
- [Performance Tuning](performance.md)
- [API Reference](api.md)

### External Resources
- [Apache Hadoop Documentation](https://hadoop.apache.org/docs/)
- [MapReduce Tutorial](https://hadoop.apache.org/docs/current/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html)
- [Hadoop Best Practices](https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/ClusterSetup.html)

### Community
- [GitHub Issues](https://github.com/your-repo/issues)
- [GitHub Discussions](https://github.com/your-repo/discussions)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/hadoop)

## 🆘 Need Help?

If you encounter issues:

1. **Check the logs** for error messages
2. **Review this guide** for common solutions
3. **Search existing issues** on GitHub
4. **Create a new issue** with detailed information
5. **Join discussions** for community help

---

**Happy MapReducing!** 🚀
