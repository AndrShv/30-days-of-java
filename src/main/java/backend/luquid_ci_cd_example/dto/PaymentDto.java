package backend.luquid_ci_cd_example.dto;


import backend.luquid_ci_cd_example.enums.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
public class PaymentDto {
    @NonNull
    private UUID userId;

    @NonNull
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}
