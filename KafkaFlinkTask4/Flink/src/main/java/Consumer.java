import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class Consumer {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
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
                        new ProcessingTimeTrailingBoundedOutOfOrdernessTimestampExtractor<Sale>(
                                Time.seconds(1), // maxOutOfOrderness
                                Time.seconds(1), // idlenessDetectionDuration
                                Time.seconds(1)  // processingTimeTrailingDuration
                        ) {
                            @Override
                            public long extractTimestamp(Sale element) {
                                return element.timestamp;
                            }
                        }
                );

        salesStream
                .keyBy(sale -> sale.category)
                .window(TumblingEventTimeWindows.of(Time.hours(1)))
                .process(new SalesAggregator())
                .print();

        env.execute("shop-events");
    }
}