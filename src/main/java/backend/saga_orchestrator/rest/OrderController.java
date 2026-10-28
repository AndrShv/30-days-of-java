package backend.saga_orchestrator.rest;


import backend.saga_orchestrator.services.OrderSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderSagaOrchestrator orchestrator;

    @PostMapping("/success")
    public ResponseEntity<String> createSuccessfulOrder() {
        String orderId = "ORD-1001";
        String sagaId = orchestrator.executeOrderSaga(orderId, "CUST-55", new BigDecimal("1200.00"));
        return ResponseEntity.ok("Saga completed successfully. Saga ID: " + sagaId);
    }

    @PostMapping("/fail")
    public ResponseEntity<String> createFailedOrder() {
        String orderId = "ORD-1002-FAIL";
        String sagaId = orchestrator.executeOrderSaga(orderId, "CUST-55", new BigDecimal("2500.00"));
        return ResponseEntity.ok("Saga failed and compensated. Saga ID: " + sagaId);
    }
}
