package com.example.tpcm_spring.controller.app;

import com.example.tpcm_spring.models.app.Subscriber;
import com.example.tpcm_spring.service.app.SubscriberServiceApp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("app.SubscriberController")
@RequestMapping("/api/app/subscribers")
@RequiredArgsConstructor
public class SubscriberControllerApp {

    private final SubscriberServiceApp subscriberService;

    @PostMapping
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        Subscriber saved = subscriberService.createSubscriber(subscriber);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Subscriber>> getAllSubscribers() {
        return ResponseEntity.ok(subscriberService.getAllSubscribers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscriber> getSubscriberById(@PathVariable Long id) {
        Subscriber subscriber = subscriberService.getSubscriberById(id);
        return ResponseEntity.ok(subscriber);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subscriber> updateSubscriber(@PathVariable Long id, @RequestBody Subscriber updated) {
        Subscriber subscriber = subscriberService.updateSubscriber(id, updated);
        return ResponseEntity.ok(subscriber);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable Long id) {
        subscriberService.deleteSubscriber(id);  // aruncă NotFoundException dacă nu există
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/by-msisdn")
    public ResponseEntity<List<Subscriber>> getSubscribersByMsisdn(@RequestParam String msisdn) {
        return ResponseEntity.ok(subscriberService.findByMsisdn(msisdn));
    }

    @GetMapping("/search/by-subscription-type")
    public ResponseEntity<List<Subscriber>> getSubscribersBySubscriptionType(@RequestParam String subscriptionType) {
        return ResponseEntity.ok(subscriberService.findBySubscriptionType(subscriptionType));
    }
}
