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
 * Image Counter MapReduce job driver class.
 * 
 * This class demonstrates the use of Hadoop counters to track different
 * image file types (JPG, GIF, OTHER) during processing. It's a map-only
 * job that counts file types without using reducers.
 * 
 * Features:
 * - Modern Hadoop 3.x API usage
 * - Proper logging with SLF4J
 * - Command-line argument validation
 * - Job configuration and execution
 * - Error handling and exit codes
 * - Counter-based metrics collection
 */
public class ImageCounter implements Tool {

    private static final Logger logger = LoggerFactory.getLogger(ImageCounter.class);
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
            System.err.println("Usage: ImageCounter <input dir> <output dir>");
            System.err.println("Example: ImageCounter input/images output/imagecounts");
            return -1;
        }

        String inputPath = args[0];
        String outputPath = args[1];

        logger.info("Starting Image Counter job with input: {} and output: {}", inputPath, outputPath);

        try {
            // Create job configuration
            Job job = Job.getInstance(getConf(), "Image Counter");
            
            // Set the jar file that contains the driver and mapper
            job.setJarByClass(ImageCounter.class);
            
            // Set input and output paths
            FileInputFormat.setInputPaths(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));
            
            // Set mapper class (this is a map-only job)
            job.setMapperClass(ImageCounterMapper.class);
            
            // Set output key and value classes
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            
            /*
             * Set the number of reduce tasks to 0 for map-only job.
             */
            job.setNumReduceTasks(0);
            
            // Enable speculative execution for better performance
            job.setSpeculativeExecution(true);
            
            logger.info("Job configuration completed. Starting job execution...");
            
            // Submit the job and wait for completion
            boolean success = job.waitForCompletion(true);
            
            if (success) {
                /*
                 * Print out the counters that the mappers have been incrementing.
                 */
                long jpg = job.getCounters().findCounter("ImageCounter", "jpg").getValue();
                long gif = job.getCounters().findCounter("ImageCounter", "gif").getValue();
                long other = job.getCounters().findCounter("ImageCounter", "other").getValue();
                
                logger.info("Image Counter job completed successfully");
                logger.info("Image counts - JPG: {}, GIF: {}, OTHER: {}", jpg, gif, other);
                
                System.out.println("JPG   = " + jpg);
                System.out.println("GIF   = " + gif);
                System.out.println("OTHER = " + other);
                
                return 0;
            } else {
                logger.error("Image Counter job failed");
                return 1;
            }
            
        } catch (Exception e) {
            logger.error("Error during Image Counter job execution", e);
            throw e;
        }
    }

    /**
     * Main method to run the Image Counter job.
     * 
     * @param args Command line arguments: [input_dir, output_dir]
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        // Set Hadoop configuration properties
        conf.set("mapreduce.job.reduces", "0");
        conf.set("mapreduce.map.output.compress", "true");
        conf.set("mapreduce.map.output.compress.codec", "org.apache.hadoop.io.compress.SnappyCodec");
        
        int exitCode = ToolRunner.run(conf, new ImageCounter(), args);
        System.exit(exitCode);
    }
}