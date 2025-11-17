package com.example.tpcmscheduler.service;

import com.example.tpcmscheduler.util.ScheduleConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseScheduler {

    @Qualifier("appJdbc")
    private final JdbcTemplate appJdbc;

    @Qualifier("clientsJdbc")
    private final JdbcTemplate clientsJdbc;

    @Scheduled(cron = ScheduleConstants.RESET_THRESHOLD_CRON)
    public void resetThreshold() {
        try {
            log.info("[{}] Starting resetThreshold procedure", LocalDateTime.now());
            appJdbc.update("BEGIN resetThreshold(); END;");
            log.info("[{}] resetThreshold completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing resetThreshold", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = ScheduleConstants.DELETE_UNCOMMITTED_TRANSACTIONS_CRON)
    public void deleteUncommittedTransactions() {
        try {
            log.info("[{}] Starting deleteUncommittedTransactions procedure", LocalDateTime.now());
            appJdbc.update("DECLARE v_count NUMBER; BEGIN v_count := deleteUncommittedTransactions(); END;");
            log.info("[{}] deleteUncommittedTransactions completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing deleteUncommittedTransactions", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = ScheduleConstants.DELETE_OLD_TRANSACTIONS_CRON)
    public void deleteOldTransactions() {
        try {
            log.info("[{}] Starting deleteOldTransactions procedure", LocalDateTime.now());
            appJdbc.update("DECLARE v_count NUMBER; BEGIN v_count := deleteOldTransactions(); END;");
            log.info("[{}] deleteOldTransactions completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing deleteOldTransactions", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = ScheduleConstants.DELETE_OLD_APP_EVENTS_CRON)
    public void deleteOldAppEvents() {
        try {
            log.info("[{}] Starting deleteOldAppEvents procedure", LocalDateTime.now());
            appJdbc.update("DECLARE v_count NUMBER; BEGIN v_count := deleteOldAppEvents(); END;");
            log.info("[{}] deleteOldAppEvents completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing deleteOldAppEvents", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = ScheduleConstants.PARTITION_TRANSACTIONS_CRON)
    public void partitionTransactions() {
        try {
            log.info("[{}] Starting partitionTransactions procedure", LocalDateTime.now());
            appJdbc.update("BEGIN partitionTransactions(); END;");
            log.info("[{}] partitionTransactions completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing partitionTransactions", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = ScheduleConstants.DELETE_OLD_SUBSCRIBER_LOGS_CRON)
    public void deleteOldSubscriberLogs() {
        try {
            log.info("[{}] Starting deleteOldSubscriberLogs procedure", LocalDateTime.now());
            clientsJdbc.update("DECLARE v_count NUMBER; BEGIN v_count := deleteOldSubscriberLogs(); END;");
            log.info("[{}] deleteOldSubscriberLogs completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing deleteOldSubscriberLogs", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = ScheduleConstants.DELETE_OLD_CUSTOMER_LOGS_CRON)
    public void deleteOldCustomerLogs() {
        try {
            log.info("[{}] Starting deleteOldCustomerLogs procedure", LocalDateTime.now());
            clientsJdbc.update("DECLARE v_count NUMBER; BEGIN v_count := deleteOldCustomerLogs(); END;");
            log.info("[{}] deleteOldCustomerLogs completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing deleteOldCustomerLogs", LocalDateTime.now(), e);
        }
    }
}