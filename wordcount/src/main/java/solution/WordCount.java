package solution;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobPriority;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WordCount MapReduce job driver class.
 * 
 * This class demonstrates the basic MapReduce pattern for counting word frequencies
 * in text files. It serves as a template for other MapReduce applications.
 * 
 * Features:
 * - Modern Hadoop 3.x API usage
 * - Proper logging with SLF4J
 * - Command-line argument validation
 * - Job configuration and execution
 * - Error handling and exit codes
 */
public class WordCount implements Tool {

    private static final Logger logger = LoggerFactory.getLogger(WordCount.class);
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
            System.err.println("Usage: WordCount <input dir> <output dir>");
            System.err.println("Example: WordCount input/shakespeare.txt output/wordcount");
            return -1;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        logger.info("Starting WordCount job with input: {} and output: {}", inputPath, outputPath);

        try {
            // Create job configuration
            Job job = Job.getInstance(getConf(), "Word Count");
            
            // Set the jar file that contains the driver, mapper, and reducer
            job.setJarByClass(WordCount.class);
            
            // Set input and output paths
            FileInputFormat.setInputPaths(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            
            // Set mapper and reducer classes
            job.setMapperClass(WordMapper.class);
            job.setReducerClass(SumReducer.class);
            
            // Set output key and value classes
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            
            // Set the number of reduce tasks (optional - defaults to 1)
            job.setNumReduceTasks(2);
            
            // Enable speculative execution for better performance
            job.setSpeculativeExecution(true);
            
            // Set job priority
            job.setPriority(JobPriority.NORMAL);
            
            logger.info("Job configuration completed. Starting job execution...");
            
            // Submit the job and wait for completion
            boolean success = job.waitForCompletion(true);
            
            if (success) {
                logger.info("WordCount job completed successfully");
                return 0;
            } else {
                logger.error("WordCount job failed");
                return 1;
            }
            
        } catch (Exception e) {
            logger.error("Error during WordCount job execution", e);
            throw e;
        }
    }

    /**
     * Main method to run the WordCount job.
     * 
     * @param args Command line arguments: [input_dir, output_dir]
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        // Set Hadoop configuration properties
        conf.set("mapreduce.job.reduces", "2");
        conf.set("mapreduce.map.output.compress", "true");
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");
        
        int exitCode = ToolRunner.run(conf, new WordCount(), args);
        System.exit(exitCode);
    }
}
