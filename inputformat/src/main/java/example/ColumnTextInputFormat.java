package example;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Input Format for processing fixed-width column-based text files.
 * 
 * This class extends FileInputFormat to create a custom input format that
 * processes text files with fixed-width records. It ensures that splits
 * are aligned to record boundaries and handles record width configuration.
 * 
 * Features:
 * - Fixed-width record processing
 * - Split boundary alignment
 * - Configurable record width
 * - Proper error handling
 * - Performance optimization for large files
 */
public class ColumnTextInputFormat extends FileInputFormat<Text, Text> {

    private static final Logger logger = LoggerFactory.getLogger(ColumnTextInputFormat.class);
    
    // Default record width in characters
    private int recordWidth = 50;
    
    // Configuration key for record width
    public static final String RECORD_WIDTH_KEY = "column.text.record.width";

    /**
     * Creates a new RecordReader for this input format.
     * 
     * @param split The input split to process
     * @param context The task attempt context
     * @return A configured ColumnTextRecordReader
     */
    @Override
    public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {

        try {
            // Get record width from configuration if available
            String recordWidthStr = context.getConfiguration().get(RECORD_WIDTH_KEY);
            if (recordWidthStr != null) {
                try {
                    recordWidth = Integer.parseInt(recordWidthStr);
                    logger.debug("Using configured record width: {}", recordWidth);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid record width configuration '{}', using default: {}", recordWidthStr, recordWidth);
                }
            }
            
            // Create and initialize the record reader
            ColumnTextRecordReader recordReader = new ColumnTextRecordReader();
            recordReader.initialize(split, context);
            
            logger.debug("Created ColumnTextRecordReader for split: {}", split);
            return recordReader;
            
        } catch (Exception e) {
            logger.error("Error creating ColumnTextRecordReader", e);
            throw new IOException("Failed to create ColumnTextRecordReader", e);
        }
    }

    /**
     * Computes the optimal split size to ensure records are not split across boundaries.
     * 
     * @param blockSize The HDFS block size
     * @param minSize The minimum split size
     * @param maxSize The maximum split size
     * @return The computed split size aligned to record boundaries
     */
    @Override
    protected long computeSplitSize(long blockSize, long minSize, long maxSize) {
        long defaultSize = super.computeSplitSize(blockSize, minSize, maxSize);
        
        logger.debug("Computing split size - blockSize: {}, minSize: {}, maxSize: {}, defaultSize: {}, recordWidth: {}", 
            blockSize, minSize, maxSize, defaultSize, recordWidth);

        // First, if the default size is less than the length of a
        // raw record, let's bump it up to a minimum of at least ONE record
        // length
        if (defaultSize < recordWidth) {
            logger.debug("Default size {} is less than record width {}, adjusting to {}", 
                defaultSize, recordWidth, recordWidth);
            return recordWidth;
        }

        // Determine the split size, it should be as close as possible to the
        // default size, but should NOT split within a record... each split
        // should contain a complete set of records with the first record
        // starting at the first byte in the split and the last record ending
        // with the last byte in the split.
        long splitSize = ((long) (Math.floor((double) defaultSize / (double) recordWidth))) * recordWidth;
        
        logger.debug("Computed split size: {} (aligned to record boundaries)", splitSize);
        return splitSize;
    }

    /**
     * Sets the record width for this input format.
     * 
     * @param recordWidth The width of each record in characters
     */
    public void setRecordWidth(int recordWidth) {
        if (recordWidth <= 0) {
            throw new IllegalArgumentException("Record width must be positive, got: " + recordWidth);
        }
        this.recordWidth = recordWidth;
        logger.debug("Record width set to: {}", recordWidth);
    }

    /**
     * Gets the current record width.
     * 
     * @return The current record width in characters
     */
    public int getRecordWidth() {
        return recordWidth;
    }
}
