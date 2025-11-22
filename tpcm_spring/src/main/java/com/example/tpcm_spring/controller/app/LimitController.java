package com.example.tpcm_spring.controller.app;

import com.example.tpcm_spring.models.app.Limit;
import com.example.tpcm_spring.service.app.LimitServiceApp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("app.LimitController")
@RequestMapping("/api/app/limits")
@RequiredArgsConstructor
public class LimitController {

    private final LimitServiceApp limitService;

    @PostMapping
    public ResponseEntity<Limit> createLimit(@RequestBody Limit limit) {
        Limit saved = limitService.createLimit(limit);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<Limit>> getAllLimits() {
        return ResponseEntity.ok(limitService.getAllLimits());
    }

    @GetMapping("/{subscriberId}")
    public ResponseEntity<Limit> getLimitBySubscriberId(@PathVariable Long subscriberId) {
        Limit limit = limitService.getLimitBySubscriberId(subscriberId);
        return (limit != null) ? ResponseEntity.ok(limit) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{subscriberId}")
    public ResponseEntity<Limit> updateLimit(@PathVariable Long subscriberId, @RequestBody Limit updated) {
        Limit saved = limitService.updateLimit(subscriberId, updated);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{subscriberId}")
    public ResponseEntity<Void> deleteLimit(@PathVariable Long subscriberId) {
        limitService.deleteLimit(subscriberId);
        return ResponseEntity.noContent().build();
    }
}
