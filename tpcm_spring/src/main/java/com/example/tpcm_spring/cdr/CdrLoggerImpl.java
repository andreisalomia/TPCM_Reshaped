package com.example.tpcm_spring.cdr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@Slf4j
public class CdrLoggerImpl implements CdrLogger {

    @Override
    public void logCdr(
            CdrOperation operation,
            String requestId,
            int httpStatus,
            CdrInternalResult internalResult,
            String operationSpecificSection
    ) {
        String cdrLine = String.format("%s|%s|%s|%d|%d|%s",
                new Timestamp(System.currentTimeMillis()),
                operation.getName(),
                requestId != null ? requestId : "UNKNOWN",
                httpStatus,
                internalResult.getCode(),
                operationSpecificSection != null ? operationSpecificSection : ""
        );

        log.info(cdrLine);
    }
}