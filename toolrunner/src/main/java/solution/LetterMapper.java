package solution;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapper class for the Average Word Length MapReduce job.
 * 
 * This mapper processes text input and extracts the first letter of each word
 * along with the word's length. It supports configurable case sensitivity
 * and provides comprehensive logging and error handling.
 * 
 * Features:
 * - Configurable case sensitivity via job configuration
 * - Efficient word splitting using compiled regex patterns
 * - Proper logging for debugging and monitoring
 * - Input validation and error handling
 * - Performance optimizations
 * - Comprehensive metrics tracking
 * 
 * @param <LongWritable> The data type of the input key
 * @param <Text> The data type of the input value
 * @param <Text> The data type of the output key (first letter)
 * @param <IntWritable> The data type of the output value (word length)
 */
public class LetterMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final Logger logger = LoggerFactory.getLogger(LetterMapper.class);
    
    // Compile regex pattern for better performance
    private static final Pattern WORD_PATTERN = Pattern.compile("\\W+");
    
    // Configuration for case sensitivity
    private boolean caseSensitive = false;
    
    // Reusable objects to reduce garbage collection
    private final Text outputKey = new Text();
    private final IntWritable outputValue = new IntWritable();
    
    // Statistics tracking
    private long lineCount = 0;
    private long wordCount = 0;
    private long errorCount = 0;

    /**
     * Setup method called once before processing begins.
     * 
     * @param context The task context
     */
    @Override
    public void setup(Context context) {
        try {
            Configuration conf = context.getConfiguration();
            caseSensitive = conf.getBoolean("caseSensitive", false);
            
            logger.debug("LetterMapper initialized with caseSensitive: {}", caseSensitive);
            
        } catch (Exception e) {
            logger.error("Error during LetterMapper setup", e);
            // Use default case sensitivity setting
            caseSensitive = false;
        }
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
            // Convert the line, which is received as a Text object, to a String object
            String line = value.toString().trim();
            
            // Skip empty lines
            if (line.isEmpty()) {
                return;
            }
            
            lineCount++;
            
            /*
             * The line.split("\\W+") call uses regular expressions to split the
             * line up by non-word characters. We use a compiled pattern for better performance.
             */
            String[] words = WORD_PATTERN.split(line);
            
            for (String word : words) {
                if (word.length() > 0) {
                    try {
                        /*
                         * Obtain the first letter of the word
                         */
                        String letter;
                        if (caseSensitive) {
                            letter = word.substring(0, 1);
                        } else {
                            letter = word.substring(0, 1).toLowerCase();
                        }

                        /*
                         * Call the write method on the Context object to emit a key and
                         * a value from the map method. The key is the letter (in
                         * lower-case) that the word starts with; the value is the
                         * word's length.
                         */
                        outputKey.set(letter);
                        outputValue.set(word.length());
                        context.write(outputKey, outputValue);
                        
                        wordCount++;
                        
                    } catch (StringIndexOutOfBoundsException e) {
                        logger.warn("Empty word encountered at line {}, skipping", key.get());
                        errorCount++;
                    }
                }
            }
            
            // Log progress every 1000 lines
            if (lineCount % 1000 == 0) {
                logger.info("Processed {} lines, {} words, {} errors", lineCount, wordCount, errorCount);
            }
            
        } catch (Exception e) {
            logger.error("Error processing line at offset {}: {}", key.get(), e.getMessage(), e);
            errorCount++;
            // Increment error counter
            context.getCounter("LetterMapper", "ErrorLines").increment(1);
        }
    }

    /**
     * Cleanup method called once after processing is complete.
     * 
     * @param context The task context
     */
    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("LetterMapper completed. Processed {} lines, {} words, {} errors", 
            lineCount, wordCount, errorCount);
        
        // Set counters for monitoring
        context.getCounter("LetterMapper", "TotalLines").setValue(lineCount);
        context.getCounter("LetterMapper", "TotalWords").setValue(wordCount);
        context.getCounter("LetterMapper", "ErrorLines").setValue(errorCount);
    }
}
