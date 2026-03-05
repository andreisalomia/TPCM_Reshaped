package com.example.tpcm-clients.service.clients;

import com.example.tpcm_spring.exceptions.NotFoundException;
import com.example.tpcm_spring.exceptions.ValidationException;
//import com.example.tpcm_spring.cdr.CdrBuilder;
//import com.example.tpcm_spring.cdr.CdrInternalResult;
//import com.example.tpcm_spring.cdr.CdrLogger;
//import com.example.tpcm_spring.cdr.CdrOperation;
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
//    private final CdrLogger cdrLogger;

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
        try {
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

//            cdrLogger.logCdr(CdrOperation.CREATE_CUSTOMER,
//                    savedCustomer.getCustomerID().toString(),
//                    201,
//                    CdrInternalResult.SUCCESS,
//                    CdrBuilder.buildCreateCustomerSection(savedCustomer.getCustomerID(), savedCustomer.getName(),
//                            savedCustomer.getType(), savedCustomer.getBillCycleDay(), savedCustomer.getEmail(),
//                            savedCustomer.getContactNumber(), savedCustomer.getAddress()));
            return savedCustomer;
        } catch (ValidationException ex) {
//            cdrLogger.logCdr(CdrOperation.CREATE_CUSTOMER,
//                    customer != null && customer.getCustomerID() != null ? customer.getCustomerID().toString() : "UNKNOWN",
//                    400,
//                    CdrInternalResult.VALIDATION_FAILED,
//                    CdrBuilder.buildCreateCustomerSection(
//                            customer != null ? customer.getCustomerID() : null,
//                            customer != null ? customer.getName() : null,
//                            customer != null ? customer.getType() : null,
//                            customer != null ? customer.getBillCycleDay() : null,
//                            customer != null ? customer.getEmail() : null,
//                            customer != null ? customer.getContactNumber() : null,
//                            customer != null ? customer.getAddress() : null));
            throw ex;
        } catch (RuntimeException ex) {
//            cdrLogger.logCdr(CdrOperation.CREATE_CUSTOMER,
//                    customer != null && customer.getCustomerID() != null ? customer.getCustomerID().toString() : "UNKNOWN",
//                    500,
//                    CdrInternalResult.GENERIC_ERROR,
//                    CdrBuilder.buildCreateCustomerSection(
//                            customer != null ? customer.getCustomerID() : null,
//                            customer != null ? customer.getName() : null,
//                            customer != null ? customer.getType() : null,
//                            customer != null ? customer.getBillCycleDay() : null,
//                            customer != null ? customer.getEmail() : null,
//                            customer != null ? customer.getContactNumber() : null,
//                            customer != null ? customer.getAddress() : null));
            throw ex;
        }
    }

    @Transactional
    public Optional<Customer> updateCustomerName(Long id, String newName) {
        try {
            validateCustomerName(newName);
        } catch (ValidationException ex) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_NAME,
//                    id != null ? id.toString() : "UNKNOWN",
//                    400,
//                    CdrInternalResult.VALIDATION_FAILED,
//                    CdrBuilder.buildUpdateCustomerNameSection(id, null, newName));
            throw ex;
        }

        Optional<Customer> optional = customerRepository.findById(id);
        if (optional.isEmpty()) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_NAME,
//                    id != null ? id.toString() : "UNKNOWN",
//                    404,
//                    CdrInternalResult.CUSTOMER_NOT_FOUND,
//                    CdrBuilder.buildUpdateCustomerNameSection(id, null, newName));
            return Optional.empty();
        }

        return optional.map(customer -> {
            String oldName = customer.getName();
            customer.setName(newName);
            Customer saved = customerRepository.save(customer);

            kafkaProducerService.publishCustomerNameUpdate(id, oldName, newName);
            log.info("Updated customer name for ID {}: {} -> {}", id, oldName, newName);

//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_NAME,
//                    id.toString(),
//                    200,
//                    CdrInternalResult.SUCCESS,
//                    CdrBuilder.buildUpdateCustomerNameSection(id, oldName, newName));
            return saved;
        });
    }

    @Transactional
    public Optional<Customer> updateCustomerType(Long id, String newType) {
        try {
            validateCustomerType(newType);
        } catch (ValidationException ex) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_TYPE,
//                    id != null ? id.toString() : "UNKNOWN",
//                    400,
//                    CdrInternalResult.VALIDATION_FAILED,
//                    CdrBuilder.buildUpdateCustomerTypeSection(id, null, newType));
            throw ex;
        }

        Optional<Customer> optional = customerRepository.findById(id);
        if (optional.isEmpty()) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_TYPE,
