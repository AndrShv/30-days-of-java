package backend.transactional_outbox_pattern_example.entity;
import jakarta.persistence.*;
import lombok.*;
import backend.transactional_outbox_pattern_example.enums.PayoutStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payouts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayoutEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PayoutStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;



}
