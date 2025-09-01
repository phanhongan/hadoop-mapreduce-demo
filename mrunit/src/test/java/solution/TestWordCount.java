package solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Comprehensive test suite for the WordCount MapReduce job using MRUnit.
 * 
 * This test class demonstrates how to test MapReduce jobs using MRUnit,
 * including individual mapper and reducer testing, as well as end-to-end
 * MapReduce pipeline testing.
 * 
 * Features:
 * - Modern JUnit 5 testing framework
 * - Comprehensive test coverage
 * - Proper test organization with @Nested classes
 * - Detailed test descriptions with @DisplayName
 * - Logging for test execution monitoring
 * - Edge case testing
 * - Performance testing considerations
 */
@DisplayName("WordCount MapReduce Job Tests")
public class TestWordCount {

    private static final Logger logger = LoggerFactory.getLogger(TestWordCount.class);

    /*
     * Declare harnesses that let you test a mapper, a reducer, and a mapper and
     * a reducer working together.
     */
    private MapDriver<LongWritable, Text, Text, IntWritable> mapDriver;
    private ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;
    private MapReduceDriver<LongWritable, Text, Text, IntWritable, Text, IntWritable> mapReduceDriver;

    /*
     * Set up the test. This method will be called before every test.
     */
    @BeforeEach
    public void setUp() {
        logger.debug("Setting up test harnesses");

        /*
         * Set up the mapper test harness.
         */
        WordMapper mapper = new WordMapper();
        mapDriver = new MapDriver<>();
        mapDriver.setMapper(mapper);

        /*
         * Set up the reducer test harness.
         */
        SumReducer reducer = new SumReducer();
        reduceDriver = new ReduceDriver<>();
        reduceDriver.setReducer(reducer);

        /*
         * Set up the mapper/reducer test harness.
         */
        mapReduceDriver = new MapReduceDriver<>();
        mapReduceDriver.setMapper(mapper);
        mapReduceDriver.setReducer(reducer);

        logger.debug("Test harnesses initialized successfully");
    }

    @Nested
    @DisplayName("Mapper Tests")
    class MapperTests {

        @Test
        @DisplayName("Should process basic word input correctly")
        public void testMapper() throws IOException {
            logger.info("Testing basic mapper functionality");

            /*
             * For this test, the mapper's input will be "1 cat cat dog"
             */
            mapDriver.withInput(new LongWritable(1), new Text("cat cat dog"));

            /*
             * The expected output is "cat 1", "cat 1", and "dog 1".
             */
            mapDriver.withOutput(new Text("cat"), new IntWritable(1));
            mapDriver.withOutput(new Text("cat"), new IntWritable(1));
            mapDriver.withOutput(new Text("dog"), new IntWritable(1));

            /*
             * Run the test.
             */
            mapDriver.runTest();
            logger.info("Basic mapper test completed successfully");
        }

        @Test
        @DisplayName("Should handle empty input gracefully")
        public void testMapperWithEmptyInput() throws IOException {
            logger.info("Testing mapper with empty input");

            mapDriver.withInput(new LongWritable(1), new Text(""));
            mapDriver.runTest();
            logger.info("Empty input test completed successfully");
        }

        @Test
        @DisplayName("Should handle single word input")
        public void testMapperWithSingleWord() throws IOException {
            logger.info("Testing mapper with single word input");

            mapDriver.withInput(new LongWritable(1), new Text("hello"));
            mapDriver.withOutput(new Text("hello"), new IntWritable(1));

            mapDriver.runTest();
            logger.info("Single word test completed successfully");
        }

        @Test
        @DisplayName("Should handle mixed case and punctuation")
        public void testMapperWithMixedCaseAndPunctuation() throws IOException {
            logger.info("Testing mapper with mixed case and punctuation");

            mapDriver.withInput(new LongWritable(1), new Text("Hello, World! Hello."));
            mapDriver.withOutput(new Text("hello"), new IntWritable(1));
            mapDriver.withOutput(new Text("world"), new IntWritable(1));
            mapDriver.withOutput(new Text("hello"), new IntWritable(1));

            mapDriver.runTest();
            logger.info("Mixed case and punctuation test completed successfully");
        }
    }

    @Nested
    @DisplayName("Reducer Tests")
    class ReducerTests {

        @Test
        @DisplayName("Should aggregate word counts correctly")
        public void testReducer() throws IOException {
            logger.info("Testing basic reducer functionality");

            List<IntWritable> values = new ArrayList<>();
            values.add(new IntWritable(1));
            values.add(new IntWritable(1));

            /*
             * For this test, the reducer's input will be "cat 1 1".
             */
            reduceDriver.withInput(new Text("cat"), values);

            /*
             * The expected output is "cat 2"
             */
            reduceDriver.withOutput(new Text("cat"), new IntWritable(2));

            /*
             * Run the test.
             */
            reduceDriver.runTest();
            logger.info("Basic reducer test completed successfully");
        }

