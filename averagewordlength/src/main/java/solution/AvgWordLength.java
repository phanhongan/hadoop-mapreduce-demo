package solution;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
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
 * Average Word Length MapReduce job driver class.
 * 
 * This class demonstrates MapReduce with custom Writable types for calculating
 * the average length of words in text files. It shows how to handle different
 * input/output types between mapper and reducer.
 * 
 * Features:
 * - Modern Hadoop 3.x API usage
 * - Proper logging with SLF4J
 * - Command-line argument validation
 * - Job configuration and execution
 * - Error handling and exit codes
 */
public class AvgWordLength implements Tool {

    private static final Logger logger = LoggerFactory.getLogger(AvgWordLength.class);
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
            System.err.println("Usage: AvgWordLength <input dir> <output dir>");
            System.err.println("Example: AvgWordLength input/shakespeare.txt output/avgwordlength");
            return -1;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        logger.info("Starting Average Word Length job with input: {} and output: {}", inputPath, outputPath);

        try {
            // Create job configuration
            Job job = Job.getInstance(getConf(), "Average Word Length");
            
            // Set the jar file that contains the driver, mapper, and reducer
            job.setJarByClass(AvgWordLength.class);
            
            // Set input and output paths
            FileInputFormat.setInputPaths(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            
            // Set mapper and reducer classes
            job.setMapperClass(LetterMapper.class);
            job.setReducerClass(AverageReducer.class);
            
            // Set mapper output key and value classes (different from final output)
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(IntWritable.class);
            
            // Set final output key and value classes
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(DoubleWritable.class);
            
            // Set the number of reduce tasks for better parallelism
            job.setNumReduceTasks(2);
            
            // Enable speculative execution for better performance
            job.setSpeculativeExecution(true);
            
            logger.info("Job configuration completed. Starting job execution...");
            
            // Submit the job and wait for completion
            boolean success = job.waitForCompletion(true);
            
            if (success) {
                logger.info("Average Word Length job completed successfully");
                return 0;
            } else {
                logger.error("Average Word Length job failed");
                return 1;
            }
            
        } catch (Exception e) {
            logger.error("Error during Average Word Length job execution", e);
            throw e;
        }
    }

    /**
     * Main method to run the Average Word Length job.
     * 
     * @param args Command line arguments: [input_dir, output_dir]
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        // Set Hadoop configuration properties
        conf.set("mapreduce.job.reduces", "2");
        conf.set("mapreduce.map.output.compress", "true");
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");
        
        int exitCode = ToolRunner.run(conf, new AvgWordLength(), args);
        System.exit(exitCode);
    }
}
