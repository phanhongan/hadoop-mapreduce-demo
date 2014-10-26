package solution;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ReadCompressedSequenceFile extends Configured implements Tool {

  @Override
  public int run(String[] args) throws Exception {

    if (args.length != 2) {
      System.out
          .printf("Usage: ReadCompressedSequenceFile <input dir> <output dir>\n");
      return -1;
    }

    Job job = Job.getInstance(getConf());
    job.setJarByClass(ReadCompressedSequenceFile.class);
    job.setJobName("Read Compressed Sequence File");

    FileInputFormat.setInputPaths(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    /*
     * We are using a SequenceFile as the input file.
     * Therefore, we must call setInputFormatClass.
     * There is no need to call setOutputFormatClass, because the
     * application uses a text file on output.
     */
    job.setInputFormatClass(SequenceFileInputFormat.class);

    /*
     * There is no need to set compression options for the input file.
     * The compression implementation details are encoded within the
     * input SequenceFile.    
     */

    /*
     * This is a map-only job that uses the default (identity mapper), so we do not need to set
     * the mapper or reducer classes.  We just need to set the number of reducers to 0.
     */
    job.setNumReduceTasks(0);

    boolean success = job.waitForCompletion(true);
    return success ? 0 : 1;
  }

  public static void main(String[] args) throws Exception {
    int exitCode = ToolRunner.run(new Configuration(), new ReadCompressedSequenceFile(), args);
    System.exit(exitCode);
  }
}
