package com.example.tpcm_spring.controller.app;

import com.example.tpcm_spring.models.app.Customer;
import com.example.tpcm_spring.service.app.CustomerServiceApp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("app.CustomerController")
@RequestMapping("/api/app/customers")
@RequiredArgsConstructor
public class CustomerControllerApp {

    private final CustomerServiceApp customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer saved = customerService.createCustomer(customer);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return customers.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer updated) {
        Customer result = customerService.updateCustomer(id, updated);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/by-name")
    public ResponseEntity<List<Customer>> getCustomersByName(@RequestParam String name) {
        List<Customer> customers = customerService.findByNameIgnoreCase(name);
        return customers.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(customers);
    }

    @GetMapping("/search/by-type")
    public ResponseEntity<List<Customer>> getCustomersByType(@RequestParam String type) {
        List<Customer> customers = customerService.findByTypeIgnoreCase(type);
        return customers.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(customers);
    }

    @GetMapping("/search/by-bill-cycle-day")
    public ResponseEntity<List<Customer>> getCustomersByBillCycleDay(@RequestParam Integer billCycleDay) {
        List<Customer> customers = customerService.findByBillCycleDay(billCycleDay);
        return customers.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(customers);
    }
}
