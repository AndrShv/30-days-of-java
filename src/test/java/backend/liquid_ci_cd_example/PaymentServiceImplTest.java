package backend.liquid_ci_cd_example;

import backend.liquid_ci_cd_example.dto.PaymentDto;
import backend.liquid_ci_cd_example.entity.OutboxEventEntity;
import backend.liquid_ci_cd_example.entity.Payment;
import backend.liquid_ci_cd_example.enums.PaymentStatus;
import backend.liquid_ci_cd_example.ex.PaymentNotFoundException;
import backend.liquid_ci_cd_example.repos.OutboxRepository;
import backend.liquid_ci_cd_example.repos.PaymentRepo;
import backend.liquid_ci_cd_example.services.PaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void createPayment_Success() {
        UUID userId = UUID.randomUUID();
        PaymentDto dto = PaymentDto.builder()
                .userId(userId)
                .amount(BigDecimal.ZERO)
                .status(PaymentStatus.PROCESSING)
                .createdAt(LocalDateTime.now())
                .build();
        when(paymentRepo.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            if (payment.getId() == null) {
                payment.setId(UUID.randomUUID());
            }
            return payment;
        });

        Payment result = paymentService.createPayment(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(BigDecimal.ZERO, result.getAmount());

        verify(paymentRepo, times(1)).save(any(Payment.class));
        ArgumentCaptor<OutboxEventEntity> outboxCaptor = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(outboxRepository, times(1)).save(outboxCaptor.capture());

        OutboxEventEntity savedOutbox = outboxCaptor.getValue();
        assertEquals("PAYOUT", savedOutbox.getAggregateType());
        assertEquals("PAYOUT_CREATED", savedOutbox.getEventType());
    }
    @Test
    void createPayment_ThrowsException_WhenAmountNotZero() {
        PaymentDto dto = PaymentDto.builder()
                .userId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100.00))
                .status(PaymentStatus.PROCESSING)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(dto);
        });

        verify(paymentRepo, never()).save(any());
        verify(outboxRepository, never()).save(any());
    }

    @Test
    void getPaymentById_Success() {
        UUID paymentId = UUID.randomUUID();
        Payment mockPayment = Payment.builder()
                .id(paymentId)
                .userId(UUID.randomUUID())
                .amount(BigDecimal.ZERO)
                .status(PaymentStatus.PROCESSING)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentRepo.findById(paymentId)).thenReturn(Optional.of(mockPayment));

        Optional<Payment> result = paymentService.getPaymentById(paymentId);

        assertTrue(result.isPresent());
        assertEquals(paymentId, result.get().getId());
        verify(paymentRepo, times(1)).findById(paymentId);
    }

    @Test
    void getPaymentById_ThrowsNotFoundException() {
        UUID paymentId = UUID.randomUUID();
        when(paymentRepo.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.getPaymentById(paymentId);
        });
    }

    @Test
    void deletePayment_Success() {
        UUID paymentId = UUID.randomUUID();
        Payment mockPayment = Payment.builder().id(paymentId).build();

        when(paymentRepo.findById(paymentId)).thenReturn(Optional.of(mockPayment));
        doNothing().when(paymentRepo).delete(mockPayment);

        paymentService.deletePayment(paymentId);

        verify(paymentRepo, times(1)).findById(paymentId);
        verify(paymentRepo, times(1)).delete(mockPayment);
    }
}