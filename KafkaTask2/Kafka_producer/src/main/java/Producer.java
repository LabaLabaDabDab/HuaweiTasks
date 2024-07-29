import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Producer {
    private static final Logger logger = LogManager.getLogger(String.valueOf(Producer.class));

    public static void main(String[] args) throws IOException {
        String topicName = "shop-events";
        String csvFile = "Kafka_producer\\sales1.txt";

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        try (var lines = Files.lines(Paths.get(csvFile))) {
            lines.forEach(line -> {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String key = parts[0];
                    ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, line);
                    producer.send(record);
                }
            });
        } catch (IOException e) {
            logger.error("Error reading the file", e);
        }
        logger.info("Producer application finished.");

        producer.flush();
        producer.close();
    }

/*    private static void collectUniqueKeys(String csvFile, Set<String> uniqueKeys) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String key = line.split(",")[0];
                uniqueKeys.add(key);
            }
        }
    }

    private static Map<String, Integer> assignPartitions(Set<String> uniqueKeys) {
        Map<String, Integer> partitionMap = new HashMap<>();
        int partition = 0;
        for (String key : uniqueKeys) {
            partitionMap.put(key, partition);
            partition = (partition + 1) % 4;
        }
        return partitionMap;
    }

    private static void publishFileToTopic(KafkaProducer<String, String> producer, String topicName, String csvFile, Map<String, Integer> partitionMap) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String key = line.split(",")[0];
                int partition = partitionMap.get(key);
                producer.send(new ProducerRecord<>(topicName, partition, key, line));
                logger.info("Sent record to topic {} with key {}: {}", topicName, key, line);
            }
        }
    }*/
}