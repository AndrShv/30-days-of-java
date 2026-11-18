package backend.concurrency.tasks.executor;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class UserReport {
    private UserProfile profile;
    private OrderSummary orderSummary;
    private int bonusPoints;
    private long executionTimeMs;

}
