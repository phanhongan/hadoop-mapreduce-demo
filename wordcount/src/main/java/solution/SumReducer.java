package solution;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reducer class for the WordCount MapReduce job.
 * 
 * This reducer aggregates word counts from the mapper output by summing
 * all the counts for each unique word. It receives key-value pairs where
 * the key is a word and the values are counts (typically 1s from the mapper).
 * 
 * Features:
 * - Efficient aggregation of word counts
 * - Proper logging for debugging and monitoring
 * - Error handling and counter tracking
 * - Performance optimizations
 */
public class SumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    private static final Logger logger = LoggerFactory.getLogger(SumReducer.class);
    
    // Reuse objects to reduce garbage collection overhead
    private final IntWritable result = new IntWritable();
    
    // Counters for monitoring
    private long totalWords = 0;
    private long totalCount = 0;

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        logger.debug("Initializing SumReducer");
    }

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {

        try {
            int wordCount = 0;
            
            // Sum all the counts for this word
            for (IntWritable value : values) {
                wordCount += value.get();
            }
            
            // Set the result and write it
            result.set(wordCount);
            context.write(key, result);
            
            // Update counters
            totalWords++;
            totalCount += wordCount;
            
            // Log progress every 1000 words
            if (totalWords % 1000 == 0) {
                logger.info("Processed {} words, total count: {}", totalWords, totalCount);
            }
            
        } catch (Exception e) {
            logger.error("Error processing word '{}': {}", key.toString(), e.getMessage(), e);
            // Increment error counter
            context.getCounter("SumReducer", "ErrorWords").increment(1);
        }
    }

    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("SumReducer completed. Processed {} words, total count: {}", totalWords, totalCount);
        
        // Set counters for monitoring
        context.getCounter("SumReducer", "TotalWords").setValue(totalWords);
        context.getCounter("SumReducer", "TotalCount").setValue(totalCount);
    }
}