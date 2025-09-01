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
 * Mapper class for the WordCount MapReduce job.
 * 
 * This mapper processes text input line by line, splits each line into words,
 * and emits each word with a count of 1. The reducer will then aggregate
 * these counts to produce the final word frequency.
 * 
 * Features:
 * - Efficient word splitting using compiled regex pattern
 * - Proper logging for debugging and monitoring
 * - Input validation and error handling
 * - Performance optimizations
 */
public class WordMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final Logger logger = LoggerFactory.getLogger(WordMapper.class);
    
    // Compile regex pattern once for better performance
    private static final Pattern WORD_PATTERN = Pattern.compile("\\W+");
    
    // Reuse objects to reduce garbage collection overhead
    private final Text word = new Text();
    private final IntWritable one = new IntWritable(1);
    
    // Counter for tracking processed lines
    private long lineCount = 0;
    private long wordCount = 0;

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        logger.debug("Initializing WordMapper");
    }

    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        try {
            // Convert the line to a string
            String line = value.toString().trim();
            
            // Skip empty lines
            if (line.isEmpty()) {
                return;
            }
            
            lineCount++;
            
            // Split the line into words using the compiled pattern
            String[] words = WORD_PATTERN.split(line);
            
            // Process each word
            for (String wordStr : words) {
                // Clean and validate the word
                wordStr = wordStr.trim().toLowerCase();
                
                // Skip empty words and very short words (likely noise)
                if (wordStr.length() > 1) {
                    word.set(wordStr);
                    context.write(word, one);
                    wordCount++;
                }
            }
            
            // Log progress every 1000 lines
            if (lineCount % 1000 == 0) {
                logger.info("Processed {} lines, {} words", lineCount, wordCount);
            }
            
        } catch (Exception e) {
            logger.error("Error processing line at offset {}: {}", key.get(), e.getMessage(), e);
            // Increment error counter
            context.getCounter("WordMapper", "ErrorLines").increment(1);
        }
    }

    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("WordMapper completed. Processed {} lines, {} words", lineCount, wordCount);
        
        // Set counters for monitoring
        context.getCounter("WordMapper", "TotalLines").setValue(lineCount);
        context.getCounter("WordMapper", "TotalWords").setValue(wordCount);
    }
}