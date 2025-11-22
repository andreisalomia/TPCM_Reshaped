package com.example.tpcm_spring.controller.app;

import com.example.tpcm_spring.models.app.LogEvent;
import com.example.tpcm_spring.service.app.LogEventServiceApp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController("app.LogEventController")
@RequestMapping("/api/app/log-events")
@RequiredArgsConstructor
public class LogEventController {

    private final LogEventServiceApp logEventService;

    @PostMapping
    public ResponseEntity<LogEvent> createLogEvent(@RequestBody LogEvent logEvent) {
        return ResponseEntity.ok(logEventService.createLogEvent(logEvent));
    }

    @GetMapping
    public ResponseEntity<List<LogEvent>> getAllLogEvents() {
        return ResponseEntity.ok(logEventService.getAllLogEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogEvent> getLogEventById(@PathVariable Long id) {
        return logEventService.getLogEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LogEvent> updateLogEvent(@PathVariable Long id, @RequestBody LogEvent updated) {
        return logEventService.updateLogEvent(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogEvent(@PathVariable Long id) {
        return logEventService.deleteLogEvent(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/search/by-type")
    public ResponseEntity<List<LogEvent>> getByType(@RequestParam String type) {
        return ResponseEntity.ok(logEventService.findByEventType(type));
    }

    @GetMapping("/search/by-timestamp-range")
    public ResponseEntity<List<LogEvent>> getByTimestampRange(@RequestParam Timestamp from, @RequestParam Timestamp to) {
        return ResponseEntity.ok(logEventService.findByTimestampRange(from, to));
    }

    @GetMapping("/search/by-subscriber-id")
    public ResponseEntity<List<LogEvent>> getBySubscriberId(@RequestParam Long subscriberID) {
        return ResponseEntity.ok(logEventService.findBySubscriberId(subscriberID));
    }

    @GetMapping("/search/by-user-id")
    public ResponseEntity<List<LogEvent>> getByUserId(@RequestParam Long userID) {
        return ResponseEntity.ok(logEventService.findByUserId(userID));
    }
}
