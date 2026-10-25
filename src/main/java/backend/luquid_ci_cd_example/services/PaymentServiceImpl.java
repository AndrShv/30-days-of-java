package backend.luquid_ci_cd_example.services;


import backend.luquid_ci_cd_example.dto.PaymentDto;
import backend.luquid_ci_cd_example.entity.Payment;
import backend.luquid_ci_cd_example.events.PaymentEvent;
import backend.luquid_ci_cd_example.ex.PaymentNotFoundException;
import backend.luquid_ci_cd_example.interfaces.PaymentService;
import backend.luquid_ci_cd_example.repos.PaymentRepo;
import backend.luquid_ci_cd_example.entity.OutboxEventEntity;
import backend.luquid_ci_cd_example.enums.OutboxStatus;
import backend.luquid_ci_cd_example.repos.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepo paymentRepo;
    private final OutboxRepository outboxRepository;

    @Override
    public Payment createPayment(PaymentDto paymentDto) {
        log.info("Creating Payment for payment {}", paymentDto);
        if (paymentDto.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            log.warn("Payment amount must be positive");
            throw  new IllegalArgumentException("Payment amount must be positive");
        }


            Payment payment = Payment.builder()
                    .userId(paymentDto.getUserId())
                    .amount(paymentDto.getAmount())
                    .status(paymentDto.getStatus())
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentRepo.save(payment);

            PaymentEvent event = new PaymentEvent(
                    payment.getId(),
                    payment.getUserId(),
                    payment.getAmount()
            );

            String payloadJson;
            try {
                payloadJson = new ObjectMapper().writeValueAsString(event);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize payout event to JSON", e);
            }

            OutboxEventEntity outboxEvent = new OutboxEventEntity(
                    UUID.randomUUID(),
                    "PAYOUT",
                    payment.getId().toString(),
                    "PAYOUT_CREATED",
                    payloadJson,
                    OutboxStatus.PENDING,
                    LocalDateTime.now()
            );
            outboxRepository.save(outboxEvent);


        return payment;
    }

    @Override
    public Optional<Payment> getPaymentById(UUID paymentId) {
        log.info("Getting payment by id {}", paymentId);

        return Optional.of(paymentRepo.findById(paymentId).orElseThrow(() -> {
            log.warn("Payment not found with id {}", paymentId);
            return new PaymentNotFoundException("Payment not found with id " + paymentId);
        }));
    }

    @Override
    public void deletePayment(UUID paymentId) {
        log.info("Deleting Payment for payment {}", paymentId);


        Payment foundPayment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> {
                    log.warn("Payment not found for Payment {}", paymentId);
                    return new PaymentNotFoundException("Payment not found for Payment " + paymentId);
                });

        paymentRepo.delete(foundPayment);
        log.info("Successfully deleted Payment with id {}", paymentId);

    }
}
