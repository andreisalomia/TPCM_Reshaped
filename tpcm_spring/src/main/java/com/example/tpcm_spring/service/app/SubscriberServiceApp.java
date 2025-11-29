package com.example.tpcm_spring.service.app;

import com.example.tpcm_spring.exceptions.ConflictException;
import com.example.tpcm_spring.exceptions.NotFoundException;
import com.example.tpcm_spring.exceptions.ValidationException;
import com.example.tpcm_spring.models.app.Customer;
import com.example.tpcm_spring.models.app.Subscriber;
import com.example.tpcm_spring.repository.app.CustomerRepositoryApp;
import com.example.tpcm_spring.repository.app.SubscriberRepositoryApp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberServiceApp {

    private final SubscriberRepositoryApp subscriberRepository;
    private final CustomerRepositoryApp customerRepository;

    private static final List<String> VALID_STATUSES = Arrays.asList("ACTIVE", "INACTIVE", "SUSPENDED");
    private static final List<String> VALID_SUB_TYPES = Arrays.asList("PREPAID", "POSTPAID", "HYBRID");

    private void validateSubscriber(Subscriber s) {
        if (s.getMsisdn() == null || !s.getMsisdn().matches("^\\+?[0-9]{10,15}$")) {
            log.warn("Invalid MSISDN format: {}", s.getMsisdn());
            throw new ValidationException("MSISDN must be a valid phone number (10–15 digits, optional '+')");
        }

        if (s.getStatus() == null || !VALID_STATUSES.contains(s.getStatus().toUpperCase())) {
            log.warn("Invalid status: {}", s.getStatus());
            throw new ValidationException("Status must be one of: " + VALID_STATUSES);
        }

        if (s.getSubscriptionType() == null || !VALID_SUB_TYPES.contains(s.getSubscriptionType().toUpperCase())) {
            log.warn("Invalid subscription type: {}", s.getSubscriptionType());
            throw new ValidationException("Subscription type must be one of: " + VALID_SUB_TYPES);
        }

        if (s.getCustomer() == null || s.getCustomer().getCustomerID() == null) {
            log.warn("Customer is required for subscriber");
            throw new ValidationException("Customer must be specified");
        }

        customerRepository.findById(s.getCustomer().getCustomerID())
                .orElseThrow(() -> new NotFoundException("Customer with ID " + s.getCustomer().getCustomerID() + " not found"));
    }

    private void updateNrSubscribers(Long customerId) {
        int count = subscriberRepository.countByCustomerCustomerID(customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer with ID " + customerId + " not found"));
        customer.setNrSubscribers(count);
        customerRepository.save(customer);
    }

    @Transactional
    public Subscriber createSubscriber(Subscriber s) {
        validateSubscriber(s);

        if (subscriberRepository.existsByMsisdn(s.getMsisdn())) {
            log.warn("Conflict: MSISDN {} already exists", s.getMsisdn());
            throw new ConflictException("A subscriber with this MSISDN already exists");
        }

        Long customerId = s.getCustomer().getCustomerID();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer with ID " + customerId + " not found"));

        s.setCustomer(customer);
        Subscriber saved = subscriberRepository.save(s);
        updateNrSubscribers(customerId);

        log.info("Created subscriber with ID: {}", saved.getSubscriberID());
        return saved;
    }

    public List<Subscriber> getAllSubscribers() {
        log.info("Retrieving all subscribers");
        return subscriberRepository.findAll();
    }

    public Subscriber getSubscriberById(Long id) {
        log.info("Retrieving subscriber with ID: {}", id);
        return subscriberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscriber with ID " + id + " not found"));
    }

    public int countByCustomerId(Long customerId) {
        return subscriberRepository.countByCustomerCustomerID(customerId);
    }

    @Transactional
    public Subscriber updateSubscriber(Long id, Subscriber updated) {
        Subscriber existing = subscriberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscriber with ID " + id + " not found"));

        validateSubscriber(updated);

        if (!existing.getMsisdn().equals(updated.getMsisdn()) &&
                subscriberRepository.existsByMsisdn(updated.getMsisdn())) {
            log.warn("Conflict: MSISDN {} already exists", updated.getMsisdn());
            throw new ConflictException("A subscriber with this MSISDN already exists");
        }

        existing.setMsisdn(updated.getMsisdn());
        existing.setStatus(updated.getStatus());
        existing.setSubscriptionType(updated.getSubscriptionType());

        // customer update not allowed directly for integrity reasons

        Subscriber saved = subscriberRepository.save(existing);
        log.info("Updated subscriber with ID: {}", saved.getSubscriberID());
        return saved;
    }

    @Transactional
    public void deleteSubscriber(Long id) {
        Subscriber existing = subscriberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscriber with ID " + id + " not found"));

        Long customerId = existing.getCustomer().getCustomerID();
        subscriberRepository.deleteById(id);
        updateNrSubscribers(customerId);

        log.info("Deleted subscriber with ID: {}", id);
    }

    public List<Subscriber> findByMsisdn(String msisdn) {
        if (msisdn == null || msisdn.isBlank()) {
            log.warn("MSISDN must not be blank");
            throw new ValidationException("MSISDN must not be blank");
        }
        log.info("Finding subscribers with MSISDN: {}", msisdn);
        return subscriberRepository.findByMsisdn(msisdn);
    }

    public List<Subscriber> findBySubscriptionType(String type) {
        if (type == null || type.isBlank()) {
            log.warn("Subscription type must not be blank");
            throw new ValidationException("Subscription type must not be blank");
        }
        log.info("Finding subscribers with subscription type: {}", type);
        return subscriberRepository.findBySubscriptionTypeIgnoreCase(type);
    }
}
