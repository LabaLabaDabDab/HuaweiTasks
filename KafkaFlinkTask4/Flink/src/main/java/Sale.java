import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Sale {
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
        long timestamp = parseTimestamp(parts[2]);
        return new Sale(category, amount, timestamp);
    }

    private static long parseTimestamp(String timestampStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(timestampStr, formatter);
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}