package com.example.kafkaconsumer.util;

public class HikariConstants {
    public static final int MINIMUM_IDLE = 3;
    public static final int MAXIMUM_POOL_SIZE = 10;
    public static final String CONNECTION_TEST_QUERY = "SELECT 1 FROM DUAL";
    public static final String DRIVER_CLASS_NAME = "oracle.jdbc.OracleDriver";
}

