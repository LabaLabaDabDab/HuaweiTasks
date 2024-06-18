import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class LetterPartitioner extends Partitioner<Text, IntWritable> {
    @Override
    public int getPartition(Text key, IntWritable value, int numPartitions) {
        char firstChar = key.toString().toLowerCase().charAt(0);
        if (firstChar >= 'a' && firstChar <= 'i') {
            return 0;
        } else if (firstChar >= 'j' && firstChar <= 'r') {
            return 1;
        } else {
            return 2;
        }
    }
}