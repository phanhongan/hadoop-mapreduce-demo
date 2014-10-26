package solution;


import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Example input line:
 * 96.7.4.14 - - [24/Apr/2011:04:20:11 -0400] "GET /cat.jpg HTTP/1.1" 200 12433
 *
 */
public class ImageCounterMapper extends
    Mapper<LongWritable, Text, Text, IntWritable> {

  @Override
  public void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException {
    
    /*
     * Split the line using the double-quote character as the delimiter.
     */
    String[] fields = value.toString().split("\"");
    if (fields.length > 1) {
      String request = fields[1];
      
      /*
       * Split the part of the line after the first double quote
       * using the space character as the delimiter to get a file name.
       */
      fields = request.split(" ");
      
      /*
       * Increment a counter based on the file's extension.
       */
      if (fields.length > 1) {
        String fileName = fields[1].toLowerCase();
        if (fileName.endsWith(".jpg")) {
          context.getCounter("ImageCounter", "jpg").increment(1);
        } else if (fileName.endsWith(".gif")) {
          context.getCounter("ImageCounter", "gif").increment(1);
        } else {
          context.getCounter("ImageCounter", "other").increment(1);
        }
      }
    }
  }
}