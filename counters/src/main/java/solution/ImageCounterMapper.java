package solution;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapper class for the Image Counter MapReduce job.
 * 
 * This mapper processes log entries and counts different image file types
 * (JPG, GIF, OTHER) based on file extensions in HTTP requests.
 * 
 * Example input line:
 * 96.7.4.14 - - [24/Apr/2011:04:20:11 -0400] "GET /cat.jpg HTTP/1.1" 200 12433
 * 
 * Features:
 * - Efficient string processing with regex patterns
 * - Proper logging for debugging and monitoring
 * - Input validation and error handling
 * - Performance optimizations
 * - Comprehensive metrics tracking
 */
public class ImageCounterMapper extends
    Mapper<LongWritable, Text, Text, IntWritable> {

    private static final Logger logger = LoggerFactory.getLogger(ImageCounterMapper.class);
    
    // Compile regex patterns for better performance
    private static final Pattern QUOTE_PATTERN = Pattern.compile("\"");
    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");
    
    // Counters for tracking processed data
    private long lineCount = 0;
    private long validRequestCount = 0;
    private long imageCount = 0;

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        logger.debug("Initializing ImageCounterMapper");
    }

    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        try {
            // Convert the line to a string and trim whitespace
            String line = value.toString().trim();
            
            // Skip empty lines
            if (line.isEmpty()) {
                return;
            }
            
            lineCount++;
            
            /*
             * Split the line using the double-quote character as the delimiter.
             */
            String[] fields = QUOTE_PATTERN.split(line);
            
            if (fields.length > 1) {
                String request = fields[1];
                
                /*
                 * Split the part of the line after the first double quote
                 * using the space character as the delimiter to get a file name.
                 */
                String[] requestFields = SPACE_PATTERN.split(request);
                
                /*
                 * Increment a counter based on the file's extension.
                 */
                if (requestFields.length > 1) {
                    String fileName = requestFields[1].toLowerCase();
                    
                    if (fileName.endsWith(".jpg")) {
                        context.getCounter("ImageCounter", "jpg").increment(1);
                        imageCount++;
                        logger.debug("Found JPG file: {}", fileName);
                    } else if (fileName.endsWith(".gif")) {
                        context.getCounter("ImageCounter", "gif").increment(1);
                        imageCount++;
                        logger.debug("Found GIF file: {}", fileName);
                    } else {
                        context.getCounter("ImageCounter", "other").increment(1);
                        imageCount++;
                        logger.debug("Found other file: {}", fileName);
                    }
                    
                    validRequestCount++;
                }
            }
            
            // Log progress every 1000 lines
            if (lineCount % 1000 == 0) {
                logger.info("Processed {} lines, {} valid requests, {} images", 
                    lineCount, validRequestCount, imageCount);
            }
            
        } catch (Exception e) {
            logger.error("Error processing line at offset {}: {}", key.get(), e.getMessage(), e);
            // Increment error counter
            context.getCounter("ImageCounter", "ErrorLines").increment(1);
        }
    }

    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("ImageCounterMapper completed. Processed {} lines, {} valid requests, {} images", 
            lineCount, validRequestCount, imageCount);
        
        // Set counters for monitoring
        context.getCounter("ImageCounter", "TotalLines").setValue(lineCount);
        context.getCounter("ImageCounter", "ValidRequests").setValue(validRequestCount);
        context.getCounter("ImageCounter", "TotalImages").setValue(imageCount);
    }
}