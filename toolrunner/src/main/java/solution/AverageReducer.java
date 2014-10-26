package solution;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * To define a reduce function for your MapReduce job, subclass
 * the Reducer class and override the reduce method.
 * The class definition requires four parameters: 
 * @param The data type of the input key - Text
 * @param The data type of the input value - IntWritable
 * @param The data type of the output key - Text
 * @param The data type of the output value - DoubleWritable
 */
public class AverageReducer extends
    Reducer<Text, IntWritable, Text, DoubleWritable> {

  /**
   * The reduce method runs once for each key received from
   * the shuffle and sort phase of the MapReduce framework.
   * The method receives:
   * @param A key of type Text
   * @param A set of values of type IntWritable
   * @param A Context object
   */
  @Override
  public void reduce(Text key, Iterable<IntWritable> values, Context context)
      throws IOException, InterruptedException {

    long sum = 0, count = 0;

    /*
     * For each value in the set of values passed to us by the mapper:
     */
    for (IntWritable value : values) {
      
      /*
       * Add up the values and increment the count
       */
      sum += value.get();
      count++;
    }
    if (count != 0) {
      
      /*
       * The average length is the sum of the values divided by the count.
       */
      double result = (double)sum / (double)count;
     
      /*
       * Call the write method on the Context object to emit a key
       * (the words' starting letter) and a value (the average length 
       * per word starting with this letter) from the reduce method. 
       */
      context.write(key, new DoubleWritable(result));
    }
  }
}