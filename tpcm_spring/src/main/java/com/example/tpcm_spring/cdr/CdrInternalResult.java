package com.example.tpcm_spring.cdr;

public enum CdrInternalResult {
    SUCCESS(0),
    SUBSCRIBER_NOT_FOUND(2),
    VALIDATION_FAILED(3),
    THRESHOLD_REACHED(4),
    TRANSACTION_LIMIT_REACHED(5),
    TRANSACTION_NOT_FOUND(6),
    TRANSACTION_EXPIRED(7),
    COMMIT_INVALID(8),
    TRANSACTION_ALREADY_COMMITTED(12),
    TRANSACTION_ALREADY_CANCELLED(13),
    LOCK_TIMEOUT(22),
    GENERIC_ERROR(100);

    private final int code;
    CdrInternalResult(int code) { this.code = code; }
    public int getCode() { return code; }
}
