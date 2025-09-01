package solution;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapper class for the StringPair MapReduce job.
 * 
 * This mapper processes text input and creates StringPairWritable objects
 * from the first two words in each line. It's designed to demonstrate
 * custom Writable types in MapReduce jobs.
 * 
 * Features:
 * - Efficient word splitting using compiled regex patterns
 * - Proper logging for debugging and monitoring
 * - Input validation and error handling
 * - Performance optimizations
 * - Comprehensive metrics tracking
 * - StringPairWritable integration
 * 
 * @param <LongWritable> The data type of the input key (line number)
 * @param <Text> The data type of the input value (line of text)
 * @param <StringPairWritable> The data type of the output key (word pair)
 * @param <LongWritable> The data type of the output value (count)
 */
public class StringPairMapper extends
        Mapper<LongWritable, Text, StringPairWritable, LongWritable> {

    private static final Logger logger = LoggerFactory.getLogger(StringPairMapper.class);
    
    // Compile regex pattern for better performance
    private static final Pattern WORD_PATTERN = Pattern.compile("\\W+");
    
    // Reusable objects to reduce garbage collection
    private final LongWritable one = new LongWritable(1);
    private final StringPairWritable outputKey = new StringPairWritable();
    
    // Statistics tracking
    private long lineCount = 0;
    private long validPairCount = 0;
    private long errorCount = 0;

    /**
     * Setup method called once before processing begins.
     * 
     * @param context The task context
     */
    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        logger.debug("StringPairMapper initialized");
    }

    /**
     * The map method runs once for each line of text in the input file.
     * 
     * @param key The input key (line number)
     * @param value The input value (line of text)
     * @param context The context object for emitting output
     */
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
             * Split the line into words. Create a new StringPairWritable consisting
             * of the first two strings in the line. Emit the pair as the key, and
             * '1' as the value (for later summing).
             */
            String[] words = WORD_PATTERN.split(line, 3);

            if (words.length > 2) {
                try {
                    // Validate that we have at least two non-empty words
                    if (words[0] != null && !words[0].isEmpty() && 
                        words[1] != null && !words[1].isEmpty()) {
                        
                        // Create the StringPairWritable and emit
                        outputKey.setLeft(words[0]);
                        outputKey.setRight(words[1]);
                        context.write(outputKey, one);
                        
                        validPairCount++;
                        
                        logger.debug("Created pair: ({}, {}) from line: {}", words[0], words[1], line);
                        
                    } else {
                        logger.warn("Empty words found in line {}: words[0]='{}', words[1]='{}'", 
                            key.get(), words[0], words[1]);
                        errorCount++;
                    }
                    
                } catch (Exception e) {
                    logger.error("Error creating StringPairWritable for line {}: {}", key.get(), e.getMessage());
                    errorCount++;
                    context.getCounter("StringPairMapper", "ErrorPairs").increment(1);
                }
                
            } else {
                logger.debug("Line {} has insufficient words ({}), skipping", key.get(), words.length);
            }
            
            // Log progress every 1000 lines
            if (lineCount % 1000 == 0) {
                logger.info("Processed {} lines, {} valid pairs, {} errors", lineCount, validPairCount, errorCount);
            }
            
        } catch (Exception e) {
            logger.error("Error processing line at offset {}: {}", key.get(), e.getMessage(), e);
            errorCount++;
            // Increment error counter
            context.getCounter("StringPairMapper", "ErrorLines").increment(1);
        }
    }

    /**
     * Cleanup method called once after processing is complete.
     * 
     * @param context The task context
     */
    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("StringPairMapper completed. Processed {} lines, {} valid pairs, {} errors", 
            lineCount, validPairCount, errorCount);
        
        // Set counters for monitoring
        context.getCounter("StringPairMapper", "TotalLines").setValue(lineCount);
        context.getCounter("StringPairMapper", "ValidPairs").setValue(validPairCount);
        context.getCounter("StringPairMapper", "ErrorLines").setValue(errorCount);
    }
}
