package backend.transactional_outbox_pattern_example.publishers;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import backend.transactional_outbox_pattern_example.entity.OutboxEventEntity;
import backend.transactional_outbox_pattern_example.enums.OutboxStatus;
import backend.transactional_outbox_pattern_example.repo.OutboxRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PayoutEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PayoutEventPublisher.class);

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${outbox.rabbitmq.exchange.name:payout_outbox_exchange}")
    private String exchange;

    @Value("${outbox.rabbitmq.routing.key:payout_outbox_routing_key}")
    private String routingKey;


    @Scheduled(fixedDelayString = "${outbox.scheduler.fixed-delay-ms:3000}")
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEventEntity> pendingEvents = outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Found {} pending outbox events to publish", pendingEvents.size());

        for (OutboxEventEntity event : pendingEvents) {
            try {
                rabbitTemplate.convertAndSend(exchange, routingKey, event.getPayload());
                event.setStatus(OutboxStatus.PROCESSED);
                outboxRepository.save(event);
                log.info("Successfully published outbox event id={}", event.getId());
            } catch (Exception e) {
                log.error("Failed to publish outbox event id={}", event.getId(), e);
                event.setStatus(OutboxStatus.FAILED);
                outboxRepository.save(event);
            }
        }
    }
}
