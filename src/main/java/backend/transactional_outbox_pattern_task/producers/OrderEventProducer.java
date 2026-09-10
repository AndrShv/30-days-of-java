package backend.transactional_outbox_pattern_task.producers;
import backend.transactional_outbox_pattern_task.entity.OutboxEventEntity;
import backend.transactional_outbox_pattern_task.enums.EventStatus;
import backend.transactional_outbox_pattern_task.repo.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);
    private final RabbitTemplate rabbitTemplate;
    private final OutboxRepository outboxRepository;

    @Value("${outbox.rabbitmq.exchange.name:order_outbox_exchange}")
    private String exchange;

    @Value("${outbox.rabbitmq.routing.key:order_outbox_routing_key}")
    private String routingKey;


    @Transactional
    @Scheduled(fixedDelayString = "${outbox.scheduler.fixed-delay-ms:3000}")
    public void processOrderEvent() {
        List<OutboxEventEntity> pendingEvents = outboxRepository.findTop10ByStatusOrderByCreatedAtAsc(EventStatus.PENDING);
        if (pendingEvents.isEmpty()) {
            return;
        }
        for (OutboxEventEntity event : pendingEvents) {
            try {
                rabbitTemplate.convertAndSend(exchange, routingKey, event);
                event.setStatus(EventStatus.PROCESSED);
                outboxRepository.save(event);
            } catch (Exception e) {
                log.error("Error sending event to RabbitMQ: {}", e.getMessage());
                event.setStatus(EventStatus.FAILED);
                outboxRepository.save(event);
            }
        }


    }

}
