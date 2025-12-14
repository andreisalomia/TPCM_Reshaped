package com.example.tpcm_spring.cdr;

public enum CdrOperation {
    RESERVE("reserve"),
    COMMIT("commit"),
    CANCEL("cancel"),
    GET_TRANSACTION_DETAILS("getTransactionDetails"),
    GET_TRANSACTIONS_FOR_MSISDN("getTransactionsForMsisdn");

    private final String name;
    CdrOperation(String name) { this.name = name; }
    public String getName() { return name; }
}
