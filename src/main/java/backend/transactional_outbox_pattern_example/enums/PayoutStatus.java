package backend.transactional_outbox_pattern_example.enums;

public enum PayoutStatus {
    CREATED,
    PROCESSING,
    COMPLETED,
    FAILED
}
