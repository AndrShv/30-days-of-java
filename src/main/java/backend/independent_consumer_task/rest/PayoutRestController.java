package backend.independent_consumer_task.rest;


import backend.independent_consumer_task.producer.PayoutProducer;
import backend.independent_consumer_task.dto.PayoutRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/payout")
public class PayoutRestController {
    private final PayoutProducer payoutProducer;


    @PostMapping("/send")
    public void pay(@RequestBody PayoutRequestedEvent payoutRequestedEvent) {
        if (payoutRequestedEvent.getUserId() == null){
            log.warn("User id is null");
            throw new IllegalArgumentException("User id is null");
        }
        log.info("Payout requested event: {}", payoutRequestedEvent);
        payoutProducer.sendPayoutEvent(payoutRequestedEvent);

    }
}
