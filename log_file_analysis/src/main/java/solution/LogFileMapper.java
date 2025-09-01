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
 * Mapper class for the Log File Analysis MapReduce job.
 * 
 * This mapper processes log file entries and extracts key information such as
 * IP addresses, HTTP status codes, and request patterns for analysis.
 * 
 * Example input line:
 * 96.7.4.14 - - [24/Apr/2011:04:20:11 -0400] "GET /cat.jpg HTTP/1.1" 200 12433
 * 
 * Features:
 * - Robust log parsing with regex patterns
 * - Proper logging for debugging and monitoring
 * - Input validation and error handling
 * - Performance optimizations
 * - Comprehensive metrics tracking
 */
public class LogFileMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final Logger logger = LoggerFactory.getLogger(LogFileMapper.class);
    
    // Compile regex patterns for better performance
    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern IP_PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
    
    // Reuse objects to reduce garbage collection overhead
    private final Text ipAddress = new Text();
    private final IntWritable one = new IntWritable(1);
    
    // Counters for tracking processed data
    private long lineCount = 0;
    private long validLogCount = 0;
    private long invalidLogCount = 0;

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        logger.debug("Initializing LogFileMapper");
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
            
            // Split the input line into space-delimited fields
            String[] fields = SPACE_PATTERN.split(line);
            
            if (fields.length >= 7) { // Minimum fields for a valid log entry
                String ip = fields[0];
                
                // Validate IP address format
                if (isValidIPAddress(ip)) {
                    ipAddress.set(ip);
                    context.write(ipAddress, one);
                    validLogCount++;
                    
                    // Log progress every 1000 valid logs
                    if (validLogCount % 1000 == 0) {
                        logger.info("Processed {} valid log entries", validLogCount);
                    }
                } else {
                    invalidLogCount++;
                    logger.debug("Invalid IP address format: {}", ip);
                }
            } else {
                invalidLogCount++;
                logger.debug("Invalid log format (insufficient fields): {}", line);
            }
            
        } catch (Exception e) {
            logger.error("Error processing log line at offset {}: {}", key.get(), e.getMessage(), e);
            // Increment error counter
            context.getCounter("LogFileMapper", "ErrorLines").increment(1);
        }
    }

    /**
     * Validates if the given string is a valid IP address format.
     * 
     * @param ip The IP address string to validate
     * @return true if valid IP format, false otherwise
     */
    private boolean isValidIPAddress(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        // Check if it matches IP pattern
        if (!IP_PATTERN.matcher(ip).matches()) {
            return false;
        }
        
        // Validate each octet
        String[] octets = ip.split("\\.");
        for (String octet : octets) {
            try {
                int value = Integer.parseInt(octet);
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("LogFileMapper completed. Processed {} lines, {} valid logs, {} invalid logs", 
            lineCount, validLogCount, invalidLogCount);
        
        // Set counters for monitoring
        context.getCounter("LogFileMapper", "TotalLines").setValue(lineCount);
        context.getCounter("LogFileMapper", "ValidLogs").setValue(validLogCount);
        context.getCounter("LogFileMapper", "InvalidLogs").setValue(invalidLogCount);
    }
}