//                    id != null ? id.toString() : "UNKNOWN",
//                    404,
//                    CdrInternalResult.CUSTOMER_NOT_FOUND,
//                    CdrBuilder.buildUpdateCustomerTypeSection(id, null, newType));
            return Optional.empty();
        }

        return optional.map(customer -> {
            String oldType = customer.getType();
            customer.setType(newType);
            Customer saved = customerRepository.save(customer);

            kafkaProducerService.publishCustomerTypeUpdate(id, oldType, newType);
            log.info("Updated customer type for ID {}: {} -> {}", id, oldType, newType);

//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_TYPE,
//                    id.toString(),
//                    200,
//                    CdrInternalResult.SUCCESS,
//                    CdrBuilder.buildUpdateCustomerTypeSection(id, oldType, newType));
            return saved;
        });
    }

    @Transactional
    public Optional<Customer> updateBillCycleDay(Long id, Integer newBillCycleDay) {
        try {
            validateBillCycleDay(newBillCycleDay);
        } catch (ValidationException ex) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_BILL_CYCLE_DAY,
//                    id != null ? id.toString() : "UNKNOWN",
//                    400,
//                    CdrInternalResult.VALIDATION_FAILED,
//                    CdrBuilder.buildUpdateCustomerBillCycleDaySection(id, null, newBillCycleDay));
            throw ex;
        }

        Optional<Customer> optional = customerRepository.findById(id);
        if (optional.isEmpty()) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_BILL_CYCLE_DAY,
//                    id != null ? id.toString() : "UNKNOWN",
//                    404,
//                    CdrInternalResult.CUSTOMER_NOT_FOUND,
//                    CdrBuilder.buildUpdateCustomerBillCycleDaySection(id, null, newBillCycleDay));
            return Optional.empty();
        }

        return optional.map(customer -> {
            Integer oldBillCycleDay = customer.getBillCycleDay();
            customer.setBillCycleDay(newBillCycleDay);
            Customer saved = customerRepository.save(customer);

            kafkaProducerService.publishCustomerBillCycleDayUpdate(id, oldBillCycleDay, newBillCycleDay);
            log.info("Updated bill cycle day for customer ID {}: {} -> {}", id, oldBillCycleDay, newBillCycleDay);

//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_BILL_CYCLE_DAY,
//                    id.toString(),
//                    200,
//                    CdrInternalResult.SUCCESS,
//                    CdrBuilder.buildUpdateCustomerBillCycleDaySection(id, oldBillCycleDay, newBillCycleDay));
            return saved;
        });
    }

    @Transactional
    public Optional<Customer> updateMsisdn(Long id, String newContactNumber) {
        try {
            validateContactNumber(newContactNumber);
        } catch (ValidationException ex) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_CONTACT_NUMBER,
//                    id != null ? id.toString() : "UNKNOWN",
//                    400,
//                    CdrInternalResult.VALIDATION_FAILED,
//                    CdrBuilder.buildUpdateCustomerContactNumberSection(id, null, newContactNumber));
            throw ex;
        }

        Optional<Customer> optional = customerRepository.findById(id);
        if (optional.isEmpty()) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_CONTACT_NUMBER,
