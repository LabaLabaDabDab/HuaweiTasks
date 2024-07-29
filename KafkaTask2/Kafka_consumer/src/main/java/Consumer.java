import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Consumer {
    private static final Logger logger = LogManager.getLogger(Consumer.class);
    private static int currentSum = 0;

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.error("Please provide the consumer instance number as arguments.");
            System.exit(1);
        }

        String consumerInstance = args[0];

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "shop-events-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        try (consumer) {
            consumer.subscribe(Collections.singletonList("shop-events"));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String[] values = record.value().split(",");
                    int price = Integer.parseInt(values[1]);
                    currentSum += price;
                    String updatedMessage = values[0] + "," + currentSum + "," + values[2];
                    System.out.println(consumerInstance + ": " + updatedMessage);
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        }
    }
}