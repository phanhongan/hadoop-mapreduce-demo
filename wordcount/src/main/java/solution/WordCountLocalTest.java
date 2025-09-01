package solution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local test version of WordCount that bypasses Hadoop framework
 * for testing purposes when Hadoop cluster is not available.
 */
public class WordCountLocalTest {
    
    private static final Logger logger = LoggerFactory.getLogger(WordCountLocalTest.class);
    private static final Pattern WORD_PATTERN = Pattern.compile("\\s+");
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: WordCountLocalTest <input_file> <output_file>");
            System.exit(1);
        }
        
        String inputFile = args[0];
        String outputFile = args[1];
        
        try {
            logger.info("Starting local WordCount test with input: {} and output: {}", inputFile, outputFile);
            
            // Read input file
            String content = Files.readString(Paths.get(inputFile));
            logger.info("Read {} characters from input file", content.length());
            
            // Process words
            Map<String, Integer> wordCounts = processWords(content);
            logger.info("Processed {} unique words", wordCounts.size());
            
            // Write output
            StringBuilder output = new StringBuilder();
            wordCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                    .thenComparing(Map.Entry.comparingByKey()))
                .forEach(entry -> output.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n"));
            
            Files.writeString(Paths.get(outputFile), output.toString());
            logger.info("Successfully wrote results to output file");
            
            // Display results
            System.out.println("\n=== WordCount Results ===");
            wordCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                    .thenComparing(Map.Entry.comparingByKey()))
                .limit(10)
                .forEach(entry -> System.out.printf("%-15s %d%n", entry.getKey(), entry.getValue()));
            
            if (wordCounts.size() > 10) {
                System.out.println("... and " + (wordCounts.size() - 10) + " more words");
            }
            
        } catch (IOException e) {
            logger.error("Error during local WordCount execution", e);
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static Map<String, Integer> processWords(String content) {
        Map<String, Integer> wordCounts = new HashMap<>();
        
        String[] lines = content.split("\n");
        int lineCount = 0;
        int wordCount = 0;
        
        for (String line : lines) {
            lineCount++;
            line = line.trim();
            
            if (line.isEmpty()) {
                continue;
            }
            
            String[] words = WORD_PATTERN.split(line);
            for (String word : words) {
                word = word.trim().toLowerCase();
                
                // Skip empty words and very long words
                if (word.isEmpty() || word.length() > 100) {
                    continue;
                }
                
                // Remove punctuation from beginning and end
                word = word.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
                
                if (!word.isEmpty()) {
                    wordCounts.merge(word, 1, Integer::sum);
                    wordCount++;
                }
            }
        }
        
        logger.info("Processed {} lines and {} words", lineCount, wordCount);
        return wordCounts;
    }
}
