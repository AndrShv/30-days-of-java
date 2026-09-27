package backend.independent_consumer.rest;

import backend.independent_consumer.dto.OrderCreatedEvent;
import backend.independent_consumer.producers.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test/events")
@RequiredArgsConstructor
public class RestProducerController {

    private final OrderEventProducer producer;

    @PostMapping("/send")
    public ResponseEntity<String> sendEvent(@RequestParam String orderId, @RequestParam BigDecimal amount) {
        String messageId = UUID.randomUUID().toString();
        OrderCreatedEvent event = new OrderCreatedEvent(messageId, orderId, "user-777", amount);

        producer.sendOrderEvent(event);
        return ResponseEntity.ok("Event sent with messageId: " + messageId);
    }

    @PostMapping("/send-duplicate")
    public ResponseEntity<String> sendDuplicateEvent(@RequestParam String messageId, @RequestParam String orderId) {
        OrderCreatedEvent event = new OrderCreatedEvent(messageId, orderId, "user-777", BigDecimal.valueOf(500.00));

        producer.sendOrderEvent(event);
        return ResponseEntity.ok("Duplicate event sent with fixed messageId: " + messageId);
    }

    @PostMapping("/send-error")
    public ResponseEntity<String> sendErrorEvent() {
        String messageId = UUID.randomUUID().toString();
        OrderCreatedEvent event = new OrderCreatedEvent(messageId, "ERROR_ORDER", "user-777", BigDecimal.ZERO);

        producer.sendOrderEvent(event);
        return ResponseEntity.ok("Error event sent with messageId: " + messageId);
    }
}
