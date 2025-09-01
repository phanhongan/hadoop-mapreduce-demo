package solution;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Average Reducer class for the Average Word Length MapReduce job with comprehensive logging.
 * 
 * This reducer calculates the average length of words that start with
 * a specific letter. It aggregates word lengths from the mapper output
 * and computes the arithmetic mean with detailed logging throughout.
 * 
 * Features:
 * - Efficient average calculation
 * - Comprehensive logging with SLF4J
 * - Input validation and error handling
 * - Performance optimizations
 * - Progress tracking and metrics
 * - Detailed logging for monitoring
 * - Division by zero protection
 * - Statistical validation
 * 
 * @param <Text> The data type of the input key (first letter)
 * @param <IntWritable> The data type of the input value (word length)
 * @param <Text> The data type of the output key (first letter)
 * @param <DoubleWritable> The data type of the output value (average length)
 */
public class AverageReducer extends
    Reducer<Text, IntWritable, Text, DoubleWritable> {

    private static final Logger logger = LoggerFactory.getLogger(AverageReducer.class);
    
    // Reusable object to reduce garbage collection
    private final DoubleWritable result = new DoubleWritable();
    
    // Statistics tracking
    private long totalWords = 0;
    private long totalLength = 0;
    private long errorCount = 0;

    /**
     * Setup method called once before processing begins.
     * 
     * @param context The task context
     */
    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        logger.debug("AverageReducer initialized with comprehensive logging");
    }

    /**
     * The reduce method runs once for each key received from
     * the shuffle and sort phase of the MapReduce framework.
     * 
     * @param key The key (first letter of words)
     * @param values An iterable collection of word lengths
     * @param context The context object for emitting output
     */
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {

        try {
            long sum = 0;
            long count = 0;

            /*
             * For each value in the set of values passed to us by the mapper:
             */
            for (IntWritable value : values) {
                try {
                    /*
                     * Add up the values and increment the count
                     */
                    int wordLength = value.get();
                    
                    // Validate word length
                    if (wordLength > 0) {
                        sum += wordLength;
                        count++;
                        
                        logger.debug("Processing word length: {} for letter: {}", wordLength, key);
                    } else {
                        logger.warn("Invalid word length encountered: {} for letter: {}", wordLength, key);
                        errorCount++;
                    }
                    
                } catch (Exception e) {
                    logger.error("Error processing value for letter {}: {}", key, e.getMessage());
                    errorCount++;
                }
            }
            
            if (count > 0) {
                /*
                 * The average length is the sum of the values divided by the count.
                 */
                double average = (double) sum / (double) count;
                
                // Validate the result
                if (Double.isFinite(average) && average > 0) {
                    /*
                     * Call the write method on the Context object to emit a key
                     * (the words' starting letter) and a value (the average length 
                     * per word starting with this letter) from the reduce method. 
                     */
                    result.set(average);
                    context.write(key, result);
                    
                    totalWords += count;
                    totalLength += sum;
                    
                    logger.info("Letter: {} - {} words, total length: {}, average: {:.2f}", 
                        key, count, sum, average);
                        
                } else {
                    logger.error("Invalid average calculated for letter {}: sum={}, count={}, average={}", 
                        key, sum, count, average);
                    errorCount++;
                }
                
            } else {
                logger.warn("No valid values found for letter: {}", key);
                errorCount++;
            }
            
        } catch (Exception e) {
            logger.error("Error in reduce method for letter {}: {}", key, e.getMessage(), e);
            errorCount++;
            // Increment error counter
            context.getCounter("AverageReducer", "ErrorLetters").increment(1);
        }
    }

    /**
     * Cleanup method called once after processing is complete.
     * 
     * @param context The task context
     */
    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("AverageReducer completed. Processed {} words with total length {}, {} errors", 
            totalWords, totalLength, errorCount);
        
        // Set counters for monitoring
        context.getCounter("AverageReducer", "TotalWords").setValue(totalWords);
        context.getCounter("AverageReducer", "TotalLength").setValue(totalLength);
        context.getCounter("AverageReducer", "ErrorLetters").setValue(errorCount);
    }
}