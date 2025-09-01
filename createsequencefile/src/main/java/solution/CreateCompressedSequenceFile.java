package solution;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create Compressed Sequence File MapReduce job driver class.
 * 
 * This class demonstrates how to create compressed Hadoop SequenceFiles
 * from text input using Snappy compression and block compression type.
 * It's a map-only job that uses the default identity mapper.
 * 
 * Features:
 * - Modern Hadoop 3.x API usage
 * - Proper logging with SLF4J
 * - Command-line argument validation
 * - Job configuration and execution
 * - Error handling and exit codes
 * - SequenceFile compression configuration
 * - Snappy compression with block compression type
 */
public class CreateCompressedSequenceFile implements Tool {

    private static final Logger logger = LoggerFactory.getLogger(CreateCompressedSequenceFile.class);
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
            System.err.println("Usage: CreateCompressedSequenceFile <input dir> <output dir>");
            System.err.println("Example: CreateCompressedSequenceFile input/text output/compressed-sequence");
            return -1;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        logger.info("Starting Create Compressed Sequence File job with input: {} and output: {}", inputPath, outputPath);

        try {
            // Create job configuration
            Job job = Job.getInstance(getConf(), "Create Compressed Sequence File");
            
            // Set the jar file that contains the driver
            job.setJarByClass(CreateCompressedSequenceFile.class);
            
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
             * Set the compression options.
             */
            
            /*
             * Compress the output
             */
            FileOutputFormat.setCompressOutput(job, true);
            
            /*
             * Use Snappy compression
             */
            FileOutputFormat.setOutputCompressorClass(job, SnappyCodec.class);
            
            /*
             * Use block compression
             */
            SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.BLOCK);
            
            /*
             * This is a map-only job that uses the default (identity mapper), so we do not need to set
             * the mapper or reducer classes. We just need to set the number of reducers to 0.
             */
            job.setNumReduceTasks(0);
            
            // Enable speculative execution for better performance
            job.setSpeculativeExecution(true);
            
            logger.info("Job configuration completed with Snappy compression and block compression type");
            logger.info("Starting job execution...");
            
            // Submit the job and wait for completion
            boolean success = job.waitForCompletion(true);
            
            if (success) {
                logger.info("Create Compressed Sequence File job completed successfully");
                return 0;
            } else {
                logger.error("Create Compressed Sequence File job failed");
                return 1;
            }
            
        } catch (Exception e) {
            logger.error("Error during Create Compressed Sequence File job execution", e);
            throw e;
        }
    }

    /**
     * Main method to run the Create Compressed Sequence File job.
     * 
     * @param args Command line arguments: [input_dir, output_dir]
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        // Set Hadoop configuration properties
        conf.set("mapreduce.job.reduces", "0");
        conf.set("mapreduce.map.output.compress", "true");
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");
        
        int exitCode = ToolRunner.run(conf, new CreateCompressedSequenceFile(), args);
        System.exit(exitCode);
    }
}
