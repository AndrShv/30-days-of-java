package backend.rate_limiter.responses;

public record ExternalApiResponse(String status, String transactionId, String message) {}