        @Test
        @DisplayName("Should handle single value input")
        public void testReducerWithSingleValue() throws IOException {
            logger.info("Testing reducer with single value");

            List<IntWritable> values = new ArrayList<>();
            values.add(new IntWritable(5));

            reduceDriver.withInput(new Text("unique"), values);
            reduceDriver.withOutput(new Text("unique"), new IntWritable(5));

            reduceDriver.runTest();
            logger.info("Single value test completed successfully");
        }

        @Test
        @DisplayName("Should handle multiple values correctly")
        public void testReducerWithMultipleValues() throws IOException {
            logger.info("Testing reducer with multiple values");

            List<IntWritable> values = new ArrayList<>();
            values.add(new IntWritable(1));
            values.add(new IntWritable(2));
            values.add(new IntWritable(3));
            values.add(new IntWritable(4));

            reduceDriver.withInput(new Text("sum"), values);
            reduceDriver.withOutput(new Text("sum"), new IntWritable(10));

            reduceDriver.runTest();
            logger.info("Multiple values test completed successfully");
        }

        @Test
        @DisplayName("Should handle zero values")
        public void testReducerWithZeroValues() throws IOException {
            logger.info("Testing reducer with zero values");

            List<IntWritable> values = new ArrayList<>();

            reduceDriver.withInput(new Text("empty"), values);
            reduceDriver.withOutput(new Text("empty"), new IntWritable(0));

            reduceDriver.runTest();
            logger.info("Zero values test completed successfully");
        }
    }

    @Nested
    @DisplayName("MapReduce Integration Tests")
    class MapReduceIntegrationTests {

        @Test
        @DisplayName("Should process complete MapReduce pipeline correctly")
        public void testMapReduce() throws IOException {
            logger.info("Testing complete MapReduce pipeline");

            /*
             * For this test, the mapper's input will be "1 cat cat dog"
             */
            mapReduceDriver.withInput(new LongWritable(1), new Text("cat cat dog"));

            /*
             * The expected output (from the reducer) is "cat 2", "dog 1".
             */
            mapReduceDriver.addOutput(new Text("cat"), new IntWritable(2));
            mapReduceDriver.addOutput(new Text("dog"), new IntWritable(1));

            /*
             * Run the test.
             */
            mapReduceDriver.runTest();
            logger.info("Complete MapReduce pipeline test completed successfully");
        }

        @Test
        @DisplayName("Should handle complex text input")
        public void testMapReduceWithComplexInput() throws IOException {
            logger.info("Testing MapReduce with complex input");

            mapReduceDriver.withInput(new LongWritable(1), new Text("the quick brown fox jumps over the lazy dog"));
            mapReduceDriver.addOutput(new Text("the"), new IntWritable(2));
            mapReduceDriver.addOutput(new Text("quick"), new IntWritable(1));
            mapReduceDriver.addOutput(new Text("brown"), new IntWritable(1));
            mapReduceDriver.addOutput(new Text("fox"), new IntWritable(1));
            mapReduceDriver.addOutput(new Text("jumps"), new IntWritable(1));
            mapReduceDriver.addOutput(new Text("over"), new IntWritable(1));
            mapReduceDriver.addOutput(new Text("lazy"), new IntWritable(1));
            mapReduceDriver.addOutput(new Text("dog"), new IntWritable(1));

            mapReduceDriver.runTest();
            logger.info("Complex input test completed successfully");
        }

        @Test
        @DisplayName("Should handle multiple input lines")
        public void testMapReduceWithMultipleLines() throws IOException {
            logger.info("Testing MapReduce with multiple input lines");

            mapReduceDriver.withInput(new LongWritable(1), new Text("hello world"));
            mapReduceDriver.withInput(new LongWritable(2), new Text("hello hadoop"));
            mapReduceDriver.withInput(new LongWritable(3), new Text("world mapreduce"));

            mapReduceDriver.addOutput(new Text("hello"), new IntWritable(2));
            mapReduceDriver.addOutput(new Text("world"), new IntWritable(2));
            mapReduceDriver.addOutput(new Text("hadoop"), new IntWritable(1));
            mapReduceDriver.addOutput(new Text("mapreduce"), new IntWritable(1));

            mapReduceDriver.runTest();
            logger.info("Multiple lines test completed successfully");
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very long words")
        public void testVeryLongWords() throws IOException {
            logger.info("Testing with very long words");

            String longWord = "a".repeat(1000);
            mapDriver.withInput(new LongWritable(1), new Text(longWord));
            mapDriver.withOutput(new Text(longWord), new IntWritable(1));

            mapDriver.runTest();
            logger.info("Very long words test completed successfully");
        }

        @Test
        @DisplayName("Should handle special characters")
        public void testSpecialCharacters() throws IOException {
            logger.info("Testing with special characters");

            mapDriver.withInput(new LongWritable(1), new Text("word@#$%^&*()_+{}|:<>?[]\\;'\",./"));
            mapDriver.withOutput(new Text("word"), new IntWritable(1));

            mapDriver.runTest();
            logger.info("Special characters test completed successfully");
        }
    }
}
