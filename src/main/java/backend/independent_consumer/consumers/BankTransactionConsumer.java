package backend.independent_consumer.consumers;

import backend.independent_consumer.annotations.Idempotent;
import backend.independent_consumer.annotations.LogTimer;
import backend.independent_consumer.configs.RabbitMqConfig;
import backend.independent_consumer.dto.BankTransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BankTransactionConsumer {

    @RabbitListener(queues = RabbitMqConfig.MAIN_QUEUE)
    @Idempotent(key = "#event.transactionId",
            expireTime = 24,
            timeUnit = TimeUnit.HOURS,
            lockTimeoutSeconds = 60
    )
    @LogTimer
    public void processPayment(BankTransactionEvent event) {
        log.info("Executing transfer: txId={}, from={}, to={}, amount={} {}",
                event.getTransactionId(), event.getAccountFrom(), event.getAccountTo(),
                event.getAmount(), event.getCurrency());

        if ("TX-ERROR-999".equals(event.getTransactionId())) {
            throw new RuntimeException("Simulated core banking error for DLQ verification!");
        }
        log.info("Money successfully transferred for txId: {}", event.getTransactionId());
    }
}