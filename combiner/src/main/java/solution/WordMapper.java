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
 * Word Mapper class for the Word Count MapReduce job with Combiner optimization.
 * 
 * This mapper processes text input and emits word-count pairs. It's designed
 * to work efficiently with the combiner optimization to reduce network traffic
 * and improve overall job performance.
 * 
 * Features:
 * - Efficient word splitting using compiled regex patterns
 * - Proper logging for debugging and monitoring
 * - Input validation and error handling
 * - Performance optimizations
 * - Comprehensive metrics tracking
 * - Combiner-friendly output format
 * 
 * @param <LongWritable> The data type of the input key (line number)
 * @param <Text> The data type of the input value (line of text)
 * @param <Text> The data type of the output key (word)
 * @param <IntWritable> The data type of the output value (count)
 */
public class WordMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final Logger logger = LoggerFactory.getLogger(WordMapper.class);
    
    // Compile regex pattern for better performance
    private static final Pattern WORD_PATTERN = Pattern.compile("\\W+");
    
    // Reusable objects to reduce garbage collection
    private final Text outputKey = new Text();
    private final IntWritable outputValue = new IntWritable(1);
    
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
    public void setup(Context context) throws IOException, InterruptedException {
        logger.debug("WordMapper initialized for combiner optimization");
    }

    /**
     * The map method runs once for each line of text in the input file.
     * 
     * @param key The input key (line number)
     * @param value The input value (line of text)
     * @param context The context object for emitting output
     */
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        try {
            // Convert the line to a string and trim whitespace
            String line = value.toString().trim();
            
            // Skip empty lines
            if (line.isEmpty()) {
                return;
            }
            
            lineCount++;
            
            /*
             * Split the line into words using compiled regex pattern for better performance.
             * The combiner will aggregate these word-count pairs locally before sending to reducers.
             */
            String[] words = WORD_PATTERN.split(line);
            
            for (String word : words) {
                if (word.length() > 0) {
                    try {
                        // Normalize word to lowercase for consistent counting
                        String normalizedWord = word.toLowerCase().trim();
                        
                        if (normalizedWord.length() > 0) {
                            // Emit word-count pair for combiner optimization
                            outputKey.set(normalizedWord);
                            context.write(outputKey, outputValue);
                            
                            wordCount++;
                        }
                        
                    } catch (Exception e) {
                        logger.warn("Error processing word '{}' in line {}: {}", word, key.get(), e.getMessage());
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
            context.getCounter("WordMapper", "ErrorLines").increment(1);
        }
    }

    /**
     * Cleanup method called once after processing is complete.
     * 
     * @param context The task context
     */
    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("WordMapper completed. Processed {} lines, {} words, {} errors", 
            lineCount, wordCount, errorCount);
        
        // Set counters for monitoring
        context.getCounter("WordMapper", "TotalLines").setValue(lineCount);
        context.getCounter("WordMapper", "TotalWords").setValue(wordCount);
        context.getCounter("WordMapper", "ErrorLines").setValue(errorCount);
    }
}