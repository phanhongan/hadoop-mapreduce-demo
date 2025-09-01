package solution;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sum Reducer class for the Word Count MapReduce job with Combiner optimization.
 * 
 * This reducer aggregates word counts from both the combiner and mapper outputs.
 * It's designed to work efficiently with the combiner optimization to provide
 * accurate final word counts while maintaining performance.
 * 
 * Features:
 * - Efficient word count aggregation
 * - Proper logging for debugging and monitoring
 * - Input validation and error handling
 * - Performance optimizations
 * - Comprehensive metrics tracking
 * - Combiner integration support
 * 
 * @param <Text> The data type of the input key (word)
 * @param <IntWritable> The data type of the input value (count)
 * @param <Text> The data type of the output key (word)
 * @param <IntWritable> The data type of the output value (total count)
 */
public class SumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    private static final Logger logger = LoggerFactory.getLogger(SumReducer.class);
    
    // Reusable object to reduce garbage collection
    private final IntWritable result = new IntWritable();
    
    // Statistics tracking
    private long totalWords = 0;
    private long totalCount = 0;
    private long errorCount = 0;

    /**
     * Setup method called once before processing begins.
     * 
     * @param context The task context
     */
    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        logger.debug("SumReducer initialized for combiner optimization");
    }

    /**
     * The reduce method aggregates word counts from the input values.
     * 
     * @param key The key (word)
     * @param values An iterable collection of counts
     * @param context The context object for emitting output
     */
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException,
            InterruptedException {
        
        try {
            int wordCount = 0;
            int valueCount = 0;
            
            /*
             * Aggregate all counts for this word. The values may come from:
             * 1. Direct mapper output (count = 1)
             * 2. Combiner output (count = aggregated local count)
             * This ensures accurate final word counts while maintaining performance.
             */
            for (IntWritable value : values) {
                try {
                    int count = value.get();
                    
                    // Validate count value
                    if (count > 0) {
                        wordCount += count;
                        valueCount++;
                    } else {
                        logger.warn("Invalid count value encountered: {} for word: {}", count, key);
                        errorCount++;
                    }
                    
                } catch (Exception e) {
                    logger.error("Error processing value for word {}: {}", key, e.getMessage());
                    errorCount++;
                }
            }
            
            if (wordCount > 0) {
                // Emit the aggregated word count
                result.set(wordCount);
                context.write(key, result);
                
                totalWords++;
                totalCount += wordCount;
                
                logger.debug("Processed word: {} with {} values, total count: {}", 
                    key, valueCount, wordCount);
                    
            } else {
                logger.warn("No valid counts found for word: {}", key);
                errorCount++;
            }
            
        } catch (Exception e) {
            logger.error("Error in reduce method for word {}: {}", key, e.getMessage(), e);
            errorCount++;
            // Increment error counter
            context.getCounter("SumReducer", "ErrorWords").increment(1);
        }
    }

    /**
     * Cleanup method called once after processing is complete.
     * 
     * @param context The task context
     */
    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("SumReducer completed. Processed {} words with total count {}, {} errors", 
            totalWords, totalCount, errorCount);
        
        // Set counters for monitoring
        context.getCounter("SumReducer", "TotalWords").setValue(totalWords);
        context.getCounter("SumReducer", "TotalCount").setValue(totalCount);
        context.getCounter("SumReducer", "ErrorWords").setValue(errorCount);
    }
}