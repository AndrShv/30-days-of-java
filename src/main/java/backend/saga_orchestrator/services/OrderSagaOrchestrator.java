package backend.saga_orchestrator.services;

import backend.saga_orchestrator.enums.OrderStatus;
import backend.saga_orchestrator.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import backend.saga_orchestrator.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderSagaOrchestrator {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;

    @Transactional
    public String executeOrderSaga(String orderId, String customerId, BigDecimal amount) {
        String sagaId = UUID.randomUUID().toString();

        Order saga = Order.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .customerId(customerId)
                .amount(amount)
                .status(OrderStatus.STARTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        orderRepository.save(saga);
        log.info("🚀 [Saga: {}] Старт Оркестратора для заказа {}", sagaId, orderId);

        try {
            // ШАГ 1: Создание/Резервирование заказа
            updateStatus(saga, OrderStatus.ORDER_CREATED);

            // ШАГ 2: Оплата
            log.info("▶️ [Saga: {}] Шаг 2: Обработка оплаты...", sagaId);
            boolean paymentSuccess = paymentService.processPayment(orderId, amount);

            if (!paymentSuccess) {
                throw new IllegalStateException("Payment failed");
            }
            updateStatus(saga, OrderStatus.PAYMENT_PROCESSED);

            // ШАГ 3: Доставка
            log.info("▶️ [Saga: {}] Шаг 3: Бронирование доставки...", sagaId);
            boolean deliverySuccess = deliveryService.reserveDelivery(orderId);

            if (!deliverySuccess) {
                throw new IllegalStateException("Delivery reservation failed: No available couriers");
            }
            updateStatus(saga, OrderStatus.DELIVERY_RESERVED);

            log.info("✅ [Saga: {}] Сага успешно завершена!", sagaId);

        } catch (Exception e) {
            log.error("❌ [Saga: {}] Ошибка на одном из этапов: {}. Запуск КОМПЕНСАЦИИ!", sagaId, e.getMessage());
            saga.setFailureReason(e.getMessage());

            rollbackSaga(saga);
        }

        return sagaId;
    }


    private void rollbackSaga(Order order) {
        OrderStatus failedAtState = order.getStatus();

        if (failedAtState == OrderStatus.DELIVERY_RESERVED) {
            // Упали ПОСЛЕ доставки -> Отменяем доставку, ПОТОМ оплату
            updateStatus(order, OrderStatus.CANCELING_DELIVERY);
            deliveryService.cancelDelivery(order.getOrderId());

            updateStatus(order, OrderStatus.CANCELING_PAYMENT);
            paymentService.refundPayment(order.getOrderId(), order.getAmount());

        } else if (failedAtState == OrderStatus.PAYMENT_PROCESSED) {
            // Упали ВО ВРЕМЯ доставки -> Доставку отменять не надо, отменяем ТОЛЬКО оплату
            updateStatus(order, OrderStatus.CANCELING_PAYMENT);
            paymentService.refundPayment(order.getOrderId(), order.getAmount());
        }

        // В любом случае отменяем заказ
        updateStatus(order, OrderStatus.CANCELING_ORDER);
        updateStatus(order, OrderStatus.COMPENSATED);
    }
    private void updateStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
}
