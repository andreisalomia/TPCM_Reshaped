package com.example.tpcm_spring.service.clients;

import com.example.tpcm_spring.models.clients.SubscriberLogs;
import com.example.tpcm_spring.repository.clients.SubscriberLogsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberLogsServiceClients {

    private final SubscriberLogsRepository subscriberLogsRepository;

    public SubscriberLogs createLog(SubscriberLogs logS) {
        log.info("Creating new subscriber log for subscriber ID: {}", logS.getSubscriber().getSubscriberID());
        return subscriberLogsRepository.save(logS);
    }

    public List<SubscriberLogs> getAllLogs() {
        log.info("Retrieving all subscriber logs");
        return subscriberLogsRepository.findAll();
    }

    public Optional<SubscriberLogs> getLogById(Long id) {
        log.info("Retrieving subscriber log with ID: {}", id);
        return subscriberLogsRepository.findById(id);
    }

    public void deleteLog(Long id) {
        log.warn("Deleting subscriber log with ID: {}", id);
        subscriberLogsRepository.deleteById(id);
    }

    public List<SubscriberLogs> getByOperation(String operation) {
        log.info("Finding subscriber logs with operation: {}", operation);
        return subscriberLogsRepository.findByOperationIgnoreCase(operation);
    }

    public List<SubscriberLogs> getByTimeRange(LocalDateTime start, LocalDateTime end) {
        log.info("Finding subscriber logs between {} and {}", start, end);
        return subscriberLogsRepository.findByTimeOfChangeBetween(start, end);
    }

    public List<SubscriberLogs> getBySubscriber(Long subscriberId) {
        log.info("Finding subscriber logs for subscriber ID: {}", subscriberId);
        return subscriberLogsRepository.findBySubscriber_SubscriberID(subscriberId);
    }

    public List<SubscriberLogs> getByUser(Long userId) {
        log.info("Finding subscriber logs for user ID: {}", userId);
        return subscriberLogsRepository.findByUser_UserID(userId);
    }
}
