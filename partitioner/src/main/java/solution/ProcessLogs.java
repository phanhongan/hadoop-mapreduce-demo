package solution;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log Processing with Custom Partitioning MapReduce job driver class.
 * 
 * This class demonstrates MapReduce with custom partitioning to distribute
 * log data across 12 reducers based on month, enabling parallel processing
 * and balanced workload distribution.
 * 
 * Features:
 * - Modern Hadoop 3.x API usage
 * - Custom partitioning by month
 * - Proper logging with SLF4J
 * - Command-line argument validation
 * - Job configuration and execution
 * - Error handling and exit codes
 * - Month-based data distribution
 */
public class ProcessLogs implements Tool {

    private static final Logger logger = LoggerFactory.getLogger(ProcessLogs.class);
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
            System.err.println("Usage: ProcessLogs <input dir> <output dir>");
            System.err.println("Example: ProcessLogs input/logs output/loganalysis");
            return -1;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        logger.info("Starting Log Processing with Custom Partitioning job with input: {} and output: {}", inputPath, outputPath);

        try {
            // Create job configuration
            Job job = Job.getInstance(getConf(), "Log Processing with Custom Partitioning");
            
            // Set the jar file that contains the driver, mapper, and reducer
            job.setJarByClass(ProcessLogs.class);
            
            // Set input and output paths
            FileInputFormat.setInputPaths(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            
            // Set mapper and reducer classes
            job.setMapperClass(LogMonthMapper.class);
            job.setReducerClass(CountReducer.class);
            
            // Set mapper output key and value classes
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
            
            // Set final output key and value classes
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            
            /*
             * Set up the partitioner. Specify 12 reducers - one for each
             * month of the year. The partitioner class must have a 
             * getPartition method that returns a number between 0 and 11.
             * This number will be used to assign the intermediate output
             * to one of the reducers.
             */
            job.setNumReduceTasks(12);
            
            /*
             * Specify the partitioner class.
             */
            job.setPartitionerClass(MonthPartitioner.class);
            
            // Enable speculative execution for better performance
            job.setSpeculativeExecution(true);
            
            logger.info("Job configuration completed. Starting job execution with 12 reducers...");
            
            // Submit the job and wait for completion
            boolean success = job.waitForCompletion(true);
            
            if (success) {
                logger.info("Log Processing with Custom Partitioning job completed successfully");
                return 0;
            } else {
                logger.error("Log Processing with Custom Partitioning job failed");
                return 1;
            }
            
        } catch (Exception e) {
            logger.error("Error during Log Processing with Custom Partitioning job execution", e);
            throw e;
        }
    }

    /**
     * Main method to run the Log Processing with Custom Partitioning job.
     * 
     * @param args Command line arguments: [input_dir, output_dir]
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        // Set Hadoop configuration properties
        conf.set("mapreduce.job.reduces", "12");
        conf.set("mapreduce.map.output.compress", "true");
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");
        
        int exitCode = ToolRunner.run(conf, new ProcessLogs(), args);
        System.exit(exitCode);
    }
}
