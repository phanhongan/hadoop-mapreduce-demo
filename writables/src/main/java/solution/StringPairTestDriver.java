package solution;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StringPair Test Driver MapReduce job driver class.
 * 
 * This class demonstrates the use of custom Writable types (StringPairWritable)
 * as keys in MapReduce jobs. It uses the LongSumReducer to aggregate counts
 * for each string pair.
 * 
 * Features:
 * - Modern Hadoop 3.x API usage
 * - Custom Writable type integration
 * - Proper logging with SLF4J
 * - Command-line argument validation
 * - Job configuration and execution
 * - Error handling and exit codes
 * - Performance optimization
 * - LongSumReducer integration
 */
public class StringPairTestDriver implements Tool {

    private static final Logger logger = LoggerFactory.getLogger(StringPairTestDriver.class);
    private Configuration conf;

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }

    @Override
    public int run(String[] args) throws Exception {
        // Validate command-line arguments
        if (args.length != 2) {
            logger.error("Invalid number of arguments. Expected 2, got {}.", args.length);
            System.err.println("Usage: " + this.getClass().getName() + " <input dir> <output dir>");
            System.err.println("Example: StringPairTestDriver input/text output/stringpairs");
            return -1;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        logger.info("Starting StringPair Test Driver job with input: {} and output: {}", inputPath, outputPath);

        try {
            // Create job configuration
            Job job = Job.getInstance(getConf(), "Custom Writable Comparable");
            
            // Set the jar file that contains the driver
            job.setJarByClass(StringPairTestDriver.class);
            
            // Set input and output paths
            FileInputFormat.setInputPaths(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            
            /*
             * LongSumReducer is a Hadoop API class that sums values into
             * a LongWritable. It works with any key and value type, therefore
             * supports the new StringPairWritable as a key type.
             */
            job.setReducerClass(LongSumReducer.class);

            job.setMapperClass(StringPairMapper.class);
            
            /*
             * Set the key output class for the job
             */   
            job.setOutputKeyClass(StringPairWritable.class);
            
            /*
             * Set the value output class for the job
             */
            job.setOutputValueClass(LongWritable.class);
            
            // Set number of reduce tasks for better performance
            job.setNumReduceTasks(2);
            
            // Enable speculative execution for better performance
            job.setSpeculativeExecution(true);
            
            logger.info("Job configuration completed with StringPairWritable integration");
            logger.info("Starting job execution...");
            
            // Submit the job and wait for completion
            boolean success = job.waitForCompletion(true);
            
            if (success) {
                logger.info("StringPair Test Driver job completed successfully");
                return 0;
            } else {
                logger.error("StringPair Test Driver job failed");
                return 1;
            }
            
        } catch (Exception e) {
            logger.error("Error during StringPair Test Driver job execution", e);
            throw e;
        }
    }

    /**
     * Main method to run the StringPair Test Driver job.
     * 
     * @param args Command line arguments: [input_dir, output_dir]
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        // Set Hadoop configuration properties
        conf.set("mapreduce.job.reduces", "2");
        conf.set("mapreduce.map.output.compress", "true");
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");
        
        int exitCode = ToolRunner.run(conf, new StringPairTestDriver(), args);
        System.exit(exitCode);
    }
}
