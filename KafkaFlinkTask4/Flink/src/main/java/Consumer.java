import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Duration;

public class Consumer {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        KafkaSource<String> kafkaSource= KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("shop-events")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> stream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kafka stream");


        DataStream<Sale> salesStream = stream
                .map(Sale::fromString)
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<Sale>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                                .withTimestampAssigner((event, timestamp) -> event.timestamp)
                );

        salesStream
                .keyBy(sale -> sale.category)
                .window(TumblingEventTimeWindows.of(Time.hours(1)))
                .process(new SalesAggregator())
                .print();

        env.execute("shop-events");
    }
}