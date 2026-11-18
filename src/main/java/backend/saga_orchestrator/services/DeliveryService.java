package backend.saga_orchestrator.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeliveryService {

    public boolean reserveDelivery(String orderId) {
        log.info("🚚 [Delivery Service] Бронирование курьера для заказа {}", orderId);

        if (orderId.endsWith("-FAIL")) {
            log.error("💥 [Delivery Service] Нет свободных курьеров для заказа {}!", orderId);
            return false;
        }
        return true;
    }

    public void cancelDelivery(String orderId) {
        log.warn("🔄 [Delivery Service COMPENSATE] Отмена бронирования курьера для заказа {}", orderId);
    }
}