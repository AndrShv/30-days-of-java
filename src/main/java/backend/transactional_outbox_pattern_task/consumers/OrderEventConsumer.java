package backend.transactional_outbox_pattern_task.consumers;
import backend.transactional_outbox_pattern_task.events.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Slf4j
public class OrderEventConsumer {
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "order-events")
    public void consumeOrderEvent(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            log.info("Received Outbox Event from RabbitMQ: orderId={}, userId={}, totalAmount={}",
                    event.getOrderId(), event.getUserId(), event.getTotalAmount());

        } catch (Exception e) {
            log.error("Failed to process Outbox Event message: {}", message, e);
            throw new RuntimeException(e);
        }
    }

}
