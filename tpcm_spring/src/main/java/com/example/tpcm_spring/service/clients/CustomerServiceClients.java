package com.example.tpcm_spring.service.clients;

import com.example.tpcm_spring.exceptions.NotFoundException;
import com.example.tpcm_spring.exceptions.ValidationException;
import com.example.tpcm_spring.kafka.producer.KafkaProducerService;
import com.example.tpcm_spring.models.clients.Customer;
import com.example.tpcm_spring.repository.clients.CustomerRepositoryClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceClients {

    private final CustomerRepositoryClients customerRepository;
    private final KafkaProducerService kafkaProducerService;

    private static final List<String> VALID_TYPES = Arrays.asList("Individual", "SME", "Large Enterprise");

    private void validateCustomerName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Customer name must not be blank");
        }
        if (!name.matches("^[A-Za-z\\s\\-']+$")) {
            throw new ValidationException("Customer name must contain only letters, spaces, apostrophes and hyphens");
        }
    }

    private void validateCustomerType(String type) {
        if (type == null || !VALID_TYPES.contains(type)) {
            throw new ValidationException("Customer type must be one of: " + VALID_TYPES);
        }
    }

    private void validateBillCycleDay(Integer billCycleDay) {
        if (billCycleDay != null && (billCycleDay < 1 || billCycleDay > 31)) {
            throw new ValidationException("Bill cycle day must be between 1 and 31");
        }
    }

    private void validateContactNumber(String contactNumber) {
        if (contactNumber != null && !contactNumber.matches("^\\+?[0-9]{10,15}$")) {
            throw new ValidationException("Contact number must be a valid phone number");
        }
    }

    private void validateEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ValidationException("Email must be a valid email address");
        }
    }

    private void validateAddress(String address) {
        if (address != null && address.length() > 255) {
            throw new ValidationException("Address must be at most 255 characters");
        }
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        validateCustomerName(customer.getName());
        validateCustomerType(customer.getType());
        validateBillCycleDay(customer.getBillCycleDay());
        validateContactNumber(customer.getContactNumber());
        validateEmail(customer.getEmail());
        validateAddress(customer.getAddress());

        log.info("Creating new customer with name: {}", customer.getName());
        Customer savedCustomer = customerRepository.save(customer);

        kafkaProducerService.publishCustomerCreated(savedCustomer);
        log.info("Customer created with ID: {}", savedCustomer.getCustomerID());
        return savedCustomer;
    }

    @Transactional
    public Optional<Customer> updateCustomerName(Long id, String newName) {
        validateCustomerName(newName);

        return customerRepository.findById(id).map(customer -> {
            String oldName = customer.getName();
            customer.setName(newName);
            Customer saved = customerRepository.save(customer);

            kafkaProducerService.publishCustomerNameUpdate(id, oldName, newName);
            log.info("Updated customer name for ID {}: {} -> {}", id, oldName, newName);
            return saved;
        });
    }

    @Transactional
    public Optional<Customer> updateCustomerType(Long id, String newType) {
        validateCustomerType(newType);

        return customerRepository.findById(id).map(customer -> {
            String oldType = customer.getType();
            customer.setType(newType);
            Customer saved = customerRepository.save(customer);

            kafkaProducerService.publishCustomerTypeUpdate(id, oldType, newType);
            log.info("Updated customer type for ID {}: {} -> {}", id, oldType, newType);
            return saved;
        });
    }

    @Transactional
    public Optional<Customer> updateBillCycleDay(Long id, Integer newBillCycleDay) {
        validateBillCycleDay(newBillCycleDay);

        return customerRepository.findById(id).map(customer -> {
            Integer oldBillCycleDay = customer.getBillCycleDay();
            customer.setBillCycleDay(newBillCycleDay);
            Customer saved = customerRepository.save(customer);

            kafkaProducerService.publishCustomerBillCycleDayUpdate(id, oldBillCycleDay, newBillCycleDay);
            log.info("Updated bill cycle day for customer ID {}: {} -> {}", id, oldBillCycleDay, newBillCycleDay);
            return saved;
        });
    }

    @Transactional
    public Optional<Customer> updateMsisdn(Long id, String newContactNumber) {
        validateContactNumber(newContactNumber);

        return customerRepository.findById(id).map(customer -> {
            String oldContactNumber = customer.getContactNumber();
            customer.setContactNumber(newContactNumber);
            Customer saved = customerRepository.save(customer);

            log.info("Updated contact number for customer ID {}: {} -> {}", id, oldContactNumber, newContactNumber);
            return saved;
        });
    }

    @Transactional
    public Optional<Customer> updateEmail(Long id, String newEmail) {
        validateEmail(newEmail);

        return customerRepository.findById(id).map(customer -> {
            String oldEmail = customer.getEmail();
            customer.setEmail(newEmail);
            Customer saved = customerRepository.save(customer);

            log.info("Updated email for customer ID {}: {} -> {}", id, oldEmail, newEmail);
            return saved;
        });
    }

    @Transactional
    public Optional<Customer> updateAddress(Long id, String newAddress) {
        validateAddress(newAddress);

        return customerRepository.findById(id).map(customer -> {
            String oldAddress = customer.getAddress();
            customer.setAddress(newAddress);
            Customer saved = customerRepository.save(customer);

            log.info("Updated address for customer ID {}: {} -> {}", id, oldAddress, newAddress);
            return saved;
        });
    }

    public List<Customer> getAllCustomers() {
        log.info("Retrieving all customers from clients service");
        return customerRepository.findAll();
    }

    public Optional<Customer> getById(Long id) {
        log.info("Retrieving customer with ID: {}", id);
        return Optional.ofNullable(customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer with ID " + id + " not found")));
    }

    @Transactional
    public boolean deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            log.warn("Attempt to delete non-existing customer with ID: {}", id);
            return false;
        }
        customerRepository.deleteById(id);
        kafkaProducerService.publishCustomerDeleted(id);
        log.info("Deleted customer with ID: {}", id);
        return true;
    }

    public List<Customer> findByName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Name must not be blank");
        }
        log.info("Finding customers with name: {}", name);
        return customerRepository.findByNameIgnoreCase(name);
    }

    public List<Customer> findByType(String type) {
        if (type == null || type.isBlank()) {
            throw new ValidationException("Type must not be blank");
        }
        if (!VALID_TYPES.contains(type)) {
            throw new ValidationException("Type must be one of: " + VALID_TYPES);
        }
        log.info("Finding customers with type: {}", type);
        return customerRepository.findByTypeIgnoreCase(type);
    }

    public List<Customer> findByBillCycleDay(Integer billCycleDay) {
        if (billCycleDay == null) {
            throw new ValidationException("Bill cycle day must not be null");
        }
        if (billCycleDay < 1 || billCycleDay > 31) {
            throw new ValidationException("Bill cycle day must be between 1 and 31");
        }
        log.info("Finding customers with bill cycle day: {}", billCycleDay);
        return customerRepository.findByBillCycleDay(billCycleDay);
    }
}