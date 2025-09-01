package solution;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reducer class for the Average Word Length MapReduce job.
 * 
 * This reducer calculates the average word length for each starting letter
 * by aggregating the word lengths from the mapper output and computing
 * the arithmetic mean.
 * 
 * Features:
 * - Efficient average calculation
 * - Proper logging for debugging and monitoring
 * - Error handling and validation
 * - Performance optimizations
 * - Comprehensive metrics tracking
 */
public class AverageReducer extends
    Reducer<Text, IntWritable, Text, DoubleWritable> {

    private static final Logger logger = LoggerFactory.getLogger(AverageReducer.class);
    
    // Reuse objects to reduce garbage collection overhead
    private final DoubleWritable result = new DoubleWritable();
    
    // Counters for monitoring
    private long totalLetters = 0;
    private long totalWords = 0;
    private long totalLength = 0;

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        logger.debug("Initializing AverageReducer");
    }

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {

        try {
            long sum = 0, count = 0;
            
            // Calculate sum and count for this letter
            for (IntWritable value : values) {
                sum += value.get();
                count++;
            }
            
            // Only emit result if we have valid data
            if (count > 0) {
                // Calculate the average length
                double average = (double) sum / (double) count;
                
                // Set the result and write it
                result.set(average);
                context.write(key, result);
                
                // Update counters
                totalLetters++;
                totalWords += count;
                totalLength += sum;
                
                logger.debug("Letter '{}': {} words, total length: {}, average: {:.2f}", 
                    key.toString(), count, sum, average);
            }
            
        } catch (Exception e) {
            logger.error("Error processing letter '{}': {}", key.toString(), e.getMessage(), e);
            // Increment error counter
            context.getCounter("AverageReducer", "ErrorLetters").increment(1);
        }
    }

    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("AverageReducer completed. Processed {} letters, {} words, total length: {}", 
            totalLetters, totalWords, totalLength);
        
        // Set counters for monitoring
        context.getCounter("AverageReducer", "TotalLetters").setValue(totalLetters);
        context.getCounter("AverageReducer", "TotalWords").setValue(totalWords);
        context.getCounter("AverageReducer", "TotalLength").setValue(totalLength);
        
        // Calculate overall average if we have data
        if (totalWords > 0) {
            double overallAverage = (double) totalLength / (double) totalWords;
            logger.info("Overall average word length: {:.2f}", overallAverage);
            context.getCounter("AverageReducer", "OverallAverage").setValue((long) (overallAverage * 100));
        }
    }
}