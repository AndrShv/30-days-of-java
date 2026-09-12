package backend.independent_consumer_task.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutRequestDto {
    private UUID userId;
    private BigDecimal amount;
}