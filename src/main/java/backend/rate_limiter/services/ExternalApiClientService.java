package backend.rate_limiter.services;

import backend.rate_limiter.responses.ExternalApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class ExternalApiClientService {

    private final Random random = new Random();

    @Retry(name = "externalPaymentApi", fallbackMethod = "fallbackPayment")
    @CircuitBreaker(name = "externalPaymentApi", fallbackMethod = "fallbackPayment")
    public ExternalApiResponse executePayment(String orderId, double amount) {
        log.info("Attempting to call External Payment API for order: {}", orderId);

        simulateApiBehavior();

        log.info("External Payment API call SUCCESSFUL for order: {}", orderId);
        return new ExternalApiResponse("SUCCESS", "TX-" + System.currentTimeMillis(), "Payment processed successfully");
    }

    public ExternalApiResponse fallbackPayment(String orderId, double amount, Throwable throwable) {
        return new ExternalApiResponse("FAILED", null, "Service unavailable: " + throwable.getMessage());
    }

    private void simulateApiBehavior() {
        int chance = random.nextInt(100);
        if (chance < 40) {
            try {
                log.warn("External API is lagging...");
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (chance >= 40 && chance < 70) {
            log.error("External API threw 500 Internal Server Error");
            throw new RuntimeException("500 Internal Server Error from External API");
        }
    }


}
