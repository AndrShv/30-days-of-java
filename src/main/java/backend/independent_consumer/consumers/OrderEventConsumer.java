package backend.independent_consumer.consumers;

import backend.independent_consumer.annotations.Idempotent;
import backend.independent_consumer.annotations.LogTimer;
import backend.independent_consumer.configs.RabbitMqConfig;
import backend.independent_consumer.dto.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventConsumer {

    @RabbitListener(queues = RabbitMqConfig.MAIN_QUEUE)
    @Idempotent(key = "#event.messageId", expireTime = 2, timeUnit = java.util.concurrent.TimeUnit.HOURS)
    @LogTimer
    public void processOrderCreated(OrderCreatedEvent event) {
        log.info("Processing order creation: orderId={}, amount={}", event.getOrderId(), event.getAmount());
        if ("ERROR_ORDER".equals(event.getOrderId())) {
            throw new RuntimeException("Simulated processing error for DLQ testing!");
        }
        log.info("Order successfully processed for orderId: {}", event.getOrderId());
    }
}
