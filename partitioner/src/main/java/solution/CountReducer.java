package solution;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/* Counts the number of values associated with a key */

public class CountReducer extends Reducer<Text, Text, Text, IntWritable> {

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		/*
		 * Iterate over the values iterable and count the number
		 * of values in it. Emit the key (unchanged) and an IntWritable
		 * containing the number of values.
		 */

		int count = 0;

		/*
		 * Use for loop to count items in the iterator. 
		 */
		
		/* Ignore warnings that we
		 * don't use the value -- in this case, we only need to count the
		 * values, not use them.
		 */
		for (@SuppressWarnings("unused")
		Text value : values) {

			/*
			 * for each item in the list, increment the count
			 */
			count++;
		}

		context.write(key, new IntWritable(count));
	}
}
