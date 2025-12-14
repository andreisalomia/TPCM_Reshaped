package com.example.tpcm_spring.cdr;

public interface CdrLogger {
    void logCdr(
            CdrOperation operation,
            String requestId,
            int httpStatus,
            CdrInternalResult internalResult,
            String operationSpecificSection
    );
}
