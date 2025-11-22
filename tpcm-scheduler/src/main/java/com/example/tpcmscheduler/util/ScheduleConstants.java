package com.example.tpcmscheduler.util;

public class ScheduleConstants {

    public static final String RESET_THRESHOLD_CRON = "0 0 0 * * *";

    public static final String DELETE_OLD_TRANSACTIONS_CRON = "0 0 20 * * *";

    public static final String DELETE_UNCOMMITTED_TRANSACTIONS_CRON = "0 0 22 * * *";

    public static final String DELETE_OLD_SUBSCRIBER_LOGS_CRON = "0 0 3 * * FRI";

    public static final String DELETE_OLD_CUSTOMER_LOGS_CRON = "0 0 3 * * SAT";

    public static final String DELETE_OLD_APP_EVENTS_CRON = "0 0 3 * * SUN";

    public static final String PARTITION_TRANSACTIONS_CRON = "0 0 23 L * *";

//    test for every 30 seconds
    public static final String EVERY_30_SECONDS_CRON = "*/30 * * * * *";
}