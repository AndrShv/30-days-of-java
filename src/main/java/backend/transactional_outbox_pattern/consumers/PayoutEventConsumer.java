package backend.transactional_outbox_pattern.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import backend.transactional_outbox_pattern.dto.PayoutRequestedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutEventConsumer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${outbox.rabbitmq.queue.name:payout_outbox_queue}")
    public void consumePayoutEvent(String messagePayload) {
        try {
            PayoutRequestedEvent event = objectMapper.readValue(messagePayload, PayoutRequestedEvent.class);
            log.info("Received Outbox Event from RabbitMQ: payoutId={}, userId={}, amount={}",
                    event.getPayoutId(), event.getUserId(), event.getAmount());

        } catch (Exception e) {
            log.error("Failed to process Outbox Event message: {}", messagePayload, e);
            throw new RuntimeException(e);
        }
    }
}