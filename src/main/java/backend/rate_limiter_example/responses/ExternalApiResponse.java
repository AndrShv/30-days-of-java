package backend.rate_limiter_example.responses;

public record ExternalApiResponse(String status, String transactionId, String message) {}
