package com.example.tpcm-clients.repository.clients;

import com.example.tpcm_spring.models.clients.CustomerLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerLogsRepository extends JpaRepository<CustomerLogs, Long> {
    List<CustomerLogs> findByOperationIgnoreCase(String operation);
    List<CustomerLogs> findByTimeOfChangeBetween(LocalDateTime start, LocalDateTime end);
    List<CustomerLogs> findByCustomer_CustomerID(Long customerID);
    List<CustomerLogs> findByUser_UserID(Long userID);
}
