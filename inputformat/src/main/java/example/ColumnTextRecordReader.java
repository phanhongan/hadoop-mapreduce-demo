package example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom RecordReader for processing fixed-width column-based text files.
 * 
 * This RecordReader processes text files with fixed-width fields, extracting
 * specific columns based on configured field widths. It handles split boundaries
 * and provides progress tracking for monitoring.
 * 
 * Features:
 * - Fixed-width field extraction
 * - Configurable field widths
 * - Progress tracking
 * - Proper error handling
 * - Performance optimization
 * - Split boundary handling
 */
public class ColumnTextRecordReader extends RecordReader<Text, Text> {

    private static final Logger logger = LoggerFactory.getLogger(ColumnTextRecordReader.class);
    
    // Configuration keys for field widths
    public static final String KEY_WIDTH_KEY = "column.text.key.width";
    public static final String LASTNAME_WIDTH_KEY = "column.text.lastname.width";
    public static final String FIRSTNAME_WIDTH_KEY = "column.text.firstname.width";
    public static final String DATE_WIDTH_KEY = "column.text.date.width";
    
    // Default field widths
    private static final int DEFAULT_KEY_WIDTH = 7;
    private static final int DEFAULT_LASTNAME_WIDTH = 25;
    private static final int DEFAULT_FIRSTNAME_WIDTH = 10;
    private static final int DEFAULT_DATE_WIDTH = 8;

    private Text key = null;
    private Text value = null;

    private long start; // byte offset into the file for this split
    private long pos; // current location in the file
    private long end; // end offset in the file for this split

    private FSDataInputStream fileIn; // input stream for the file

    private int keyWidth; // number of bytes of the key field
    private int lastNameWidth; // number of bytes of the surname field
    private int firstNameWidth; // number of bytes of the first name field
    private int dateWidth; // number of bytes of the date field

    private byte[] keyBytes;
    private byte[] dateBytes;
    private byte[] lastNameBytes;
    private byte[] firstNameBytes;
    
    // Statistics tracking
    private long recordsProcessed = 0;
    private long errorsEncountered = 0;

    @Override
    public boolean nextKeyValue() throws IOException {
        if (pos >= end) {
            return false;
        }

        try {
            // Read fixed-width fields from the current position
            fileIn.readFully(pos, keyBytes);
            pos = pos + keyWidth;
            
            fileIn.readFully(pos, lastNameBytes);
            pos = pos + lastNameWidth;
            
            fileIn.readFully(pos, firstNameBytes);
            pos = pos + firstNameWidth;
            
            fileIn.readFully(pos, dateBytes);
            pos = pos + dateWidth;

            // Create key and value from the extracted fields
            key = new Text(keyBytes);
            String valueString = new String(lastNameBytes, StandardCharsets.UTF_8).trim() + "," + 
                               new String(firstNameBytes, StandardCharsets.UTF_8).trim() + "\t" +
                               new String(dateBytes, StandardCharsets.UTF_8).trim();
            value = new Text(valueString);
            
            recordsProcessed++;
            
            // Log progress every 1000 records
            if (recordsProcessed % 1000 == 0) {
                logger.debug("Processed {} records, current position: {}/{}", recordsProcessed, pos, end);
            }

        } catch (IOException e) {
            logger.error("Error reading record at position {}: {}", pos, e.getMessage());
            key = null;
            value = null;
            errorsEncountered++;
            return false;
        }

        return true;
    }

    @Override
    public Text getCurrentKey() {
        return key;
    }

    @Override
    public Text getCurrentValue() {
        return value;
    }

    @Override
    public void initialize(InputSplit genericSplit, TaskAttemptContext context) throws IOException,
            InterruptedException {

        try {
            // Get the file name and start and end positions for this split
            FileSplit split = (FileSplit) genericSplit;
            this.start = split.getStart();
            this.end = start + split.getLength();
            this.pos = start;
            Path file = split.getPath();

            logger.debug("Initializing RecordReader for split: start={}, end={}, file={}", start, end, file);

            // Open the file and seek to the start of the split
            Configuration job = context.getConfiguration();
            FileSystem fs = file.getFileSystem(job);
            fileIn = fs.open(file);

            // Configure field widths from configuration or use defaults
            configureFieldWidths(job);

            // Create byte buffers to hold the input
            keyBytes = new byte[keyWidth];
            dateBytes = new byte[dateWidth];
            lastNameBytes = new byte[lastNameWidth];
            firstNameBytes = new byte[firstNameWidth];

            logger.debug("RecordReader initialized with field widths - key: {}, lastName: {}, firstName: {}, date: {}", 
                keyWidth, lastNameWidth, firstNameWidth, dateWidth);

        } catch (Exception e) {
            logger.error("Error initializing RecordReader", e);
            throw new IOException("Failed to initialize RecordReader", e);
        }
    }

    /**
     * Configure field widths from job configuration or use defaults.
     * 
     * @param conf The job configuration
     */
    private void configureFieldWidths(Configuration conf) {
        keyWidth = conf.getInt(KEY_WIDTH_KEY, DEFAULT_KEY_WIDTH);
        lastNameWidth = conf.getInt(LASTNAME_WIDTH_KEY, DEFAULT_LASTNAME_WIDTH);
        firstNameWidth = conf.getInt(FIRSTNAME_WIDTH_KEY, DEFAULT_FIRSTNAME_WIDTH);
        dateWidth = conf.getInt(DATE_WIDTH_KEY, DEFAULT_DATE_WIDTH);
        
        // Validate field widths
        if (keyWidth <= 0 || lastNameWidth <= 0 || firstNameWidth <= 0 || dateWidth <= 0) {
            throw new IllegalArgumentException("All field widths must be positive");
        }
        
        logger.debug("Field widths configured - key: {}, lastName: {}, firstName: {}, date: {}", 
            keyWidth, lastNameWidth, firstNameWidth, dateWidth);
    }

    /**
     * Return percentage complete for progress tracking.
     * 
     * @return Progress as a float between 0.0 and 1.0
     */
    @Override
    public float getProgress() throws IOException, InterruptedException {
        if (start == end) {
            return 0.0f;
        } else {
            return Math.min(1.0f, (pos - start) / (float) (end - start));
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (fileIn != null) {
                fileIn.close();
            }
            
            logger.info("RecordReader closed. Processed {} records with {} errors", 
                recordsProcessed, errorsEncountered);
                
        } catch (IOException e) {
            logger.error("Error closing RecordReader", e);
            throw e;
        }
    }

    /**
     * Get the total number of records processed by this reader.
     * 
     * @return The number of records processed
     */
    public long getRecordsProcessed() {
        return recordsProcessed;
    }

    /**
     * Get the total number of errors encountered during processing.
     * 
     * @return The number of errors encountered
     */
    public long getErrorsEncountered() {
        return errorsEncountered;
    }
}
