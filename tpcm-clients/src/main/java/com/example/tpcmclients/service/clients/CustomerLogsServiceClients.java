package com.example.tpcm-clients.service.clients;

import com.example.tpcm_spring.models.clients.CustomerLogs;
import com.example.tpcm_spring.repository.clients.CustomerLogsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerLogsServiceClients {

    private final CustomerLogsRepository customerLogsRepository;

    public CustomerLogs createLog(CustomerLogs logC) {
        if (logC.getOperation() == null || logC.getOperation().isEmpty()) {
            log.warn("Attempted to create a log with null or empty operation");
            return null;
        }
        if (logC.getChangedField() == null || logC.getChangedField().isEmpty()) {
            log.warn("Attempted to create a log with null or empty changed field");
            return null;
        }
        if (logC.getTimeOfChange() == null) {
            log.warn("Attempted to create a log with null time of change");
            return null;
        }
        return customerLogsRepository.save(logC);
    }

    public List<CustomerLogs> getAllLogs() {
        log.info("Retrieving all customer logs");
        return customerLogsRepository.findAll();
    }

    public Optional<CustomerLogs> getLogById(Long id) {
        log.info("Retrieving customer log with ID: {}", id);
        return customerLogsRepository.findById(id);
    }

    public boolean deleteLog(Long id) {
        return customerLogsRepository.findById(id)
                .map(logC -> {
                    customerLogsRepository.delete(logC);
                    log.info("Deleted customer log with ID: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public List<CustomerLogs> findByOperation(String operation) {
        log.info("Retrieving all customer logs");
        return customerLogsRepository.findByOperationIgnoreCase(operation);
    }

    public List<CustomerLogs> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        log.info("Retrieving customer logs between {} and {}", start, end);
        return customerLogsRepository.findByTimeOfChangeBetween(start, end);
    }

    public List<CustomerLogs> findByCustomer(Long customerId) {
        log.info("Retrieving customer logs for customer ID: {}", customerId);
        return customerLogsRepository.findByCustomer_CustomerID(customerId);
    }

    public List<CustomerLogs> findByUser(Long userId) {
        log.info("Retrieving customer logs for user ID: {}", userId);
        return customerLogsRepository.findByUser_UserID(userId);
    }
}
