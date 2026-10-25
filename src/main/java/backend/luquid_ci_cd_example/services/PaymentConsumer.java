package backend.luquid_ci_cd_example.services;


import backend.luquid_ci_cd_example.events.PaymentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${outbox.rabbitmq.queue.name:payout_outbox_queue}")
    public void consumePayoutEvent(String messagePayload) {
        try {
            PaymentEvent event = objectMapper.readValue(messagePayload, PaymentEvent.class);
            log.info("Received Outbox Event from RabbitMQ: payoutId={}, userId={}, amount={}",
                    event.getPaymentId(), event.getUserId(), event.getAmount());

        } catch (Exception e) {
            log.error("Failed to process Outbox Event message: {}", messagePayload, e);
            throw new RuntimeException(e);
        }
    }
}
