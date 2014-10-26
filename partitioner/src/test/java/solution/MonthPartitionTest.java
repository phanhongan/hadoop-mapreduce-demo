package solution;

import static org.junit.Assert.assertEquals;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.junit.Test;

public class MonthPartitionTest {

	static String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	MonthPartitioner<Text,Text> mpart;

	@Test
	public void testMonthPartition() {

		mpart=new MonthPartitioner<Text, Text>();
		mpart.setConf(new Configuration());
		int result;
		for (int i = 0; i < months.length; i++) {
			result = mpart.getPartition(new Text("foo"), new Text(months[i]), 12);
			assertEquals(result,i);			
		}
		
	}

}
