package com.example.tpcm_spring.repository.clients;

import com.example.tpcm_spring.models.clients.SubscriberLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubscriberLogsRepository extends JpaRepository<SubscriberLogs, Long> {
    List<SubscriberLogs> findByOperationIgnoreCase(String operation);
    List<SubscriberLogs> findByTimeOfChangeBetween(LocalDateTime start, LocalDateTime end);
    List<SubscriberLogs> findBySubscriber_SubscriberID(Long subscriberId);
    List<SubscriberLogs> findByUser_UserID(Long userId);
}
