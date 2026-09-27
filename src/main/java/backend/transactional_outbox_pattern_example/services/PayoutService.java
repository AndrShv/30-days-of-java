package backend.transactional_outbox_pattern_example.services;

import backend.independent_consumer_task.dto.PayoutRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import backend.transactional_outbox_pattern_example.dto.PayoutRequestedEvent;
import backend.transactional_outbox_pattern_example.entity.OutboxEventEntity;
import backend.transactional_outbox_pattern_example.entity.PayoutEntity;
import backend.transactional_outbox_pattern_example.enums.OutboxStatus;
import backend.transactional_outbox_pattern_example.enums.PayoutStatus;
import backend.transactional_outbox_pattern_example.repo.OutboxRepository;
import backend.transactional_outbox_pattern_example.repo.PayoutRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayoutService {

    private final PayoutRepository payoutRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public PayoutEntity createPayout(PayoutRequestDto dto) {
        UUID payoutId = UUID.randomUUID();
        PayoutEntity payout = new PayoutEntity(
                payoutId,
                dto.getUserId(),
                dto.getAmount(),
                PayoutStatus.CREATED,
                LocalDateTime.now()
        );
        payoutRepository.save(payout);

        PayoutRequestedEvent event = new PayoutRequestedEvent(
                payout.getId(),
                payout.getUserId(),
                payout.getAmount()
        );

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize payout event to JSON", e);
        }

        OutboxEventEntity outboxEvent = new OutboxEventEntity(
                UUID.randomUUID(),
                "PAYOUT",
                payout.getId().toString(),
                "PAYOUT_CREATED",
                payloadJson,
                OutboxStatus.PENDING,
                LocalDateTime.now()
        );
        outboxRepository.save(outboxEvent);
        return payout;
    }
}