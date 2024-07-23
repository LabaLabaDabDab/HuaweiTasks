import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Producer {
    private static final Logger logger = LogManager.getLogger(String.valueOf(Producer.class));

    public static void main(String[] args) throws IOException {
        String topicName = "shop-events";
        String csvFile = "Producer\\sales1.txt";

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        Set<String> uniqueKeys = new HashSet<>();
        collectUniqueKeys(csvFile, uniqueKeys);

        Map<String, Integer> partitionMap = assignPartitions(uniqueKeys);

        publishFileToTopic(producer, topicName, csvFile, partitionMap);

        logger.info("Producer application finished.");

        producer.flush();
        producer.close();
    }

    private static void collectUniqueKeys(String csvFile, Set<String> uniqueKeys) throws IOException {
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
    }
}