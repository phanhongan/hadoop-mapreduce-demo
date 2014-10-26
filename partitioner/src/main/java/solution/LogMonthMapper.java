package solution;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class LogMonthMapper extends Mapper<LongWritable, Text, Text, Text> {

	public static List<String> months = Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");

  /**
   * Example input line:
   * 96.7.4.14 - - [24/Apr/2011:04:20:11 -0400] "GET /cat.jpg HTTP/1.1" 200 12433
   *
   */
  @Override
  public void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException {
    
    /*
     * Split the input line into space-delimited fields.
     */
    String[] fields = value.toString().split(" ");
    
    if (fields.length > 3) {
      
      /*
       * Save the first field in the line as the IP address.
       */
      String ip = fields[0];
      
      /*
       * The fourth field contains [dd/Mmm/yyyy:hh:mm:ss].
       * Split the fourth field into "/" delimited fields.
       * The second of these contains the month.
       */
      String[] dtFields = fields[3].split("/");
      if (dtFields.length > 1) {
        String theMonth = dtFields[1];
        
        /* check if it's a valid month, if so, write it out */
        if (months.contains(theMonth))
        	context.write(new Text(ip), new Text(theMonth));
      }
    }
  }
}
