package solution;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create Uncompressed Sequence File MapReduce job driver class.
 * 
 * This class demonstrates how to create uncompressed Hadoop SequenceFiles
 * from text input. It's a map-only job that uses the default identity mapper
 * to convert text files to SequenceFile format.
 * 
 * Features:
 * - Modern Hadoop 3.x API usage
 * - Proper logging with SLF4J
 * - Command-line argument validation
 * - Job configuration and execution
 * - Error handling and exit codes
 * - SequenceFile output format configuration
 */
public class CreateUncompressedSequenceFile implements Tool {

    private static final Logger logger = LoggerFactory.getLogger(CreateUncompressedSequenceFile.class);
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
            System.err.println("Usage: CreateUncompressedSequenceFile <input dir> <output dir>");
            System.err.println("Example: CreateUncompressedSequenceFile input/text output/sequence");
            return -1;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        logger.info("Starting Create Uncompressed Sequence File job with input: {} and output: {}", inputPath, outputPath);

        try {
            // Create job configuration
            Job job = Job.getInstance(getConf(), "Create Uncompressed Sequence File");
            
            // Set the jar file that contains the driver
            job.setJarByClass(CreateUncompressedSequenceFile.class);
            
            // Set input and output paths
            FileInputFormat.setInputPaths(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            
            /*
             * There is no need to call setInputFormatClass, because the input
             * file is a text file. However, the output file is a SequenceFile.
             * Therefore, we must call setOutputFormatClass.
             */
            job.setOutputFormatClass(SequenceFileOutputFormat.class);
            
            /*
             * This is a map-only job that uses the default (identity mapper), so we do not need to set
             * the mapper or reducer classes. We just need to set the number of reducers to 0.
             */
            job.setNumReduceTasks(0);
            
            // Enable speculative execution for better performance
            job.setSpeculativeExecution(true);
            
            logger.info("Job configuration completed. Starting job execution...");
            
            // Submit the job and wait for completion
            boolean success = job.waitForCompletion(true);
            
            if (success) {
                logger.info("Create Uncompressed Sequence File job completed successfully");
                return 0;
            } else {
                logger.error("Create Uncompressed Sequence File job failed");
                return 1;
            }
            
        } catch (Exception e) {
            logger.error("Error during Create Uncompressed Sequence File job execution", e);
            throw e;
        }
    }

    /**
     * Main method to run the Create Uncompressed Sequence File job.
     * 
     * @param args Command line arguments: [input_dir, output_dir]
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        // Set Hadoop configuration properties
        conf.set("mapreduce.job.reduces", "0");
        conf.set("mapreduce.map.output.compress", "false");
        
        int exitCode = ToolRunner.run(conf, new CreateUncompressedSequenceFile(), args);
        System.exit(exitCode);
    }
}
