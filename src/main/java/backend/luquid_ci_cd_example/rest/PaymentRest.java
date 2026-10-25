package backend.luquid_ci_cd_example.rest;


import backend.luquid_ci_cd_example.interfaces.PaymentService;
import backend.luquid_ci_cd_example.dto.PaymentDto;
import backend.luquid_ci_cd_example.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentRest {
    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentDto paymentDto) {
        log.info("Creating Payment for payment {}", paymentDto);
        Payment new_payment = paymentService.createPayment(paymentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new_payment);
    }

    @DeleteMapping("/cancel")
    public void cancelPayment(@RequestParam UUID paymentId) {
        log.info("Canceling Payment for payment {}", paymentId);
        paymentService.deletePayment(paymentId);
    }

   public ResponseEntity<Optional<Payment>> getPaymentById(@RequestParam UUID paymentId) {
        log.info("Retrieving Payment for payment {}", paymentId);
        Optional<Payment> dbPayment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.status(HttpStatus.FOUND).body(dbPayment);
   }
}
