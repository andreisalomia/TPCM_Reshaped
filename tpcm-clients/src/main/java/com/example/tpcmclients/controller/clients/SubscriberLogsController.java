package com.example.tpcm-clients.controller.clients;

import com.example.tpcm_spring.models.clients.SubscriberLogs;
import com.example.tpcm_spring.service.clients.SubscriberLogsServiceClients;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("clients.SubscriberLogsController")
@RequestMapping("/api/clients/subscriber-logs")
@RequiredArgsConstructor
//@Hidden
public class SubscriberLogsController {

    private final SubscriberLogsServiceClients subscriberLogsService;

    @PostMapping
    public ResponseEntity<SubscriberLogs> createLog(@RequestBody SubscriberLogs log) {
        return ResponseEntity.ok(subscriberLogsService.createLog(log));
    }

    @GetMapping
    public ResponseEntity<List<SubscriberLogs>> getAllLogs() {
        return ResponseEntity.ok(subscriberLogsService.getAllLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriberLogs> getLogById(@PathVariable Long id) {
        return subscriberLogsService.getLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        subscriberLogsService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/by-operation")
    public ResponseEntity<List<SubscriberLogs>> getLogsByOperation(@RequestParam String operation) {
        return ResponseEntity.ok(subscriberLogsService.getByOperation(operation));
    }

    @GetMapping("/search/by-date-range")
    public ResponseEntity<List<SubscriberLogs>> getLogsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(subscriberLogsService.getByTimeRange(start, end));
    }

    @GetMapping("/search/by-subscriber")
    public ResponseEntity<List<SubscriberLogs>> getLogsBySubscriber(@RequestParam Long subscriberId) {
        return ResponseEntity.ok(subscriberLogsService.getBySubscriber(subscriberId));
    }

    @GetMapping("/search/by-user")
    public ResponseEntity<List<SubscriberLogs>> getLogsByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(subscriberLogsService.getByUser(userId));
    }
}
