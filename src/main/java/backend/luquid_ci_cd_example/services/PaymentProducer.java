package backend.luquid_ci_cd_example.services;

import backend.luquid_ci_cd_example.entity.OutboxEventEntity;
import backend.luquid_ci_cd_example.enums.OutboxStatus;
import backend.luquid_ci_cd_example.repos.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private static final Logger log = LoggerFactory.getLogger(PaymentProducer.class);

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${luquid.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${luquid.rabbitmq.routing-key}")
    private String routingKey;



    @Transactional
    @Scheduled(fixedDelayString = "5000")
    public void producePayments() {
        List<OutboxEventEntity> pendingEvents = outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        if  (pendingEvents.isEmpty()) {
            return;
        }
        log.info("Sending {} pending events", pendingEvents.size());
        for (OutboxEventEntity event : pendingEvents) {
            try {
                rabbitTemplate.convertAndSend(exchangeName, routingKey, event.getPayload());
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
