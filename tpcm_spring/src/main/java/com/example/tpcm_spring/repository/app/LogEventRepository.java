package com.example.tpcm_spring.repository.app;

import com.example.tpcm_spring.models.app.LogEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface LogEventRepository extends JpaRepository<LogEvent, Long> {
    List<LogEvent> findByEventTypeIgnoreCase(String eventType);

    List<LogEvent> findByLogTimestampBetween(Timestamp from, Timestamp to);

    List<LogEvent> findBySubscriber_SubscriberID(Long subscriberID);

    List<LogEvent> findByUser_UserId(Long userID);
}
