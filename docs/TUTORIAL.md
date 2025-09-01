# Hadoop MapReduce Learning Tutorial

## 🎯 Overview

This tutorial provides a structured learning path through 12 essential MapReduce examples, each designed to teach specific concepts while demonstrating real-world applications. The examples progress from basic concepts to advanced techniques, ensuring students understand both the "how" and "why" of each pattern.

## 📚 Learning Objectives

By completing this tutorial, students will:
- Master fundamental MapReduce concepts and patterns
- Understand real-world applications of each technique
- Learn modern Hadoop 3.x APIs and best practices
- Gain hands-on experience with production-ready code
- Develop skills applicable to current big data technologies

**Note**: This tutorial complements the main [README](README.md) which provides project overview, technology stack, and quick start instructions.

## 🛠️ Prerequisites

- **Java 17+** knowledge and programming experience
- **Maven 3.9+** familiarity for building projects
- **Basic understanding** of distributed systems concepts
- **Command line** proficiency for running examples
- **Text editor or IDE** for examining and modifying code

## 🚀 Getting Started

**Prerequisites**: Ensure you have Java 17+, Maven 3.9+, and basic Java programming knowledge.

**Setup**: Follow the [Quick Start](README.md#-quick-start) section in the main README for environment setup and initial project build.

**Project Structure**: Each module focuses on specific MapReduce concepts and is self-contained with its own source code, tests, and documentation.

---

## 📖 Tutorial Modules

### Module 1: Foundation - Word Count
**Difficulty**: ⭐  
**Concepts**: Basic MapReduce, Mapper, Reducer, Job Configuration

#### What You'll Learn
- The fundamental MapReduce programming model
- How data flows through mapper and reducer phases
- Basic job configuration and execution

#### Real-World Applications
- **Search Engines**: Counting term frequencies for ranking algorithms
- **Content Analysis**: Analyzing social media posts, articles, or documents
- **Data Quality**: Identifying most common values in datasets
- **Natural Language Processing**: Building vocabulary from text corpora
- **Business Intelligence**: Analyzing customer feedback or support tickets

#### Practical Example
```java
// This pattern is used in:
// - Google's PageRank algorithm (counting links)
// - Twitter's trending topics (counting hashtags)
// - E-commerce recommendation systems (counting product views)
```

#### Hands-On Exercise
```bash
# Run the word count example
./scripts/run-wordcount.sh

# Examine the output
cat output/wordcount/part-r-00000 | head -20
```

#### Key Takeaways
- MapReduce is perfect for aggregation tasks
- The shuffle-and-sort phase is automatic but crucial
- Always validate input and handle edge cases

---

### Module 2: Intermediate - Average Word Length
**Difficulty**: ⭐⭐  
**Concepts**: Custom Writable types, Different input/output types, Aggregation

#### What You'll Learn
- How to handle different data types between mapper and reducer
- Creating custom Writable types for complex data
- Advanced aggregation patterns

#### Real-World Applications
- **Text Analysis**: Measuring readability scores of documents
- **Language Processing**: Analyzing linguistic patterns across languages
- **Content Optimization**: Determining optimal content length for engagement
- **Academic Research**: Studying writing complexity in literature
- **SEO Analysis**: Optimizing content length for search engines

#### Practical Example
```java
// This pattern is used in:
// - Educational platforms (analyzing student writing complexity)
// - Content management systems (optimizing article length)
// - Language learning apps (measuring text difficulty)
```

#### Hands-On Exercise
```bash
# Build and run the average word length example
mvn clean compile -pl averagewordlength
mvn exec:java -pl averagewordlength -Dexec.mainClass="solution.AvgWordLength" \
  -Dexec.args="input/shakespeare.txt output/avgwordlength"
```

#### Key Takeaways
- Different data types require careful type management
- Custom Writables provide type safety and serialization
- Aggregation patterns are fundamental to data analysis

---

### Module 3: Real-World - Log File Analysis
**Difficulty**: ⭐⭐  
**Concepts**: Log parsing, Data validation, Error handling, Real-world data processing

#### What You'll Learn
- Processing structured log data
- Implementing robust data validation
- Handling malformed input gracefully
- Real-world error handling patterns

#### Real-World Applications
- **Web Analytics**: Analyzing website traffic patterns
- **Security Monitoring**: Detecting suspicious access patterns
- **Performance Analysis**: Identifying slow endpoints
- **Capacity Planning**: Understanding system usage patterns
- **Compliance**: Auditing system access for regulatory requirements

#### Practical Example
```java
// This pattern is used in:
// - Netflix (analyzing streaming logs for recommendations)
// - Amazon (monitoring e-commerce traffic)
// - Google (analyzing search query logs)
// - Banks (monitoring transaction logs for fraud detection)
```

#### Hands-On Exercise
```bash
# Create sample log data
echo '192.168.1.1 - - [25/Dec/2023:10:00:01 -0500] "GET /api/users HTTP/1.1" 200 1234' > input/sample.log
echo '10.0.0.1 - - [25/Dec/2023:10:00:02 -0500] "POST /api/login HTTP/1.1" 401 567' >> input/sample.log

# Run log analysis
mvn exec:java -pl log_file_analysis -Dexec.mainClass="solution.ProcessLogs" \
  -Dexec.args="input/sample.log output/loganalysis"
```

#### Key Takeaways
- Real-world data is messy and requires robust parsing
- Validation is crucial for data quality
- Error handling prevents job failures

---

### Module 4: Advanced - Custom Partitioning
**Difficulty**: ⭐⭐⭐  
**Concepts**: Data distribution, Load balancing, Custom partitioners, Parallel processing

#### What You'll Learn
- How to control data distribution across reducers
- Implementing custom partitioning logic
- Load balancing strategies
- Optimizing parallel processing

#### Real-World Applications
- **Time-Series Analysis**: Partitioning data by time periods (monthly reports)
- **Geographic Analysis**: Partitioning by regions or countries
- **User Segmentation**: Partitioning by user demographics
- **Product Categorization**: Partitioning by product categories
- **Financial Analysis**: Partitioning by account types or regions

#### Practical Example
```java
// This pattern is used in:
// - Uber (partitioning ride data by city)
// - Spotify (partitioning music data by genre)
// - Airbnb (partitioning booking data by region)
// - Financial institutions (partitioning transactions by account type)
```

#### Hands-On Exercise
```bash
# Run the custom partitioning example
mvn exec:java -pl partitioner -Dexec.mainClass="solution.ProcessLogs" \
  -Dexec.args="input/logs output/partitioned"

# Examine the partitioned output
ls output/partitioned/
cat output/partitioned/part-r-00000  # January data
cat output/partitioned/part-r-00001  # February data
```

#### Key Takeaways
- Custom partitioning enables efficient parallel processing
- Load balancing is crucial for performance
- Partitioning strategy depends on data characteristics

---

### Module 5: Monitoring - Counters
**Difficulty**: ⭐⭐  
**Concepts**: Job monitoring, Metrics collection, Performance tracking, Debugging

#### What You'll Learn
- Using Hadoop counters for job monitoring
- Implementing custom metrics
- Performance tracking and debugging
- Production monitoring best practices

#### Real-World Applications
- **Data Quality Monitoring**: Tracking data quality metrics
- **Performance Monitoring**: Measuring job performance
- **Business Metrics**: Tracking business-specific KPIs
- **Error Tracking**: Monitoring error rates and types
- **Resource Usage**: Tracking resource consumption

#### Practical Example
```java
// This pattern is used in:
// - Netflix (monitoring content processing metrics)
// - LinkedIn (tracking data pipeline health)
// - Twitter (monitoring tweet processing metrics)
// - Banks (tracking transaction processing metrics)
```

#### Hands-On Exercise
```bash
# Run the counter example
mvn exec:java -pl counters -Dexec.mainClass="solution.ImageCounter" \
  -Dexec.args="input/images output/counters"

# Check the counter output in the logs
```

#### Key Takeaways
- Counters provide valuable insights into job execution
- Custom counters enable business-specific monitoring
- Monitoring is essential for production systems

---

### Module 6: Storage - Sequence Files
**Difficulty**: ⭐⭐⭐  
**Concepts**: Binary file formats, Compression, Storage optimization, Data serialization

#### What You'll Learn
- Working with Hadoop's binary file formats
- Implementing compression for storage optimization
- Understanding serialization and deserialization
- Storage format selection strategies

#### Real-World Applications
- **Data Archiving**: Long-term storage of processed data
- **Data Exchange**: Efficient data transfer between systems
- **Backup Systems**: Compressed backup of critical data
- **Data Lakes**: Storing structured data efficiently
- **ETL Pipelines**: Intermediate storage in data processing

#### Practical Example
```java
// This pattern is used in:
// - Facebook (storing user interaction data)
// - Google (archiving search logs)
// - Amazon (storing product catalog data)
// - Financial institutions (archiving transaction data)
```

#### Hands-On Exercise
```bash
# Create a compressed sequence file
mvn exec:java -pl createsequencefile -Dexec.mainClass="solution.CreateCompressedSequenceFile" \
  -Dexec.args="input/shakespeare.txt output/compressed"

# Read the sequence file
mvn exec:java -pl createsequencefile -Dexec.mainClass="solution.ReadCompressedSequenceFile" \
  -Dexec.args="output/compressed"
```

#### Key Takeaways
- Binary formats are more efficient than text
- Compression significantly reduces storage costs
- Choose formats based on access patterns

---

### Module 7: Custom Input - Input Formats
**Difficulty**: ⭐⭐⭐  
**Concepts**: Custom data ingestion, Record readers, Data parsing, Input format design

#### What You'll Learn
- Creating custom input formats for specialized data
- Implementing record readers
- Handling complex data structures
- Designing efficient data ingestion

#### Real-World Applications
- **Legacy System Integration**: Processing data from legacy systems
- **Binary Data Processing**: Handling proprietary data formats
- **Database Integration**: Processing data from various databases
- **API Data Ingestion**: Processing data from REST APIs
- **Sensor Data**: Processing IoT sensor data

#### Practical Example
```java
// This pattern is used in:
// - Healthcare (processing medical records)
// - Manufacturing (processing sensor data)
// - Finance (processing trading data)
// - IoT platforms (processing device data)
```

#### Hands-On Exercise
```bash
# Create sample columnar data
echo "John    25    Engineer   50000" > input/columntext-testdata/employee.txt
echo "Jane    30    Manager    75000" >> input/columntext-testdata/employee.txt

# Run the custom input format example
mvn exec:java -pl inputformat -Dexec.mainClass="example.ColumnTextDriver" \
  -Dexec.args="input/columntext-testdata output/processed"
```

#### Key Takeaways
- Custom input formats enable processing of specialized data
- Record readers control how data is parsed
- Efficient parsing is crucial for performance

---

### Module 8: Testing - MRUnit
**Difficulty**: ⭐⭐  
**Concepts**: Unit testing, Test-driven development, MapReduce testing, Quality assurance

#### What You'll Learn
- Testing MapReduce jobs with MRUnit
- Test-driven development practices
- Quality assurance for big data applications
- Debugging and validation techniques

#### Real-World Applications
- **Quality Assurance**: Ensuring data processing correctness
- **Regression Testing**: Preventing bugs in production
- **Performance Testing**: Validating performance requirements
- **Data Validation**: Ensuring data quality
- **Continuous Integration**: Automated testing in CI/CD pipelines

#### Practical Example
```java
// This pattern is used in:
// - All major tech companies for data pipeline testing
// - Financial institutions for transaction processing validation
// - Healthcare for medical data processing validation
// - E-commerce for recommendation system testing
```

#### Hands-On Exercise
```bash
# Run the MRUnit tests
mvn test -pl mrunit

# Examine test results
cat target/surefire-reports/TEST-solution.TestWordCount.xml
```

#### Key Takeaways
- Testing is crucial for data processing applications
- MRUnit enables comprehensive testing
- Test-driven development improves code quality

---

### Module 9: CLI Tools - ToolRunner
**Difficulty**: ⭐⭐  
**Concepts**: Command-line interfaces, Configuration management, Tool integration, User experience

#### What You'll Learn
- Creating command-line tools with ToolRunner
- Managing configuration parameters
- Integrating with Hadoop's configuration system
- Building user-friendly interfaces

#### Real-World Applications
- **Data Pipeline Tools**: Command-line data processing tools
- **Administrative Scripts**: System administration and monitoring
- **ETL Tools**: Extract, transform, load operations
- **Data Migration**: Moving data between systems
- **Backup and Recovery**: Data backup and restoration tools

#### Practical Example
```java
// This pattern is used in:
// - Apache Spark (spark-submit command)
// - Apache Kafka (kafka-console-producer/consumer)
// - Database tools (mysqldump, pg_dump)
// - Cloud tools (AWS CLI, gcloud)
```

#### Hands-On Exercise
```bash
# Run with different configurations
mvn exec:java -pl toolrunner -Dexec.mainClass="solution.AvgWordLength" \
  -Dexec.args="input/shakespeare.txt output/avgwordlength"

# Try with custom configuration
mvn exec:java -pl toolrunner -Dexec.mainClass="solution.AvgWordLength" \
  -Dexec.args="-DcaseSensitive=false input/shakespeare.txt output/avgwordlength"
```

#### Key Takeaways
- Good CLI tools improve user experience
- Configuration management is essential
- ToolRunner simplifies Hadoop integration

---

### Module 10: Optimization - Combiners
**Difficulty**: ⭐⭐⭐  
**Concepts**: Performance optimization, Local aggregation, Network optimization, Resource efficiency

#### What You'll Learn
- Implementing combiners for performance optimization
- Understanding local aggregation benefits
- Network traffic reduction strategies
- Resource efficiency optimization

#### Real-World Applications
- **High-Volume Processing**: Optimizing large-scale data processing
- **Network Optimization**: Reducing data transfer costs
- **Resource Management**: Optimizing cluster resource usage
- **Cost Optimization**: Reducing cloud computing costs
- **Performance Tuning**: Improving job execution times

#### Practical Example
```java
// This pattern is used in:
// - Google (optimizing search index generation)
// - Facebook (optimizing social graph processing)
// - Twitter (optimizing tweet processing)
// - LinkedIn (optimizing professional network analysis)
```

#### Hands-On Exercise
```bash
# Run with combiner optimization
mvn exec:java -pl combiner -Dexec.mainClass="solution.WordCountDriver" \
  -Dexec.args="input/shakespeare.txt output/wordcount-combined"

# Compare performance with and without combiner
time mvn exec:java -pl wordcount -Dexec.mainClass="solution.WordCount" \
  -Dexec.args="input/shakespeare.txt output/wordcount-no-combiner"
```

#### Key Takeaways
- Combiners significantly improve performance
- Local aggregation reduces network traffic
- Optimization is crucial for large-scale processing

---

### Module 11: Custom Types - Writables
**Difficulty**: ⭐⭐⭐  
**Concepts**: Custom data types, Serialization, Type safety, Data modeling

#### What You'll Learn
- Creating custom Writable types
- Implementing efficient serialization
- Ensuring type safety in MapReduce
- Designing data models for big data

#### Real-World Applications
- **Complex Data Structures**: Processing nested or complex data
- **Domain-Specific Types**: Creating types for specific business domains
- **Performance Optimization**: Optimizing serialization performance
- **Type Safety**: Ensuring data integrity
- **API Design**: Creating clean, type-safe APIs

#### Practical Example
```java
// This pattern is used in:
// - Apache Avro (schema evolution)
// - Apache Parquet (columnar storage)
// - Protocol Buffers (efficient serialization)
// - Custom business objects in enterprise applications
```

#### Hands-On Exercise
```bash
# Run the custom writable example
mvn exec:java -pl writables -Dexec.mainClass="solution.StringPairTestDriver" \
  -Dexec.args="input/shakespeare.txt output/stringpairs"

# Examine the custom type output
cat output/stringpairs/part-r-00000
```

#### Key Takeaways
- Custom types enable complex data processing
- Efficient serialization is crucial for performance
- Type safety prevents runtime errors

---

### Module 12: Production - Logging
**Difficulty**: ⭐⭐  
**Concepts**: Production logging, Monitoring, Debugging, Operational excellence

#### What You'll Learn
- Implementing comprehensive logging
- Production monitoring strategies
- Debugging techniques for distributed systems
- Operational best practices

#### Real-World Applications
- **Production Monitoring**: Monitoring live systems
- **Debugging**: Troubleshooting production issues
- **Performance Analysis**: Analyzing system performance
- **Compliance**: Meeting regulatory logging requirements
- **Auditing**: Tracking system operations

#### Practical Example
```java
// This pattern is used in:
// - All production systems for monitoring
// - Financial systems for compliance
// - Healthcare systems for auditing
// - E-commerce for performance monitoring
```

#### Hands-On Exercise
```bash
# Run with comprehensive logging
mvn exec:java -pl logging -Dexec.mainClass="solution.AvgWordLength" \
  -Dexec.args="input/shakespeare.txt output/logging"

# Check the logs
tail -f logs/hadoop-mapreduce-demo.log
```

#### Key Takeaways
- Comprehensive logging is essential for production
- Monitoring enables proactive issue detection
- Good logging practices improve maintainability

---

## 🎯 Learning Path Recommendations

### For Beginners (0-6 months experience)
1. **Start with Module 1** (Word Count) - Build foundation
2. **Progress to Module 2** (Average Word Length) - Learn custom types
3. **Try Module 3** (Log Analysis) - Real-world application
4. **Practice with Module 8** (Testing) - Quality assurance

### For Intermediate Developers (6 months - 2 years)
1. **Review Modules 1-3** - Solidify fundamentals
2. **Focus on Module 4** (Partitioning) - Advanced concepts
3. **Study Module 5** (Counters) - Monitoring
4. **Explore Module 10** (Combiners) - Optimization

### For Advanced Developers (2+ years experience)
1. **Deep dive into Module 6** (Sequence Files) - Storage optimization
2. **Master Module 7** (Input Formats) - Custom data processing
3. **Implement Module 11** (Writables) - Custom types
4. **Focus on Module 12** (Logging) - Production readiness

## 🔧 Practical Exercises

### Exercise 1: Build a Recommendation System
Using the word count pattern, build a simple recommendation system that:
- Counts user-item interactions
- Identifies popular items
- Suggests items based on user preferences

### Exercise 2: Analyze Social Media Data
Using log analysis techniques, analyze social media data to:
- Identify trending topics
- Detect sentiment patterns
- Monitor engagement metrics

### Exercise 3: Optimize Data Processing
Using combiner and partitioning techniques, optimize a data processing pipeline for:
- Large-scale data processing
- Network efficiency
- Resource utilization

## 📊 Assessment and Progress Tracking

### Knowledge Checkpoints
After each module, test your understanding:
1. **Conceptual**: Can you explain the concept in your own words?
2. **Practical**: Can you implement a similar example?
3. **Application**: Can you identify real-world use cases?

### Portfolio Projects
Build these projects to demonstrate your skills:
1. **Data Pipeline**: End-to-end data processing pipeline
2. **Analytics Dashboard**: Real-time analytics system
3. **ETL Tool**: Extract, transform, load tool for specific domain

## 🚀 Next Steps

After completing this tutorial:
1. **Explore Apache Spark**: Modern alternative to MapReduce
2. **Learn Apache Kafka**: Real-time data streaming
3. **Study Apache Flink**: Stream processing framework
4. **Investigate Cloud Platforms**: AWS EMR, Google Dataproc, Azure HDInsight

## 📚 Additional Resources

- [Apache Hadoop Documentation](https://hadoop.apache.org/docs/)
- [MapReduce Tutorial](https://hadoop.apache.org/docs/current/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html)
- [Hadoop: The Definitive Guide](https://www.oreilly.com/library/view/hadoop-the-definitive/9781491901687/)
- [Big Data Analytics with Hadoop](https://www.packtpub.com/product/big-data-analytics-with-hadoop-3/9781788628846)

## 🤝 Community and Support

- **GitHub Issues**: Report bugs and request features
- **Discussions**: Ask questions and share experiences
- **Contributing**: Help improve the tutorial and examples

---

*This tutorial is designed to provide both theoretical understanding and practical experience with Hadoop MapReduce. Each module builds upon previous concepts while introducing new techniques and real-world applications. Take your time with each module, experiment with the code, and don't hesitate to explore beyond the examples provided.*
