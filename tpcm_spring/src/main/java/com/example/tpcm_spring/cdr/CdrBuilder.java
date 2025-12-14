package com.example.tpcm_spring.cdr;

import com.example.tpcm_spring.models.app.Transaction;

import java.sql.Timestamp;

public class CdrBuilder {

    private static final String DELIMITER = "|";
    private static final String EMPTY = "";

    public static String buildReserveSection(
            String msisdn,
            Long thirdPartyId,
            Double amount,
            Long transactionId,
            Double remainingBalance
    ) {
        return String.join(DELIMITER,
                nvl(msisdn),
                nvl(thirdPartyId),
                nvl(amount),
                nvl(transactionId),
                nvl(remainingBalance)
        );
    }

    public static String buildCommitSection(Long transactionId, Double commitAmount) {
        return String.join(DELIMITER,
                nvl(transactionId),
                nvl(commitAmount)
        );
    }

    public static String buildCancelSection(Long transactionId) {
        return nvl(transactionId);
    }

 public static String buildGetTransactionDetailsSection(Transaction transaction) {
        if (transaction == null) {
            return EMPTY;
        }

        Long remainingDuration;
        if ("PENDING".equals(transaction.getStatus())) {
            remainingDuration = calculateRemainingDuration(transaction.getCreatedDate());
        } else {
            remainingDuration = 0L;
        }
        
        return String.join(DELIMITER,
                nvl(transaction.getTransactionId()),
                nvl(transaction.getStatus()),
                nvl(transaction.getMsisdn()),
                String.valueOf(remainingDuration),
                nvl(transaction.getAmount())
        );
    }

    public static String buildGetTransactionsForMsisdnSection(String msisdn) {
        return nvl(msisdn);
    }

    private static Long calculateRemainingDuration(Timestamp createdDate) {
        if (createdDate == null) {
            return 0L;
        }
        
        long createdMillis = createdDate.getTime();
        long currentMillis = System.currentTimeMillis();
        long expirationMillis = createdMillis + (24 * 60 * 60 * 1000L);
        
        long remainingMillis = expirationMillis - currentMillis;
        
        if (remainingMillis < 0) {
            return 0L;
        }
        
        return remainingMillis / 1000;
    }

    private static String nvl(Object value) {
        return value != null ? value.toString() : EMPTY;
    }
}