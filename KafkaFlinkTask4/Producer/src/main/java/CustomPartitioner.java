import com.google.common.hash.Hashing;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.errors.InvalidTopicException;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class CustomPartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        int numPartitions = cluster.partitionCountForTopic(topic);

        if (numPartitions == 0) {
            throw new InvalidTopicException("Topic has zero partitions: " + topic);
        }

        if (keyBytes == null) {
            return 0;
        }

        int hash = Hashing.murmur3_128().hashBytes(keyBytes).asInt();

        return ((hash >> 5) & Integer.MAX_VALUE) % numPartitions;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
