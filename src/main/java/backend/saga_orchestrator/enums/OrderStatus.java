package backend.saga_orchestrator.enums;

public enum OrderStatus {
    STARTED,
    ORDER_CREATED,
    PAYMENT_PROCESSED,
    DELIVERY_RESERVED, // Успешное завершение всей Саги (COMPLETED)
    CANCELING_PAYMENT, // Компенсация оплаты
    CANCELING_ORDER,    // Компенсация заказа
    CANCELING_DELIVERY, // Компенсация доставки
    COMPENSATED,       // Успешный откат саги
    FAILED
}
