package com.example.tpcm_spring.controller.app;

import com.example.tpcm_spring.models.app.Customer;
import com.example.tpcm_spring.models.app.Subscriber;
import com.example.tpcm_spring.service.app.CustomerServiceApp;
import com.example.tpcm_spring.service.app.SubscriberServiceApp;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/app/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final CustomerServiceApp customerService;
    private final SubscriberServiceApp subscriberService;

    private static final String CDR_LOG_PATH = "logs/cdr.log";
    private static final int MAX_LOG_LINES = 1000;

    @GetMapping("/search-by-name")
    @Operation(summary = "Search subscribers by customer name to get MSISDN")
    public ResponseEntity<Map<String, Object>> searchByName(@RequestParam String name) {
        try {
            List<Customer> customers = customerService.getAllCustomers().stream()
                    .filter(customer -> customer.getName() != null && 
                            customer.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
            
            if (customers.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("searchedName", name);
                result.put("found", false);
                result.put("message", "No customer found with name: " + name);
                return ResponseEntity.ok(result);
            }

            List<Map<String, Object>> foundSubscribers = new ArrayList<>();
            
            for (Customer customer : customers) {
                List<Subscriber> subscribers = subscriberService.getAllSubscribers().stream()
                        .filter(sub -> sub.getCustomer() != null && 
                                sub.getCustomer().getCustomerID().equals(customer.getCustomerID()))
                        .toList();
                
                for (Subscriber subscriber : subscribers) {
                    Map<String, Object> subInfo = new HashMap<>();
                    subInfo.put("msisdn", subscriber.getMsisdn());
                    subInfo.put("customerName", customer.getName());
                    subInfo.put("status", subscriber.getStatus());
                    subInfo.put("customerId", customer.getCustomerID());
                    foundSubscribers.add(subInfo);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("searchedName", name);
            result.put("found", !foundSubscribers.isEmpty());
            result.put("count", foundSubscribers.size());
            result.put("subscribers", foundSubscribers);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/logs/{operation}/{identifier}")
    @Operation(summary = "Get CDR log entries for a specific operation and identifier")
    public ResponseEntity<List<String>> getLogsByOperation(
            @PathVariable String operation,
            @PathVariable String identifier) {

        try {
            Path logPath = Paths.get(CDR_LOG_PATH);
            
            if (!Files.exists(logPath)) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            try (Stream<String> lines = Files.lines(logPath)) {
                List<String> results = lines
                    .filter(line -> matchesOperation(line, operation, identifier))
                    .limit(MAX_LOG_LINES)
                    .collect(Collectors.toList());
                
                return ResponseEntity.ok(results);
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean matchesOperation(String line, String operation, String identifier) {
        if (line == null || line.trim().isEmpty()) {
            return false;
        }

        String[] parts = line.split("\\|");
        if (parts.length < 6) {
            return false;
        }

        if (!parts[1].equals(operation)) {
            return false;
        }

        for (int i = 5; i < parts.length; i++) {
            if (parts[i].contains(identifier)) {
                return true;
            }
        }

        return false;
    }
}