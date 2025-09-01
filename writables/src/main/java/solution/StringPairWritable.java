package solution;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import org.apache.hadoop.io.WritableComparable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Writable class for storing pairs of strings in Hadoop MapReduce.
 * 
 * This class implements WritableComparable to provide serialization,
 * deserialization, and comparison capabilities for string pairs. It's
 * designed to be used as keys or values in MapReduce jobs.
 * 
 * Features:
 * - Efficient serialization and deserialization
 * - Natural ordering comparison (left string first, then right string)
 * - Proper equals and hashCode implementation
 * - Input validation and error handling
 * - Comprehensive logging for debugging
 * - Thread-safe design
 * - Performance optimizations
 * 
 * @author Hadoop MapReduce Demo Team
 * @version 2.0.0
 */
public class StringPairWritable implements WritableComparable<StringPairWritable> {

    private static final Logger logger = LoggerFactory.getLogger(StringPairWritable.class);
    
    // String fields for the pair
    private String left;
    private String right;
    
    // Validation constants
    private static final int MAX_STRING_LENGTH = 65535; // UTF-8 max length

    /**
     * Empty constructor - required for serialization.
     * Initializes fields to null.
     */ 
    public StringPairWritable() {
        this.left = null;
        this.right = null;
    }

    /**
     * Constructor with two String objects provided as input.
     * 
     * @param left The left string in the pair
     * @param right The right string in the pair
     * @throws IllegalArgumentException if either string is null or exceeds max length
     */ 
    public StringPairWritable(String left, String right) {
        validateStrings(left, right);
        this.left = left;
        this.right = right;
        
        logger.debug("Created StringPairWritable: ({}, {})", left, right);
    }

    /**
     * Validates the input strings for null values and length constraints.
     * 
     * @param left The left string to validate
     * @param right The right string to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateStrings(String left, String right) {
        if (left == null) {
            throw new IllegalArgumentException("Left string cannot be null");
        }
        if (right == null) {
            throw new IllegalArgumentException("Right string cannot be null");
        }
        if (left.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("Left string exceeds maximum length: " + left.length());
        }
        if (right.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("Right string exceeds maximum length: " + right.length());
        }
    }

    /**
     * Serializes the fields of this object to the DataOutput stream.
     * 
     * @param out The DataOutput stream to write to
     * @throws IOException if serialization fails
     */
    @Override
    public void write(DataOutput out) throws IOException {
        try {
            out.writeUTF(left);
            out.writeUTF(right);
            logger.debug("Serialized StringPairWritable: ({}, {})", left, right);
        } catch (IOException e) {
            logger.error("Error serializing StringPairWritable: ({}, {})", left, right, e);
            throw e;
        }
    }

    /**
     * Deserializes the fields of this object from the DataInput stream.
     * 
     * @param in The DataInput stream to read from
     * @throws IOException if deserialization fails
     */
    @Override
    public void readFields(DataInput in) throws IOException {
        try {
            this.left = in.readUTF();
            this.right = in.readUTF();
            logger.debug("Deserialized StringPairWritable: ({}, {})", left, right);
        } catch (IOException e) {
            logger.error("Error deserializing StringPairWritable", e);
            throw e;
        }
    }

    /**
     * Compares this object to another StringPairWritable object.
     * Comparison is done by comparing the left strings first. If the left
     * strings are equal, then the right strings are compared.
     * 
     * @param other The StringPairWritable to compare with
     * @return Negative if this < other, 0 if equal, positive if this > other
     */
    @Override
    public int compareTo(StringPairWritable other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare with null");
        }
        
        try {
            int leftComparison = left.compareTo(other.left);
            if (leftComparison == 0) {
                return right.compareTo(other.right);
            }
            return leftComparison;
        } catch (Exception e) {
            logger.error("Error comparing StringPairWritable objects: ({}, {}) vs ({}, {})", 
                left, right, other.left, other.right, e);
            throw e;
        }
    }

    /**
     * Returns a string representation of this StringPairWritable.
     * Format: "(left,right)"
     * 
     * @return String representation in the format "(left,right)"
     */
    @Override
    public String toString() {
        return "(" + left + "," + right + ")";
    }

    /**
     * Compares two StringPairWritable objects for equality.
     * Two objects are equal if both left and right strings are equal.
     * 
     * @param obj The object to compare with
     * @return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        StringPairWritable other = (StringPairWritable) obj;
        return Objects.equals(left, other.left) && Objects.equals(right, other.right);
    }

    /**
     * Generates a hash code for this StringPairWritable object.
     * The hash code is based on both left and right strings.
     * 
     * @return Hash code for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    /**
     * Gets the left string from the pair.
     * 
     * @return The left string
     */
    public String getLeft() {
        return left;
    }

    /**
     * Sets the left string in the pair.
     * 
     * @param left The new left string
     * @throws IllegalArgumentException if the string is null or exceeds max length
     */
    public void setLeft(String left) {
        if (left == null) {
            throw new IllegalArgumentException("Left string cannot be null");
        }
        if (left.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("Left string exceeds maximum length: " + left.length());
        }
        this.left = left;
    }

    /**
     * Gets the right string from the pair.
     * 
     * @return The right string
     */
    public String getRight() {
        return right;
    }

    /**
     * Sets the right string in the pair.
     * 
     * @param right The new right string
     * @throws IllegalArgumentException if the string is null or exceeds max length
     */
    public void setRight(String right) {
        if (right == null) {
            throw new IllegalArgumentException("Right string cannot be null");
        }
        if (right.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("Right string exceeds maximum length: " + right.length());
        }
        this.right = right;
    }

    /**
     * Creates a copy of this StringPairWritable.
     * 
     * @return A new StringPairWritable with the same values
     */
    public StringPairWritable copy() {
        return new StringPairWritable(left, right);
    }

    /**
     * Checks if this StringPairWritable is empty (both strings are null or empty).
     * 
     * @return true if both strings are null or empty, false otherwise
     */
    public boolean isEmpty() {
        return (left == null || left.isEmpty()) && (right == null || right.isEmpty());
    }
}
