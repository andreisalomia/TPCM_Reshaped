package com.example.tpcm_spring.cdr;

public enum CdrOperation {
    RESERVE("reserve"),
    COMMIT("commit"),
    CANCEL("cancel"),
    GET_TRANSACTION_DETAILS("getTransactionDetails"),
    GET_TRANSACTIONS_FOR_MSISDN("getTransactionsForMsisdn"),
    GET_SUBSCRIBER_AVAILABLE_BALANCE("getSubscriberAvailableBalance"),
    CREATE_SUBSCRIBER("createSubscriber"),
    UPDATE_SUBSCRIBER_MSISDN("updateSubscriberMsisdn"),
    UPDATE_SUBSCRIBER_STATUS("updateSubscriberStatus"),
    UPDATE_SUBSCRIBER_SUBSCRIPTION_TYPE("updateSubscriberSubscriptionType"),
    UPDATE_SUBSCRIBER_CUSTOMER("updateSubscriberCustomer"),
    DELETE_SUBSCRIBER("deleteSubscriber"),
    CREATE_CUSTOMER("createCustomer"),
    UPDATE_CUSTOMER_NAME("updateCustomerName"),
    UPDATE_CUSTOMER_TYPE("updateCustomerType"),
    UPDATE_CUSTOMER_BILL_CYCLE_DAY("updateCustomerBillCycleDay"),
    UPDATE_CUSTOMER_CONTACT_NUMBER("updateCustomerContactNumber"),
    UPDATE_CUSTOMER_EMAIL("updateCustomerEmail"),
    UPDATE_CUSTOMER_ADDRESS("updateCustomerAddress"),
    DELETE_CUSTOMER("deleteCustomer");

    private final String name;
    CdrOperation(String name) { this.name = name; }
    public String getName() { return name; }
}
