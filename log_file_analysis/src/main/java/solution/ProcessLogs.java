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
 * Log File Analysis MapReduce job driver class.
 * 
 * This class demonstrates MapReduce for analyzing log files to extract
 * meaningful insights such as error rates, access patterns, and performance metrics.
 * 
 * Features:
 * - Modern Hadoop 3.x API usage
 * - Proper logging with SLF4J
 * - Command-line argument validation
 * - Job configuration and execution
 * - Error handling and exit codes
 * - Log analysis capabilities
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

        logger.info("Starting Log File Analysis job with input: {} and output: {}", inputPath, outputPath);

        try {
            // Create job configuration
            Job job = Job.getInstance(getConf(), "Log File Analysis");
            
            // Set the jar file that contains the driver, mapper, and reducer
            job.setJarByClass(ProcessLogs.class);
            
            // Set input and output paths
            FileInputFormat.setInputPaths(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            
            // Set mapper and reducer classes
            job.setMapperClass(LogFileMapper.class);
            job.setReducerClass(SumReducer.class);
            
            // Set output key and value classes
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            
            // Set the number of reduce tasks for better parallelism
            job.setNumReduceTasks(3);
            
            // Enable speculative execution for better performance
            job.setSpeculativeExecution(true);
            
            // Set job priority
            job.setPriority(org.apache.hadoop.mapreduce.JobPriority.NORMAL);
            
            logger.info("Job configuration completed. Starting job execution...");
            
            // Submit the job and wait for completion
            boolean success = job.waitForCompletion(true);
            
            if (success) {
                logger.info("Log File Analysis job completed successfully");
                return 0;
            } else {
                logger.error("Log File Analysis job failed");
                return 1;
            }
            
        } catch (Exception e) {
            logger.error("Error during Log File Analysis job execution", e);
            throw e;
        }
    }

    /**
     * Main method to run the Log File Analysis job.
     * 
     * @param args Command line arguments: [input_dir, output_dir]
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        // Set Hadoop configuration properties
        conf.set("mapreduce.job.reduces", "3");
        conf.set("mapreduce.map.output.compress", "true");
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");
        
        int exitCode = ToolRunner.run(conf, new ProcessLogs(), args);
        System.exit(exitCode);
    }
}
