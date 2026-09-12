package backend.transactional_outbox_pattern_example.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutRequestedEvent {
    private UUID payoutId;
    private UUID userId;
    private BigDecimal amount;

}
