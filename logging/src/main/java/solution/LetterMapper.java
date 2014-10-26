package solution;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;


public class LetterMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

	boolean caseSensitive = false;

	/*
	 * Set up a logger for this class.
	 */
	private static final Logger LOGGER = Logger.getLogger(LetterMapper.class);
	
	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {


		String line = value.toString();

		for (String word : line.split("\\W+")) {
			if (word.length() > 0) {

				/*
				 * Obtain the first letter of the word
				 */
				String letter;

				if (caseSensitive)
					letter = word.substring(0, 1);
				else
					letter = word.substring(0, 1).toLowerCase();

				context.write(new Text(letter), new IntWritable(word.length()));
			}
		}
	}

	@Override
	public void setup(Context context) {
		
		Configuration conf = context.getConfiguration();
		caseSensitive = conf.getBoolean("caseSensitive", false);

		/*
		 * If Debug logging is enabled, log a message indicating 
		 * the value of caseSensitive.
		 */
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Case sensitive: " + caseSensitive);
		}
	}

}
