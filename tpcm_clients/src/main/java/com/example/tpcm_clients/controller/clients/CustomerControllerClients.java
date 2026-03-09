package com.example.tpcm_clients.controller.clients;

import com.example.tpcm_clients.models.clients.Customer;
import com.example.tpcm_clients.service.clients.CustomerServiceClients;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("clients.CustomerPatchController")
@RequestMapping("/api/clients/customers")
@RequiredArgsConstructor
public class CustomerControllerClients {

    private final CustomerServiceClients customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomerResponse(@RequestBody Customer customer) {
        Customer saved = customerService.createCustomer(customer);
        return (saved != null) ? ResponseEntity.status(201).body(saved) : ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return customers.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/name")
    @Operation(summary = "Update customer name")
    public ResponseEntity<Customer> updateCustomerName(
            @PathVariable Long id,
            @RequestBody @Parameter(description = "New customer name") String name) {
        return customerService.updateCustomerName(id, name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/type")
    @Operation(summary = "Update customer type")
    public ResponseEntity<Customer> updateCustomerType(
            @PathVariable Long id,
            @RequestBody @Parameter(description = "New customer type") String type) {
        return customerService.updateCustomerType(id, type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/bill-cycle-day")
    @Operation(summary = "Update customer bill cycle day")
    public ResponseEntity<Customer> updateBillCycleDay(
            @PathVariable Long id,
            @RequestBody @Parameter(description = "New bill cycle day") Integer billCycleDay) {
        return customerService.updateBillCycleDay(id, billCycleDay)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/msisdn")
    @Operation(summary = "Update customer contact number (MSISDN)")
    public ResponseEntity<Customer> updateContactNumber(
            @PathVariable Long id,
            @RequestBody @Parameter(description = "New contact number (MSISDN)") String contactNumber) {
        return customerService.updateMsisdn(id, contactNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/email")
    @Operation(summary = "Update customer email")
    public ResponseEntity<Customer> updateEmail(
            @PathVariable Long id,
            @RequestBody @Parameter(description = "New email address") String email) {
        return customerService.updateEmail(id, email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/address")
    @Operation(summary = "Update customer address")
    public ResponseEntity<Customer> updateAddress(
            @PathVariable Long id,
            @RequestBody @Parameter(description = "New address") String address) {
        return customerService.updateAddress(id, address)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        return customerService.deleteCustomer(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/search/by-name")
    public ResponseEntity<List<Customer>> getCustomersByName(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) return ResponseEntity.badRequest().build();
        List<Customer> list = customerService.findByName(name);
        return list.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/search/by-type")
    public ResponseEntity<List<Customer>> getCustomersByType(@RequestParam String type) {
        if (type == null || type.trim().isEmpty()) return ResponseEntity.badRequest().build();
        List<Customer> list = customerService.findByType(type);
        return list.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/search/by-bill-cycle-day")
    public ResponseEntity<List<Customer>> getCustomersByBillCycleDay(@RequestParam Integer billCycleDay) {
        if (billCycleDay == null || billCycleDay < 1 || billCycleDay > 31) return ResponseEntity.badRequest().build();
        List<Customer> list = customerService.findByBillCycleDay(billCycleDay);
        return list.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(list);
    }
}