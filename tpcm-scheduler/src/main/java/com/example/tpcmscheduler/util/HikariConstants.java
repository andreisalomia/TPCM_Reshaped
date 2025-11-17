package com.example.tpcmscheduler.util;

public class HikariConstants {
    public static final String DRIVER_CLASS_NAME = "oracle.jdbc.OracleDriver";
    public static final int MINIMUM_IDLE = 5;
    public static final int MAXIMUM_POOL_SIZE = 20;
    public static final String CONNECTION_TEST_QUERY = "SELECT 1 FROM DUAL";
}