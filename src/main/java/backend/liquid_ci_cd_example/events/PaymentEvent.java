package backend.liquid_ci_cd_example.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Getter
@Service
@AllArgsConstructor
public class PaymentEvent {
    private UUID paymentId;
    private UUID userId;
    private BigDecimal amount;
}
