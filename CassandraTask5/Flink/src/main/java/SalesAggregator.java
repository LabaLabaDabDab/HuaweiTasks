import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class SalesAggregator extends ProcessWindowFunction<Sale, SaleDTO, String, TimeWindow> {
    @Override
    public void process(String key, Context context, Iterable<Sale> elements, Collector<SaleDTO> out) {
        int sum = 0;
        for (Sale sale : elements) {
            sum += sale.amount;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        long windowStartEpochMilli = context.window().getStart();

        String windowStart = formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(windowStartEpochMilli), ZoneId.systemDefault()));

        out.collect(new SaleDTO(key, sum, windowStart));
    }
}