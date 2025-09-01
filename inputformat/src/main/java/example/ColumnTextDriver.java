package example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Column Text Input Format MapReduce job driver class.
 * 
 * This class demonstrates the use of custom input format (ColumnTextInputFormat)
 * to process text files with column-based data. It's a map-only job that
 * uses the custom input format to extract specific columns from text files.
 * 
 * Features:
 * - Modern Hadoop 3.x API usage
 * - Proper logging with SLF4J
 * - Command-line argument validation
 * - Job configuration and execution
 * - Error handling and exit codes
 * - Custom input format integration
 */
public class ColumnTextDriver implements Tool {

    private static final Logger logger = LoggerFactory.getLogger(ColumnTextDriver.class);
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
            System.err.println("Example: ColumnTextDriver input/columns output/processed");
            return -1;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        logger.info("Starting Column Text Input Format job with input: {} and output: {}", inputPath, outputPath);

        try {
            // Create job configuration
            Job job = Job.getInstance(getConf(), "Column Text Input Format");
            
            // Set the jar file that contains the driver
            job.setJarByClass(ColumnTextDriver.class);
            
            // Set input and output paths
            FileInputFormat.setInputPaths(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            
            // Set custom input format class
            job.setInputFormatClass(ColumnTextInputFormat.class);
            
            // This is a map-only job
            job.setNumReduceTasks(0);
            
            // Enable speculative execution for better performance
            job.setSpeculativeExecution(true);
            
            logger.info("Job configuration completed with custom ColumnTextInputFormat");
            logger.info("Starting job execution...");
            
            // Submit the job and wait for completion
            boolean success = job.waitForCompletion(true);
            
            if (success) {
                logger.info("Column Text Input Format job completed successfully");
                return 0;
            } else {
                logger.error("Column Text Input Format job failed");
                return 1;
            }
            
        } catch (Exception e) {
            logger.error("Error during Column Text Input Format job execution", e);
            throw e;
        }
    }

    /**
     * Main method to run the Column Text Input Format job.
     * 
     * @param args Command line arguments: [input_dir, output_dir]
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        // Set Hadoop configuration properties
        conf.set("mapreduce.job.reduces", "0");
        conf.set("mapreduce.map.output.compress", "true");
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");
        
        int exitCode = ToolRunner.run(conf, new ColumnTextDriver(), args);
        System.exit(exitCode);
    }
}
