import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;

import java.sql.Timestamp;

@Table(keyspace = "lab_space", name = "sales")
public class SaleDTO {
    @Column(name = "category")
    public String category;
    @Column(name = "amount")
    public int amount;
    @Column(name = "timestamp")
    public String timestamp;

    public SaleDTO() {}

    public SaleDTO(String category, int amount, String timestamp) {
        this.category = category;
        this.amount = amount;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}