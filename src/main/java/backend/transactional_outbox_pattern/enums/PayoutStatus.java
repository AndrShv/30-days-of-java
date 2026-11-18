package backend.transactional_outbox_pattern.enums;

public enum PayoutStatus {
    CREATED,
    PROCESSING,
    COMPLETED,
    FAILED
}
