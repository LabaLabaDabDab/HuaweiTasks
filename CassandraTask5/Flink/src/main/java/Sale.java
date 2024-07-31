import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

@Table(keyspace = "lab_space", name = "sales")
public class Sale {
    private static final Logger logger = LoggerFactory.getLogger(Sale.class);
    @Column(name = "category")
    public String category;
    @Column(name = "amount")
    public int amount;
    @Column(name = "timestamp")
    public long timestamp;

    public Sale() {}

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}