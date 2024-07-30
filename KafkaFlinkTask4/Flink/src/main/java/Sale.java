import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

public class Sale {
    private static final Logger logger = LoggerFactory.getLogger(Sale.class);
    public String category;
    public int amount;
    public long timestamp;

    public Sale(String category, int amount, long timestamp) {
        this.category = category;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public static Sale fromString(String line) {
        String[] parts = line.split(",");
        String category = parts[0];
        int amount = Integer.parseInt(parts[1]);
        long timestamp = Timestamp.valueOf(parts[2]).getTime();
        logger.info("Parsed Sale: category={}, amount={}, timestamp={}", category, amount, timestamp);
        return new Sale(category, amount, timestamp);
    }
}