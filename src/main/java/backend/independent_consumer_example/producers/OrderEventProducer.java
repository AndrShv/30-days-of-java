package backend.independent_consumer_example.producers;

import backend.independent_consumer_example.configs.RabbitMqConfig;
import backend.independent_consumer_example.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderEvent(OrderCreatedEvent event) {
        log.info("Sending order event to RabbitMQ. messageId={}, orderId={}",
                event.getMessageId(), event.getOrderId());

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.MAIN_EXCHANGE,
                RabbitMqConfig.ROUTING_KEY,
                event
        );
    }
}
