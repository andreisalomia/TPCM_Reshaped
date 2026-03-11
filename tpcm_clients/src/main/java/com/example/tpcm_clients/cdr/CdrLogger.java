package com.example.tpcm_clients.cdr;

public interface CdrLogger {
    void logCdr(
            CdrOperation operation,
            String requestId,
            int httpStatus,
            CdrInternalResult internalResult,
            String operationSpecificSection
    );
}
