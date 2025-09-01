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
 * Mapper class for the Average Word Length MapReduce job.
 * 
 * This mapper processes text input line by line, extracts the first letter of each word,
 * and emits (letter, word_length) pairs. The reducer will then calculate the average
 * word length for each starting letter.
 * 
 * Features:
 * - Efficient word splitting using compiled regex pattern
 * - Proper logging for debugging and monitoring
 * - Input validation and error handling
 * - Performance optimizations
 * - Case-insensitive letter processing
 */
public class LetterMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final Logger logger = LoggerFactory.getLogger(LetterMapper.class);
    
    // Compile regex pattern once for better performance
    private static final Pattern WORD_PATTERN = Pattern.compile("\\W+");
    
    // Reuse objects to reduce garbage collection overhead
    private final Text letter = new Text();
    private final IntWritable wordLength = new IntWritable();
    
    // Counters for tracking processed data
    private long lineCount = 0;
    private long wordCount = 0;
    private long totalWordLength = 0;

    @Override
    public void setup(Context context) throws IOException, InterruptedException {
        logger.debug("Initializing LetterMapper");
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
            
            // Split the line into words using the compiled pattern
            String[] words = WORD_PATTERN.split(line);
            
            // Process each word
            for (String word : words) {
                // Clean and validate the word
                word = word.trim();
                
                // Skip empty words and very short words (likely noise)
                if (word.length() > 1) {
                    // Extract the first letter and convert to lowercase
                    String firstLetter = word.substring(0, 1).toLowerCase();
                    
                    // Validate that it's actually a letter
                    if (Character.isLetter(firstLetter.charAt(0))) {
                        letter.set(firstLetter);
                        wordLength.set(word.length());
                        context.write(letter, wordLength);
                        
                        wordCount++;
                        totalWordLength += word.length();
                    }
                }
            }
            
            // Log progress every 1000 lines
            if (lineCount % 1000 == 0) {
                logger.info("Processed {} lines, {} words, avg length: {:.2f}", 
                    lineCount, wordCount, 
                    wordCount > 0 ? (double) totalWordLength / wordCount : 0.0);
            }
            
        } catch (Exception e) {
            logger.error("Error processing line at offset {}: {}", key.get(), e.getMessage(), e);
            // Increment error counter
            context.getCounter("LetterMapper", "ErrorLines").increment(1);
        }
    }

    @Override
    public void cleanup(Context context) throws IOException, InterruptedException {
        logger.info("LetterMapper completed. Processed {} lines, {} words, total length: {}", 
            lineCount, wordCount, totalWordLength);
        
        // Set counters for monitoring
        context.getCounter("LetterMapper", "TotalLines").setValue(lineCount);
        context.getCounter("LetterMapper", "TotalWords").setValue(wordCount);
        context.getCounter("LetterMapper", "TotalWordLength").setValue(totalWordLength);
    }
}
