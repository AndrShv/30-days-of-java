package backend.transactional_outbox_pattern_task.producers;
import backend.transactional_outbox_pattern_task.entity.OutboxEventEntity;
import backend.transactional_outbox_pattern_task.enums.EventStatus;
import backend.transactional_outbox_pattern_task.repo.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final OutboxRepository outboxRepository;

    @Value("${outbox.rabbitmq.exchange.name:order_outbox_exchange}")
    private String exchange;

    @Value("${outbox.rabbitmq.routing.key:order_outbox_routing_key}")
    private String routingKey;


    @Transactional
    @Scheduled(fixedDelayString = "${outbox.scheduler.fixed-delay-ms:3000}")
    public void processOrderEvent() {
        log.info("Processing order events");
        List<OutboxEventEntity> pendingEvents = outboxRepository.findTop10ByStatusOrderByCreatedAtAsc(EventStatus.PENDING);
        if (pendingEvents.isEmpty()) {
            log.info("No pending events found");
            return;
        }
        for (OutboxEventEntity event : pendingEvents) {
            try {
                log.info("Sending event to RabbitMQ: {}", event.getPayload());
                rabbitTemplate.convertAndSend(exchange, routingKey, event.getPayload());
                event.setStatus(EventStatus.PROCESSED);
                log.info("Event sent to RabbitMQ: {}", event.getPayload());
                outboxRepository.save(event);
                log.info("Event saved to database: {}", event.getPayload());
            } catch (Exception e) {
                log.error("Error sending event to RabbitMQ: {}", e.getMessage());
                event.setStatus(EventStatus.FAILED);
                log.error("Event failed to send to RabbitMQ: {}", event.getPayload());
                outboxRepository.save(event);
            }
        }


    }

}
