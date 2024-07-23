import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;



public class SalesAggregator extends ProcessWindowFunction<Sale, String, String, TimeWindow> {
    @Override
    public void process(String key, Context context, Iterable<Sale> elements, Collector<String> out) {
        int sum = 0;
        for (Sale sale : elements) {
            sum += sale.amount;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        long windowStartEpochMilli = context.window().getStart();
        long windowEndEpochMilli = context.window().getEnd() - 1000;

        String windowStart = formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(windowStartEpochMilli), ZoneId.systemDefault()));
        String windowEnd = formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(windowEndEpochMilli), ZoneId.systemDefault()));

        System.out.println(windowEnd);

        out.collect(windowStart + ", " + key + ", " + sum);
    }
}