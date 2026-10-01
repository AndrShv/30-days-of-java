package backend.independent_consumer.rest;

import backend.independent_consumer.dto.BankTransactionEvent;
import backend.independent_consumer.producers.BankTransactionProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bank/transactions")
@RequiredArgsConstructor
public class BankTransactionTestController {

    private final BankTransactionProducer producer;


    @PostMapping("/send")
    public ResponseEntity<String> sendTransaction(@RequestParam BigDecimal amount) {
        String transactionId = "TX-" + UUID.randomUUID().toString().substring(0, 8);
        BankTransactionEvent event = new BankTransactionEvent(
                transactionId,
                "ACC-UA-1001",
                "ACC-UA-2002",
                amount,
                "USD"
        );

        producer.sendTransactionEvent(event);
        return ResponseEntity.ok("Transaction initiated. transactionId: " + transactionId);
    }

    @PostMapping("/send-duplicate")
    public ResponseEntity<String> sendDuplicateTransaction(@RequestParam String transactionId) {
        BankTransactionEvent event = new BankTransactionEvent(
                transactionId,
                "ACC-UA-1001",
                "ACC-UA-2002",
                new BigDecimal("250.00"),
                "USD"
        );

        producer.sendTransactionEvent(event);
        return ResponseEntity.ok("Duplicate event sent with fixed transactionId: " + transactionId);
    }

    @PostMapping("/send-error")
    public ResponseEntity<String> sendErrorTransaction() {
        String transactionId = "TX-ERROR-999";
        BankTransactionEvent event = new BankTransactionEvent(
                transactionId,
                "ACC-UA-1001",
                "ACC-UA-2002",
                BigDecimal.ZERO,
                "USD"
        );

        producer.sendTransactionEvent(event);
        return ResponseEntity.ok("Faulty transaction sent. transactionId: " + transactionId);
    }
}