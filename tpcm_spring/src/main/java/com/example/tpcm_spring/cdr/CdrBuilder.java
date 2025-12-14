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

    public static String buildGetSubscriberBalanceSection(String msisdn, Long subscriberId,
                                                          Number availableBalance, Number maxCycle, Number consumedAmount) {
        return String.join(DELIMITER,
                nvl(msisdn),
                nvl(subscriberId),
                nvl(availableBalance),
                nvl(maxCycle),
                nvl(consumedAmount));
    }

    public static String buildCreateSubscriberSection(Long subscriberId, String msisdn, String status,
                                                      String subscriptionType, Long customerId) {
        return String.join(DELIMITER,
                nvl(subscriberId),
                nvl(msisdn),
                nvl(status),
                nvl(subscriptionType),
                nvl(customerId));
    }

    public static String buildUpdateSubscriberMsisdnSection(Long subscriberId, String oldMsisdn, String newMsisdn) {
        return String.join(DELIMITER,
                nvl(subscriberId),
                nvl(oldMsisdn),
                nvl(newMsisdn));
    }

    public static String buildUpdateSubscriberStatusSection(Long subscriberId, String oldStatus, String newStatus) {
        return String.join(DELIMITER,
                nvl(subscriberId),
                nvl(oldStatus),
                nvl(newStatus));
    }

    public static String buildUpdateSubscriberSubscriptionTypeSection(Long subscriberId, String oldType, String newType) {
        return String.join(DELIMITER,
                nvl(subscriberId),
                nvl(oldType),
                nvl(newType));
    }

    public static String buildUpdateSubscriberCustomerSection(Long subscriberId, Long oldCustomerId, Long newCustomerId) {
        return String.join(DELIMITER,
                nvl(subscriberId),
                nvl(oldCustomerId),
                nvl(newCustomerId));
    }

    public static String buildDeleteSubscriberSection(Long subscriberId, String msisdn, Long customerId) {
        return String.join(DELIMITER,
                nvl(subscriberId),
                nvl(msisdn),
                nvl(customerId));
    }

    public static String buildCreateCustomerSection(Long customerId, String name, String type, Integer billCycleDay,
                                                    String email, String contactNumber, String address) {
        return String.join(DELIMITER,
                nvl(customerId),
                nvl(name),
                nvl(type),
                nvl(billCycleDay),
                nvl(email),
                nvl(contactNumber),
                nvl(address));
    }

    public static String buildUpdateCustomerNameSection(Long customerId, String oldName, String newName) {
        return String.join(DELIMITER,
                nvl(customerId),
                nvl(oldName),
                nvl(newName));
    }

    public static String buildUpdateCustomerTypeSection(Long customerId, String oldType, String newType) {
        return String.join(DELIMITER,
                nvl(customerId),
                nvl(oldType),
                nvl(newType));
    }

    public static String buildUpdateCustomerBillCycleDaySection(Long customerId, Integer oldDay, Integer newDay) {
        return String.join(DELIMITER,
                nvl(customerId),
                nvl(oldDay),
                nvl(newDay));
    }

    public static String buildUpdateCustomerContactNumberSection(Long customerId, String oldNumber, String newNumber) {
        return String.join(DELIMITER,
                nvl(customerId),
                nvl(oldNumber),
                nvl(newNumber));
    }

    public static String buildUpdateCustomerEmailSection(Long customerId, String oldEmail, String newEmail) {
        return String.join(DELIMITER,
                nvl(customerId),
                nvl(oldEmail),
                nvl(newEmail));
    }

    public static String buildUpdateCustomerAddressSection(Long customerId, String oldAddress, String newAddress) {
        return String.join(DELIMITER,
                nvl(customerId),
                nvl(oldAddress),
                nvl(newAddress));
    }

    public static String buildDeleteCustomerSection(Long customerId, String name) {
        return String.join(DELIMITER,
                nvl(customerId),
                nvl(name));
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