//                    id != null ? id.toString() : "UNKNOWN",
//                    404,
//                    CdrInternalResult.CUSTOMER_NOT_FOUND,
//                    CdrBuilder.buildUpdateCustomerContactNumberSection(id, null, newContactNumber));
            return Optional.empty();
        }

        return optional.map(customer -> {
            String oldContactNumber = customer.getContactNumber();
            customer.setContactNumber(newContactNumber);
            Customer saved = customerRepository.save(customer);

            log.info("Updated contact number for customer ID {}: {} -> {}", id, oldContactNumber, newContactNumber);

//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_CONTACT_NUMBER,
//                    id.toString(),
//                    200,
//                    CdrInternalResult.SUCCESS,
//                    CdrBuilder.buildUpdateCustomerContactNumberSection(id, oldContactNumber, newContactNumber));
            return saved;
        });
    }

    @Transactional
    public Optional<Customer> updateEmail(Long id, String newEmail) {
        try {
            validateEmail(newEmail);
        } catch (ValidationException ex) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_EMAIL,
//                    id != null ? id.toString() : "UNKNOWN",
//                    400,
//                    CdrInternalResult.VALIDATION_FAILED,
//                    CdrBuilder.buildUpdateCustomerEmailSection(id, null, newEmail));
            throw ex;
        }

        Optional<Customer> optional = customerRepository.findById(id);
        if (optional.isEmpty()) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_EMAIL,
//                    id != null ? id.toString() : "UNKNOWN",
//                    404,
//                    CdrInternalResult.CUSTOMER_NOT_FOUND,
//                    CdrBuilder.buildUpdateCustomerEmailSection(id, null, newEmail));
            return Optional.empty();
        }

        return optional.map(customer -> {
            String oldEmail = customer.getEmail();
            customer.setEmail(newEmail);
            Customer saved = customerRepository.save(customer);

            log.info("Updated email for customer ID {}: {} -> {}", id, oldEmail, newEmail);

//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_EMAIL,
//                    id.toString(),
//                    200,
//                    CdrInternalResult.SUCCESS,
//                    CdrBuilder.buildUpdateCustomerEmailSection(id, oldEmail, newEmail));
            return saved;
        });
    }

    @Transactional
    public Optional<Customer> updateAddress(Long id, String newAddress) {
        try {
            validateAddress(newAddress);
        } catch (ValidationException ex) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_ADDRESS,
//                    id != null ? id.toString() : "UNKNOWN",
//                    400,
//                    CdrInternalResult.VALIDATION_FAILED,
//                    CdrBuilder.buildUpdateCustomerAddressSection(id, null, newAddress));
            throw ex;
        }

        Optional<Customer> optional = customerRepository.findById(id);
        if (optional.isEmpty()) {
//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_ADDRESS,
//                    id != null ? id.toString() : "UNKNOWN",
//                    404,
//                    CdrInternalResult.CUSTOMER_NOT_FOUND,
//                    CdrBuilder.buildUpdateCustomerAddressSection(id, null, newAddress));
            return Optional.empty();
        }

        return optional.map(customer -> {
            String oldAddress = customer.getAddress();
            customer.setAddress(newAddress);
            Customer saved = customerRepository.save(customer);

            log.info("Updated address for customer ID {}: {} -> {}", id, oldAddress, newAddress);

//            cdrLogger.logCdr(CdrOperation.UPDATE_CUSTOMER_ADDRESS,
//                    id.toString(),
//                    200,
//                    CdrInternalResult.SUCCESS,
//                    CdrBuilder.buildUpdateCustomerAddressSection(id, oldAddress, newAddress));
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
//            cdrLogger.logCdr(CdrOperation.DELETE_CUSTOMER,
//                    id != null ? id.toString() : "UNKNOWN",
//                    404,
//                    CdrInternalResult.CUSTOMER_NOT_FOUND,
//                    CdrBuilder.buildDeleteCustomerSection(id, null));
            return false;
        }
        customerRepository.deleteById(id);
        kafkaProducerService.publishCustomerDeleted(id);
        log.info("Deleted customer with ID: {}", id);

//        cdrLogger.logCdr(CdrOperation.DELETE_CUSTOMER,
//                id.toString(),
//                204,
//                CdrInternalResult.SUCCESS,
//                CdrBuilder.buildDeleteCustomerSection(id, null));
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
            throw new ValidationException("Bill cycle day must not be null");
        }
        if (billCycleDay < 1 || billCycleDay > 31) {
            throw new ValidationException("Bill cycle day must be between 1 and 31");
        }
        log.info("Finding customers with bill cycle day: {}", billCycleDay);
        return customerRepository.findByBillCycleDay(billCycleDay);
    }
}
