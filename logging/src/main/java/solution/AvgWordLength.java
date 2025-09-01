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
 * Average Word Length MapReduce job driver class with comprehensive logging.
 * 
 * This class demonstrates the use of comprehensive logging throughout the
 * MapReduce job lifecycle. It calculates the average length of words in
 * text files and provides detailed logging for monitoring and debugging.
 * 
 * Features:
 * - Modern Hadoop 3.x API usage
 * - Comprehensive logging with SLF4J
 * - Command-line argument validation
 * - Job configuration and execution
 * - Error handling and exit codes
 * - Performance optimization
 * - Detailed logging for monitoring
 * - Progress tracking and metrics
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
        /*
         * Validate that two arguments were passed from the command line.
         */
        if (args.length != 2) {
            logger.error("Invalid number of arguments. Expected 2, got {}.", args.length);
            System.err.println("Usage: AvgWordLength <input dir> <output dir>");
            System.err.println("Example: AvgWordLength input/text output/avgwordlength");
            return -1;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        logger.info("Starting Average Word Length job with input: {} and output: {}", inputPath, outputPath);

        try {
            /*
             * Instantiate a Job object for your job's configuration.
             */
            Job job = Job.getInstance(getConf(), "Average Word Length");

            /*
             * Specify the jar file that contains your driver, mapper, and reducer.
             * Hadoop will transfer this jar file to nodes in your cluster running
             * mapper and reducer tasks.
             */
            job.setJarByClass(AvgWordLength.class);

            /*
             * Specify the paths to the input and output data based on the
             * command-line arguments.
             */
            FileInputFormat.setInputPaths(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));

            /*
             * Specify the mapper and reducer classes.
             */
            job.setMapperClass(LetterMapper.class);
            job.setReducerClass(AverageReducer.class);

            /*
             * The input file and output files are text files, so there is no need
             * to call the setInputFormatClass and setOutputFormatClass methods.
             */

            /*
             * The mapper's output keys and values have different data types than
             * the reducer's output keys and values. Therefore, you must call the
             * setMapOutputKeyClass and setMapOutputValueClass methods.
             */
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(IntWritable.class);

            /*
             * Specify the job's output key and value classes.
             */
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(DoubleWritable.class);
            
            // Set number of reduce tasks for better performance
            job.setNumReduceTasks(2);
            
            // Enable speculative execution for better performance
            job.setSpeculativeExecution(true);
            
            logger.info("Job configuration completed. Starting job execution...");
            logger.debug("Job details - Mapper: {}, Reducer: {}, Reduces: {}", 
                job.getMapperClass().getSimpleName(), 
                job.getReducerClass().getSimpleName(),
                job.getNumReduceTasks());

            /*
             * Start the MapReduce job and wait for it to finish. If it finishes
             * successfully, return 0. If not, return 1.
             */
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
     * Main method to run the Average Word Length job using ToolRunner.
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
