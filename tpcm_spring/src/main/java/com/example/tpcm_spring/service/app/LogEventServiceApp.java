package com.example.tpcm_spring.service.app;

import com.example.tpcm_spring.models.app.LogEvent;
import com.example.tpcm_spring.repository.app.LogEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogEventServiceApp {

    private final LogEventRepository logEventRepository;

    public LogEvent createLogEvent(LogEvent logEvent) {
        return logEventRepository.save(logEvent);
    }

    public List<LogEvent> getAllLogEvents() {
        return logEventRepository.findAll();
    }

    public Optional<LogEvent> getLogEventById(Long id) {
        return logEventRepository.findById(id);
    }

    public Optional<LogEvent> updateLogEvent(Long id, LogEvent updated) {
        return logEventRepository.findById(id).map(existing -> {
            existing.setEventType(updated.getEventType());
            existing.setLogTimestamp(updated.getLogTimestamp());
            existing.setSubscriber(updated.getSubscriber());
            existing.setUser(updated.getUser());
            existing.setTransaction(updated.getTransaction());
            existing.setDetailedEvent(updated.getDetailedEvent());
            return logEventRepository.save(existing);
        });
    }

    public boolean deleteLogEvent(Long id) {
        if (logEventRepository.existsById(id)) {
            logEventRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<LogEvent> findByEventType(String type) {
        return logEventRepository.findByEventTypeIgnoreCase(type);
    }

    public List<LogEvent> findByTimestampRange(Timestamp from, Timestamp to) {
        return logEventRepository.findByLogTimestampBetween(from, to);
    }

    public List<LogEvent> findBySubscriberId(Long subscriberID) {
        return logEventRepository.findBySubscriber_SubscriberID(subscriberID);
    }

    public List<LogEvent> findByUserId(Long userID) {
        return logEventRepository.findByUser_UserId(userID);
    }
}
