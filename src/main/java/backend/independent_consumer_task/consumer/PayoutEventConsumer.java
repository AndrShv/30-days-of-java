package backend.independent_consumer_task.consumer;


import backend.independent_consumer_task.annotations.IdempotentPayout;
import backend.independent_consumer_task.annotations.LogExecuteTimer;
import backend.independent_consumer_task.configs.PayoutRabbitConfig;
import backend.independent_consumer_task.dto.PayoutRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutEventConsumer {




    @IdempotentPayout(key = "#payoutRequestedEvent.payoutId", timeUnit = TimeUnit.MINUTES)
    @LogExecuteTimer
    @RabbitListener(queues = PayoutRabbitConfig.PAYOUT_QUEUE)
    public void consumePayoutEvent(PayoutRequestedEvent payoutRequestedEvent) {
        log.info("Received payout event with payoutId: {} and userId: {}",
                payoutRequestedEvent.getPayoutId(), payoutRequestedEvent.getUserId());
        int eventAmount = payoutRequestedEvent.getAmount().intValue();
        if (eventAmount <=  0 )
            throw new IllegalArgumentException("Payout amount must be greater than zero");

        log.info("Payout amount: {}", eventAmount);
    }
}
