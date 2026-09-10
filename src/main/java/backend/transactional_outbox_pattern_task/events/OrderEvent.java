package backend.transactional_outbox_pattern_task.events;


import backend.transactional_outbox_pattern_task.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OrderEvent {

    private UUID orderId;
    private UUID userId;
    private UUID productId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

}
