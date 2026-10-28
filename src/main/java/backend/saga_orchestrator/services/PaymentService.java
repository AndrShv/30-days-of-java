package backend.saga_orchestrator.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PaymentService {

    public boolean processPayment(String orderId, BigDecimal amount) {
        log.info("💳 [Payment Service] Списание средств {} UAH по заказу {}", amount, orderId);
        return true;
    }

    public void refundPayment(String orderId, BigDecimal amount) {
        log.warn("🔄 [Payment Service COMPENSATE] Возврат средств {} UAH по заказу {}", amount, orderId);
    }
}