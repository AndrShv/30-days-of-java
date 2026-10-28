package backend.liquid_ci_cd_example.interfaces;

import backend.liquid_ci_cd_example.dto.PaymentDto;
import backend.liquid_ci_cd_example.entity.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
    Payment createPayment(PaymentDto paymentDto);
    Optional<Payment> getPaymentById(UUID paymentId);
    void deletePayment(UUID paymentId);
}
