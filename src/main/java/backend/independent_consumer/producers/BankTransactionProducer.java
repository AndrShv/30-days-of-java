package backend.independent_consumer.producers;

import backend.independent_consumer.configs.RabbitMqConfig;
import backend.independent_consumer.dto.BankTransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankTransactionProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTransactionEvent(BankTransactionEvent event) {
        log.info("Publishing transaction event to RabbitMQ. transactionId={}", event.getTransactionId());

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.MAIN_EXCHANGE,
                RabbitMqConfig.ROUTING_KEY,
                event
        );
    }
}