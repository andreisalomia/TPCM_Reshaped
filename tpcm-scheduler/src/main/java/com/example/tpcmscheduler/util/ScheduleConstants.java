package com.example.tpcmscheduler.util;

public class ScheduleConstants {

    // Daily at midnight (12:00 AM)
    public static final String RESET_THRESHOLD_CRON = "0 0 0 * * *";

    // Daily at 8:00 PM
    public static final String DELETE_OLD_TRANSACTIONS_CRON = "0 0 20 * * *";

    // Daily at 10:00 PM
    public static final String DELETE_UNCOMMITTED_TRANSACTIONS_CRON = "0 0 22 * * *";

    // Every Friday at 3:00 AM
    public static final String DELETE_OLD_SUBSCRIBER_LOGS_CRON = "0 0 3 * * FRI";

    // Every Saturday at 3:00 AM
    public static final String DELETE_OLD_CUSTOMER_LOGS_CRON = "0 0 3 * * SAT";

    // Every Sunday at 3:00 AM
    public static final String DELETE_OLD_APP_EVENTS_CRON = "0 0 3 * * SUN";

    // Last day of every month at 11:00 PM
    public static final String PARTITION_TRANSACTIONS_CRON = "0 0 23 L * *";
}