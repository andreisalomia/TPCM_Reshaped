package com.example.tpcm_spring.controller.app;

import com.example.tpcm_spring.models.app.Customer;
import com.example.tpcm_spring.models.app.Subscriber;
import com.example.tpcm_spring.service.app.CustomerServiceApp;
import com.example.tpcm_spring.service.app.SubscriberServiceApp;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final CustomerServiceApp customerService;
    private final SubscriberServiceApp subscriberService;

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
}