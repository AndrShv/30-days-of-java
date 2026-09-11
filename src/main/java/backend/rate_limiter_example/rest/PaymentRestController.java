package backend.rate_limiter_example.rest;

import backend.rate_limiter_example.annotations.RateLimit;
import backend.rate_limiter_example.responses.ExternalApiResponse;
import backend.rate_limiter_example.services.ExternalApiClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentRestController {

    private final ExternalApiClientService externalApiClientService;

    @PostMapping("/process")
    @RateLimit(limit = 5, windowInSeconds = 1)
    public ResponseEntity<ExternalApiResponse> processPayment(
            @RequestParam String orderId,
            @RequestParam double amount) {

        ExternalApiResponse response = externalApiClientService.executePayment(orderId, amount);
        return ResponseEntity.ok(response);
    }
}
