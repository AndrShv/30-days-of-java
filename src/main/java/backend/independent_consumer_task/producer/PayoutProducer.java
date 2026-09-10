package backend.independent_consumer_task.producer;


import backend.independent_consumer_task.configs.PayoutRabbitConfig;
import backend.independent_consumer_task.dto.PayoutRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayoutProducer {
    private final RabbitTemplate rabbitTemplate;


    public void sendPayoutEvent(PayoutRequestedEvent payoutRequestedEvent){
        log.info("Sending order event to RabbitMQ. messageId={}, orderId={}",
                payoutRequestedEvent.getPayoutId(), payoutRequestedEvent.getUserId());

        rabbitTemplate.convertAndSend(
                PayoutRabbitConfig.PAYOUT_EXCHANGE,
                PayoutRabbitConfig.PAYOUT_ROUTING_KEY,
                payoutRequestedEvent
        );

    }
}
