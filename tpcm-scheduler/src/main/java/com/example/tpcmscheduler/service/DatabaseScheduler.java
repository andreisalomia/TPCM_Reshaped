package com.example.tpcmscheduler.service;

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

    @Scheduled(cron = "${schedule.cron.reset-threshold}")
    public void resetThreshold() {
        try {
            log.info("[{}] Starting resetThreshold procedure", LocalDateTime.now());
            appJdbc.update("BEGIN resetThreshold(); END;");
            log.info("[{}] resetThreshold completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing resetThreshold", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = "${schedule.cron.delete-uncommitted-transactions}")
    public void deleteUncommittedTransactions() {
        try {
            log.info("[{}] Starting deleteUncommittedTransactions procedure", LocalDateTime.now());
            appJdbc.update("DECLARE v_count NUMBER; BEGIN v_count := deleteUncommittedTransactions(); END;");
            log.info("[{}] deleteUncommittedTransactions completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing deleteUncommittedTransactions", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = "${schedule.cron.delete-old-transactions}")
    public void deleteOldTransactions() {
        try {
            log.info("[{}] Starting deleteOldTransactions procedure", LocalDateTime.now());
            appJdbc.update("DECLARE v_count NUMBER; BEGIN v_count := deleteOldTransactions(); END;");
            log.info("[{}] deleteOldTransactions completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing deleteOldTransactions", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = "${schedule.cron.delete-old-app-events}")
    public void deleteOldAppEvents() {
        try {
            log.info("[{}] Starting deleteOldAppEvents procedure", LocalDateTime.now());
            appJdbc.update("DECLARE v_count NUMBER; BEGIN v_count := deleteOldAppEvents(); END;");
            log.info("[{}] deleteOldAppEvents completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing deleteOldAppEvents", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = "${schedule.cron.partition-transactions}")
    public void partitionTransactions() {
        try {
            log.info("[{}] Starting partitionTransactions procedure", LocalDateTime.now());
            appJdbc.update("BEGIN partitionTransactions(); END;");
            log.info("[{}] partitionTransactions completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing partitionTransactions", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = "${schedule.cron.delete-old-subscriber-logs}")
    public void deleteOldSubscriberLogs() {
        try {
            log.info("[{}] Starting deleteOldSubscriberLogs procedure", LocalDateTime.now());
            clientsJdbc.update("DECLARE v_count NUMBER; BEGIN v_count := deleteOldSubscriberLogs(); END;");
            log.info("[{}] deleteOldSubscriberLogs completed successfully", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[{}] Error executing deleteOldSubscriberLogs", LocalDateTime.now(), e);
        }
    }

    @Scheduled(cron = "${schedule.cron.delete-old-customer-logs}")
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