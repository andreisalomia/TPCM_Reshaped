package com.example.tpcm_clients.cdr;

public class CdrBuilder {

    private static final String DELIMITER = "|";
    private static final String EMPTY = "";

    public static String buildCreateSubscriberSection(Long subscriberId, String msisdn, String status,
                                                      String subscriptionType, Long customerId) {
        return String.join(DELIMITER,
                nvl(subscriberId), nvl(msisdn), nvl(status),
                nvl(subscriptionType), nvl(customerId));
    }

    public static String buildUpdateSubscriberMsisdnSection(Long subscriberId, String oldMsisdn, String newMsisdn) {
        return String.join(DELIMITER, nvl(subscriberId), nvl(oldMsisdn), nvl(newMsisdn));
    }

    public static String buildUpdateSubscriberStatusSection(Long subscriberId, String oldStatus, String newStatus) {
        return String.join(DELIMITER, nvl(subscriberId), nvl(oldStatus), nvl(newStatus));
    }

    public static String buildUpdateSubscriberSubscriptionTypeSection(Long subscriberId, String oldType, String newType) {
        return String.join(DELIMITER, nvl(subscriberId), nvl(oldType), nvl(newType));
    }

    public static String buildUpdateSubscriberCustomerSection(Long subscriberId, Long oldCustomerId, Long newCustomerId) {
        return String.join(DELIMITER, nvl(subscriberId), nvl(oldCustomerId), nvl(newCustomerId));
    }

    public static String buildDeleteSubscriberSection(Long subscriberId, String msisdn, Long customerId) {
        return String.join(DELIMITER, nvl(subscriberId), nvl(msisdn), nvl(customerId));
    }

    public static String buildCreateCustomerSection(Long customerId, String name, String type, Integer billCycleDay,
                                                    String email, String contactNumber, String address) {
        return String.join(DELIMITER,
                nvl(customerId), nvl(name), nvl(type), nvl(billCycleDay),
                nvl(email), nvl(contactNumber), nvl(address));
    }

    public static String buildUpdateCustomerNameSection(Long customerId, String oldName, String newName) {
        return String.join(DELIMITER, nvl(customerId), nvl(oldName), nvl(newName));
    }

    public static String buildUpdateCustomerTypeSection(Long customerId, String oldType, String newType) {
        return String.join(DELIMITER, nvl(customerId), nvl(oldType), nvl(newType));
    }

    public static String buildUpdateCustomerBillCycleDaySection(Long customerId, Integer oldDay, Integer newDay) {
        return String.join(DELIMITER, nvl(customerId), nvl(oldDay), nvl(newDay));
    }

    public static String buildUpdateCustomerContactNumberSection(Long customerId, String oldNumber, String newNumber) {
        return String.join(DELIMITER, nvl(customerId), nvl(oldNumber), nvl(newNumber));
    }

    public static String buildUpdateCustomerEmailSection(Long customerId, String oldEmail, String newEmail) {
        return String.join(DELIMITER, nvl(customerId), nvl(oldEmail), nvl(newEmail));
    }

    public static String buildUpdateCustomerAddressSection(Long customerId, String oldAddress, String newAddress) {
        return String.join(DELIMITER, nvl(customerId), nvl(oldAddress), nvl(newAddress));
    }

    public static String buildDeleteCustomerSection(Long customerId, String name) {
        return String.join(DELIMITER, nvl(customerId), nvl(name));
    }

    private static String nvl(Object value) {
        return value != null ? value.toString() : EMPTY;
    }
}