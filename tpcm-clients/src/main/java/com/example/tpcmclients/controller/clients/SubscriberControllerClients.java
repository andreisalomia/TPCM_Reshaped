package com.example.tpcm-clients.controller.clients;

import com.example.tpcm_spring.models.clients.Subscriber;
import com.example.tpcm_spring.service.clients.SubscriberServiceClients;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("clients.SubscriberPatchController")
@RequestMapping("/api/clients/subscribers")
@RequiredArgsConstructor
public class SubscriberControllerClients {

    private final SubscriberServiceClients subscriberService;

    @PostMapping
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        Subscriber saved = subscriberService.createSubscriber(subscriber);
        return (saved != null) ? ResponseEntity.status(201).body(saved) : ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<Subscriber>> getSubscribers() {
        List<Subscriber> list = subscriberService.getAllSubscribers();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscriber> getSubscriberById(@PathVariable Long id) {
        return subscriberService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/msisdn")
    @Operation(summary = "Update subscriber MSISDN")
    public ResponseEntity<Subscriber> updateMsisdn(
            @PathVariable Long id,
            @RequestBody @Parameter(description = "New MSISDN") String msisdn) {
        return subscriberService.updateMsisdn(id, msisdn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update subscriber status")
    public ResponseEntity<Subscriber> updateStatus(
            @PathVariable Long id,
            @RequestBody @Parameter(description = "New status (ACTIVE, INACTIVE, SUSPENDED)") String status) {
        return subscriberService.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/subscription-type")
    @Operation(summary = "Update subscriber subscription type")
    public ResponseEntity<Subscriber> updateSubscriptionType(
            @PathVariable Long id,
            @RequestBody @Parameter(description = "New subscription type (PREPAID, POSTPAID, HYBRID)") String subscriptionType) {
        return subscriberService.updateSubscriptionType(id, subscriptionType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/customer")
    @Operation(summary = "Move subscriber to different customer")
    public ResponseEntity<Subscriber> updateCustomer(
            @PathVariable Long id,
            @RequestBody @Parameter(description = "New customer ID") Long customerId) {
        return subscriberService.updateCustomer(id, customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable Long id) {
        return subscriberService.deleteSubscriber(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/search/by-msisdn")
    public ResponseEntity<List<Subscriber>> getSubscribersByMsisdn(@RequestParam String msisdn) {
        List<Subscriber> list = subscriberService.findByMsisdn(msisdn);
        return list.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/search/by-subscription-type")
    public ResponseEntity<List<Subscriber>> getSubscribersBySubscriptionType(@RequestParam String subscriptionType) {
        List<Subscriber> list = subscriberService.findBySubscriptionType(subscriptionType);
        return list.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(list);
    }
}