package solution;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;

public class ProcessLogs {

  public static void main(String[] args) throws Exception {

    if (args.length != 2) {
      System.out.printf("Usage: ProcessLogs <input dir> <output dir>\n");
      System.exit(-1);
    }

    Job job = Job.getInstance();
    job.setJarByClass(ProcessLogs.class);
    job.setJobName("Process Logs");

    FileInputFormat.setInputPaths(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    job.setMapperClass(LogMonthMapper.class);
    job.setReducerClass(CountReducer.class);
    
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    
    /*
     * Set up the partitioner. Specify 12 reducers - one for each
     * month of the year. The partitioner class must have a 
     * getPartition method that returns a number between 0 and 11.
     * This number will be used to assign the intermediate output
     * to one of the reducers.
     */
    job.setNumReduceTasks(12);
    
    /*
     * Specify the partitioner class.
     */
    job.setPartitionerClass(MonthPartitioner.class);

    boolean success = job.waitForCompletion(true);
    System.exit(success ? 0 : 1);
  }
}
