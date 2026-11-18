package backend.concurrency.tasks.executor;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class OrderSummary {
    private double totalAmount;
    private int orderCount;
}
