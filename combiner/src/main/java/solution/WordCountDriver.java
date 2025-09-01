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
 * Word Count Driver MapReduce job driver class with Combiner optimization.
 * 
 * This class demonstrates the use of a combiner to optimize MapReduce jobs
 * by performing local aggregation before data is sent to reducers. The combiner
 * reduces network traffic and improves overall job performance.
 * 
 * Features:
 * - Modern Hadoop 3.x API usage
 * - Combiner optimization for performance
 * - Proper logging with SLF4J
 * - Command-line argument validation
 * - Job configuration and execution
 * - Error handling and exit codes
 * - Performance optimization
 * - Combiner validation
 * 
 * @author Hadoop MapReduce Demo Team
 * @version 2.0.0
 */
public class WordCountDriver implements Tool {

    private static final Logger logger = LoggerFactory.getLogger(WordCountDriver.class);
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
            System.err.println("Usage: WordCountDriver <input dir> <output dir>");
            System.err.println("Example: WordCountDriver input/text output/wordcount");
            return -1;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        logger.info("Starting Word Count Driver job with input: {} and output: {}", inputPath, outputPath);

        try {
            // Create job configuration
            Job job = Job.getInstance(getConf(), "Word Count Driver");
            
            // Set the jar file that contains the driver
            job.setJarByClass(WordCountDriver.class);
            
            // Set input and output paths
            FileInputFormat.setInputPaths(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            
            // Set mapper and reducer classes
            job.setMapperClass(WordMapper.class);
            job.setReducerClass(SumReducer.class);

            /*
             * Specify SumCombiner as the combiner class.
             * The combiner runs on the mapper output to perform local aggregation,
             * reducing the amount of data sent to reducers and improving performance.
             */
            job.setCombinerClass(SumReducer.class);

            // Set output key and value classes
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            
            // Set number of reduce tasks for better performance
            job.setNumReduceTasks(2);
            
            // Enable speculative execution for better performance
            job.setSpeculativeExecution(true);
            
            // Validate that combiner is set
            if (job.getCombinerClass() == null) {
                throw new Exception("Combiner not set - this is required for this job");
            }
            
            logger.info("Job configuration completed with combiner optimization");
            logger.info("Combiner class: {}", job.getCombinerClass().getSimpleName());
            logger.info("Starting job execution...");
            
            // Submit the job and wait for completion
            boolean success = job.waitForCompletion(true);
            
            if (success) {
                logger.info("Word Count Driver job completed successfully with combiner optimization");
                return 0;
            } else {
                logger.error("Word Count Driver job failed");
                return 1;
            }
            
        } catch (Exception e) {
            logger.error("Error during Word Count Driver job execution", e);
            throw e;
        }
    }

    /**
     * Main method to run the Word Count Driver job using ToolRunner.
     * 
     * The main method calls the ToolRunner.run method, which calls an options
     * parser that interprets Hadoop command-line options and puts them into a
     * Configuration object.
     * 
     * @param args Command line arguments: [input_dir, output_dir]
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        // Set Hadoop configuration properties
        conf.set("mapreduce.job.reduces", "2");
        conf.set("mapreduce.map.output.compress", "true");
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");
        
        // Enable combiner optimization
        conf.set("mapreduce.job.combine.enable", "true");
        
        int exitCode = ToolRunner.run(conf, new WordCountDriver(), args);
        System.exit(exitCode);
    }
}
