package com.example.tpcm_spring.service.app;

import com.example.tpcm_spring.exceptions.NotFoundException;
import com.example.tpcm_spring.exceptions.ValidationException;
import com.example.tpcm_spring.models.app.Customer;
import com.example.tpcm_spring.repository.app.CustomerRepositoryApp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceApp {

    private final CustomerRepositoryApp customerRepository;

    private static final List<String> VALID_TYPES = Arrays.asList("Individual", "SME", "Large Enterprise");

    private void validateCustomer(Customer customer) {
        if(customer.getName() == null || customer.getName().isBlank()) {
            log.warn("Customer name must not be blank");
            throw new ValidationException("Customer name must not be blank");
        }

        if (!customer.getName().matches("^[A-Za-z\\s\\-']+$")) {
            log.warn("Customer name contains invalid characters");
            throw new ValidationException("Customer name must contain only letters, spaces, apostrophes and hyphens");
        }

        if (customer.getType() == null || !VALID_TYPES.contains(customer.getType())) {
            log.warn("Customer type must be one of: {}", VALID_TYPES);
            throw new ValidationException("Customer type must be one of: " + VALID_TYPES);
        }

        if (customer.getBillCycleDay() != null &&
                (customer.getBillCycleDay() < 1 || customer.getBillCycleDay() > 31)) {
            log.warn("Bill cycle day must be between 1 and 31");
            throw new ValidationException("Bill cycle day must be between 1 and 31");
        }
    }

    public Customer getCustomerById(Long id) {
        log.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with ID: " + id));
    }

    public List<Customer> getAllCustomers() {
        log.info("Fetching all customers from the app service");
        return customerRepository.findAll();
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer updated) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with ID: " + id));

        validateCustomer(updated);

        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setNrSubscribers(updated.getNrSubscribers());
        existing.setBillCycleDay(updated.getBillCycleDay());
        existing.setEmail(updated.getEmail());

        log.info("Updating customer with ID: {}", id);
        return customerRepository.save(existing);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            log.warn("Attempt to delete non-existing customer with ID: {}", id);
            throw new NotFoundException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
        log.info("Deleted customer with ID: {}", id);
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        validateCustomer(customer);
        log.info("Creating new customer with name: {}", customer.getName());
        return customerRepository.save(customer);
    }

    public List<Customer> findByNameIgnoreCase(String name) {
        if (name == null || name.isBlank()) {
            log.warn("Name must not be blank");
            throw new ValidationException("Name must not be blank");
        }
        log.info("Finding customers with name: {}", name);
        return customerRepository.findByNameIgnoreCase(name);
    }

    public List<Customer> findByTypeIgnoreCase(String type) {
        if (type == null || type.isBlank()) {
            log.warn("Type must not be blank");
            throw new ValidationException("Type must not be blank");
        }
        if (!VALID_TYPES.contains(type)) {
            log.warn("Type must be one of: {}", VALID_TYPES);
            throw new ValidationException("Type must be one of: " + VALID_TYPES);
        }
        log.info("Finding customers with type: {}", type);
        return customerRepository.findByTypeIgnoreCase(type);
    }

    public List<Customer> findByBillCycleDay(Integer billCycleDay) {
        if (billCycleDay == null || billCycleDay < 1 || billCycleDay > 31) {
            log.warn("Bill cycle day must be between 1 and 31");
            throw new ValidationException("Bill cycle day must be between 1 and 31");
        }
        log.info("Finding customers with bill cycle day: {}", billCycleDay);
        return customerRepository.findByBillCycleDay(billCycleDay);
    }
}
