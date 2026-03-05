package com.example.tpcm-clients.controller.clients;

import com.example.tpcm_spring.models.clients.CustomerLogs;
import com.example.tpcm_spring.service.clients.CustomerLogsServiceClients;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("clients.CustomerLogsController")
@RequestMapping("/api/clients/customer-logs")
@RequiredArgsConstructor
//@Hidden
public class CustomerLogsController {

    private final CustomerLogsServiceClients customerLogsService;

    @PostMapping
    public ResponseEntity<CustomerLogs> createLog(@RequestBody CustomerLogs log) {
        CustomerLogs saved = customerLogsService.createLog(log);
        return (saved != null) ? ResponseEntity.status(201).body(saved) : ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerLogs>> getLogs() {
        List<CustomerLogs> logs = customerLogsService.getAllLogs();
        return logs.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerLogs> getLogById(@PathVariable Long id) {
        return customerLogsService.getLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        return customerLogsService.deleteLog(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/search/by-operation")
    public ResponseEntity<List<CustomerLogs>> getLogsByOperation(@RequestParam String operation) {
        List<CustomerLogs> logs = customerLogsService.findByOperation(operation);
        return logs.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(logs);
    }

    @GetMapping("/search/by-date-range")
    public ResponseEntity<List<CustomerLogs>> getLogsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<CustomerLogs> logs = customerLogsService.findByTimeRange(start, end);
        return logs.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(logs);
    }

    @GetMapping("/search/by-customer")
    public ResponseEntity<List<CustomerLogs>> getLogsByCustomer(@RequestParam Long userId) {
        List<CustomerLogs> logs = customerLogsService.findByCustomer(userId);
        return logs.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(logs);
    }

    @GetMapping("/search/by-user")
    public ResponseEntity<List<CustomerLogs>> getLogsByUser(@RequestParam Long userId) {
        List<CustomerLogs> logs = customerLogsService.findByUser(userId);
        return logs.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(logs);
    }
}
