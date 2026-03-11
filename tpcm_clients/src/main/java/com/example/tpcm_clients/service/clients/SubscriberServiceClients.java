package com.example.tpcm_clients.service.clients;

import com.example.tpcm_clients.exceptions.ConflictException;
import com.example.tpcm_clients.exceptions.NotFoundException;
import com.example.tpcm_clients.exceptions.ValidationException;
import com.example.tpcm_clients.cdr.CdrBuilder;
import com.example.tpcm_clients.cdr.CdrInternalResult;
import com.example.tpcm_clients.cdr.CdrLogger;
import com.example.tpcm_clients.cdr.CdrOperation;
import com.example.tpcm_clients.kafka.producer.KafkaProducerService;
import com.example.tpcm_clients.models.clients.Customer;
import com.example.tpcm_clients.models.clients.Subscriber;
import com.example.tpcm_clients.repository.clients.CustomerRepositoryClients;
import com.example.tpcm_clients.repository.clients.SubscriberRepositoryClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriberServiceClients {

    private final SubscriberRepositoryClients subscriberRepository;
    private final CustomerRepositoryClients customerRepository;
    private final KafkaProducerService kafkaProducerService;
   private final CdrLogger cdrLogger;

    private static final List<String> VALID_STATUSES = Arrays.asList("ACTIVE", "INACTIVE", "SUSPENDED");
    private static final List<String> VALID_SUBSCRIPTION_TYPES = Arrays.asList("PREPAID", "POSTPAID", "HYBRID");

    private void validateMsisdn(String msisdn) {
        if (msisdn == null || !msisdn.matches("^\\+?[0-9]{10,15}$")) {
            throw new ValidationException("MSISDN must be a valid phone number (10-15 digits, optional '+')");
        }
    }

    private void validateStatus(String status) {
        if (status == null || !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new ValidationException("Status must be one of: " + VALID_STATUSES);
        }
    }

    private void validateSubscriptionType(String subscriptionType) {
        if (subscriptionType == null || !VALID_SUBSCRIPTION_TYPES.contains(subscriptionType.toUpperCase())) {
            throw new ValidationException("Subscription type must be one of: " + VALID_SUBSCRIPTION_TYPES);
        }
    }

    private void updateNrSubscribers(Long customerId) {
        if (customerId != null) {
            int count = subscriberRepository.countByCustomerCustomerID(customerId);
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new NotFoundException("Customer with ID " + customerId + " not found"));
            customer.setNrSubscribers(count);
            log.info("Updating number of subscribers for customer ID {}: {}", customerId, count);
            customerRepository.save(customer);
        }
    }

    @Transactional
    public Subscriber createSubscriber(Subscriber s) {
        try {
            validateMsisdn(s.getMsisdn());
            validateStatus(s.getStatus());
            validateSubscriptionType(s.getSubscriptionType());

            if (s.getCustomer() == null || s.getCustomer().getCustomerID() == null) {
                throw new ValidationException("Customer reference must not be null");
            }

            Long customerId = s.getCustomer().getCustomerID();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new NotFoundException("Customer with ID " + customerId + " not found"));
            s.setCustomer(customer);

            if (subscriberRepository.existsByMsisdn(s.getMsisdn())) {
               cdrLogger.logCdr(CdrOperation.CREATE_SUBSCRIBER,
                       s.getMsisdn(),
                       409,
                       CdrInternalResult.ALREADY_EXISTS,
                       CdrBuilder.buildCreateSubscriberSection(null, s.getMsisdn(), s.getStatus(),
                               s.getSubscriptionType(), customerId));
                throw new ConflictException("A subscriber with this MSISDN already exists");
            }

            Subscriber saved = subscriberRepository.save(s);
            updateNrSubscribers(customerId);

            kafkaProducerService.publishSubscriberCreated(saved);
            log.info("Subscriber created with ID: {}", saved.getSubscriberID());

           cdrLogger.logCdr(CdrOperation.CREATE_SUBSCRIBER,
                   saved.getSubscriberID().toString(),
                   201,
                   CdrInternalResult.SUCCESS,
                   CdrBuilder.buildCreateSubscriberSection(saved.getSubscriberID(), saved.getMsisdn(),
                           saved.getStatus(), saved.getSubscriptionType(), customerId));

            return saved;
        } catch (ValidationException ex) {
           cdrLogger.logCdr(CdrOperation.CREATE_SUBSCRIBER,
                   s != null ? s.getMsisdn() : "UNKNOWN",
                   400,
                   CdrInternalResult.VALIDATION_FAILED,
                   CdrBuilder.buildCreateSubscriberSection(null,
                           s != null ? s.getMsisdn() : null,
                           s != null ? s.getStatus() : null,
                           s != null ? s.getSubscriptionType() : null,
                           s != null && s.getCustomer() != null ? s.getCustomer().getCustomerID() : null));
            throw ex;
        } catch (ConflictException ex) {
           cdrLogger.logCdr(CdrOperation.CREATE_SUBSCRIBER,
                   s != null ? s.getMsisdn() : "UNKNOWN",
                   409,
                   CdrInternalResult.ALREADY_EXISTS,
                   CdrBuilder.buildCreateSubscriberSection(null,
                           s != null ? s.getMsisdn() : null,
                           s != null ? s.getStatus() : null,
                           s != null ? s.getSubscriptionType() : null,
                           s != null && s.getCustomer() != null ? s.getCustomer().getCustomerID() : null));
            throw ex;
        } catch (NotFoundException ex) {
           cdrLogger.logCdr(CdrOperation.CREATE_SUBSCRIBER,
                   s != null ? s.getMsisdn() : "UNKNOWN",
                   404,
                   CdrInternalResult.CUSTOMER_NOT_FOUND,
                   CdrBuilder.buildCreateSubscriberSection(null,
                           s != null ? s.getMsisdn() : null,
                           s != null ? s.getStatus() : null,
                           s != null ? s.getSubscriptionType() : null,
                           s != null && s.getCustomer() != null ? s.getCustomer().getCustomerID() : null));
            throw ex;
        } catch (RuntimeException ex) {
           cdrLogger.logCdr(CdrOperation.CREATE_SUBSCRIBER,
                   s != null ? s.getMsisdn() : "UNKNOWN",
                   500,
                   CdrInternalResult.GENERIC_ERROR,
                   CdrBuilder.buildCreateSubscriberSection(null,
                           s != null ? s.getMsisdn() : null,
                           s != null ? s.getStatus() : null,
                           s != null ? s.getSubscriptionType() : null,
                           s != null && s.getCustomer() != null ? s.getCustomer().getCustomerID() : null));
            throw ex;
        }
    }

    @Transactional
    public Optional<Subscriber> updateMsisdn(Long id, String newMsisdn) {
        try {
            validateMsisdn(newMsisdn);
        } catch (ValidationException ex) {
           cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_MSISDN,
                   id != null ? id.toString() : newMsisdn,
                   400,
                   CdrInternalResult.VALIDATION_FAILED,
                   CdrBuilder.buildUpdateSubscriberMsisdnSection(id, null, newMsisdn));
            throw ex;
        }

        Optional<Subscriber> optional = subscriberRepository.findById(id);
        if (optional.isEmpty()) {
           cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_MSISDN,
                   id != null ? id.toString() : "UNKNOWN",
                   404,
                   CdrInternalResult.SUBSCRIBER_NOT_FOUND,
                   CdrBuilder.buildUpdateSubscriberMsisdnSection(id, null, newMsisdn));
            return Optional.empty();
        }

        return optional.map(subscriber -> {
            String oldMsisdn = subscriber.getMsisdn();

            if (!oldMsisdn.equals(newMsisdn) && subscriberRepository.existsByMsisdn(newMsisdn)) {
               cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_MSISDN,
                       id.toString(),
                       409,
                       CdrInternalResult.ALREADY_EXISTS,
                       CdrBuilder.buildUpdateSubscriberMsisdnSection(id, oldMsisdn, newMsisdn));
                throw new ConflictException("A subscriber with this MSISDN already exists");
            }

            subscriber.setMsisdn(newMsisdn);
            Subscriber saved = subscriberRepository.save(subscriber);

            kafkaProducerService.publishSubscriberMsisdnUpdate(id, oldMsisdn, newMsisdn);
            log.info("Updated MSISDN for subscriber ID {}: {} -> {}", id, oldMsisdn, newMsisdn);

           cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_MSISDN,
                   id.toString(),
                   200,
                   CdrInternalResult.SUCCESS,
                   CdrBuilder.buildUpdateSubscriberMsisdnSection(id, oldMsisdn, newMsisdn));
            return saved;
        });
    }

    @Transactional
    public Optional<Subscriber> updateStatus(Long id, String newStatus) {
        try {
            validateStatus(newStatus);
        } catch (ValidationException ex) {
           cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_STATUS,
                   id != null ? id.toString() : "UNKNOWN",
                   400,
                   CdrInternalResult.VALIDATION_FAILED,
                   CdrBuilder.buildUpdateSubscriberStatusSection(id, null, newStatus));
            throw ex;
        }

        Optional<Subscriber> optional = subscriberRepository.findById(id);
        if (optional.isEmpty()) {
           cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_STATUS,
                   id != null ? id.toString() : "UNKNOWN",
                   404,
                   CdrInternalResult.SUBSCRIBER_NOT_FOUND,
                   CdrBuilder.buildUpdateSubscriberStatusSection(id, null, newStatus));
            return Optional.empty();
        }

        return optional.map(subscriber -> {
            String oldStatus = subscriber.getStatus();
            subscriber.setStatus(newStatus);
            Subscriber saved = subscriberRepository.save(subscriber);

            kafkaProducerService.publishSubscriberStatusUpdate(id, oldStatus, newStatus);
            log.info("Updated status for subscriber ID {}: {} -> {}", id, oldStatus, newStatus);

           cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_STATUS,
                   id.toString(),
                   200,
                   CdrInternalResult.SUCCESS,
                   CdrBuilder.buildUpdateSubscriberStatusSection(id, oldStatus, newStatus));
            return saved;
        });
    }

    @Transactional
    public Optional<Subscriber> updateSubscriptionType(Long id, String newSubscriptionType) {
        try {
            validateSubscriptionType(newSubscriptionType);
        } catch (ValidationException ex) {
           cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_SUBSCRIPTION_TYPE,
                   id != null ? id.toString() : "UNKNOWN",
                   400,
                   CdrInternalResult.VALIDATION_FAILED,
                   CdrBuilder.buildUpdateSubscriberSubscriptionTypeSection(id, null, newSubscriptionType));
            throw ex;
        }

        Optional<Subscriber> optional = subscriberRepository.findById(id);
        if (optional.isEmpty()) {
           cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_SUBSCRIPTION_TYPE,
                   id != null ? id.toString() : "UNKNOWN",
                   404,
                   CdrInternalResult.SUBSCRIBER_NOT_FOUND,
                   CdrBuilder.buildUpdateSubscriberSubscriptionTypeSection(id, null, newSubscriptionType));
            return Optional.empty();
        }

        return optional.map(subscriber -> {
            String oldSubscriptionType = subscriber.getSubscriptionType();
            subscriber.setSubscriptionType(newSubscriptionType);
            Subscriber saved = subscriberRepository.save(subscriber);

            kafkaProducerService.publishSubscriberSubscriptionTypeUpdate(id, oldSubscriptionType, newSubscriptionType);
            log.info("Updated subscription type for subscriber ID {}: {} -> {}", id, oldSubscriptionType, newSubscriptionType);

           cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_SUBSCRIPTION_TYPE,
                   id.toString(),
                   200,
                   CdrInternalResult.SUCCESS,
                   CdrBuilder.buildUpdateSubscriberSubscriptionTypeSection(id, oldSubscriptionType, newSubscriptionType));
            return saved;
        });
    }

    @Transactional
    public Optional<Subscriber> updateCustomer(Long id, Long newCustomerId) {
        Customer newCustomer = customerRepository.findById(newCustomerId)
                .orElseThrow(() -> {
                   cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_CUSTOMER,
                           id != null ? id.toString() : "UNKNOWN",
                           404,
                           CdrInternalResult.CUSTOMER_NOT_FOUND,
                           CdrBuilder.buildUpdateSubscriberCustomerSection(id, null, newCustomerId));
                    return new NotFoundException("Customer with ID " + newCustomerId + " not found");
                });

        Optional<Subscriber> optional = subscriberRepository.findById(id);
        if (optional.isEmpty()) {
           cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_CUSTOMER,
                   id != null ? id.toString() : "UNKNOWN",
                   404,
                   CdrInternalResult.SUBSCRIBER_NOT_FOUND,
                   CdrBuilder.buildUpdateSubscriberCustomerSection(id, null, newCustomerId));
            return Optional.empty();
        }

        return optional.map(subscriber -> {
            Long oldCustomerId = subscriber.getCustomer().getCustomerID();
            subscriber.setCustomer(newCustomer);
            Subscriber saved = subscriberRepository.save(subscriber);
            
            updateNrSubscribers(oldCustomerId);
            updateNrSubscribers(newCustomerId);

            kafkaProducerService.publishSubscriberCustomerUpdate(id, oldCustomerId, newCustomerId);
            log.info("Updated customer for subscriber ID {}: {} -> {}", id, oldCustomerId, newCustomerId);

           cdrLogger.logCdr(CdrOperation.UPDATE_SUBSCRIBER_CUSTOMER,
                   id.toString(),
                   200,
                   CdrInternalResult.SUCCESS,
                   CdrBuilder.buildUpdateSubscriberCustomerSection(id, oldCustomerId, newCustomerId));
            return saved;
        });
    }

    public List<Subscriber> getAllSubscribers() {
        log.info("Retrieving all subscribers");
        return subscriberRepository.findAll();
    }

    public Optional<Subscriber> getById(Long id) {
        log.info("Retrieving subscriber with ID: {}", id);
        return Optional.ofNullable(subscriberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscriber with ID " + id + " not found")));
    }

    @Transactional
    public boolean deleteSubscriber(Long id) {
        Optional<Subscriber> optional = subscriberRepository.findById(id);
        if (optional.isEmpty()) {
           cdrLogger.logCdr(CdrOperation.DELETE_SUBSCRIBER,
                   id != null ? id.toString() : "UNKNOWN",
                   404,
                   CdrInternalResult.SUBSCRIBER_NOT_FOUND,
                   CdrBuilder.buildDeleteSubscriberSection(id, null, null));
            return false;
        }

        Subscriber toDelete = optional.get();
        Long customerId = toDelete.getCustomer().getCustomerID();
        subscriberRepository.delete(toDelete);
        updateNrSubscribers(customerId);

        kafkaProducerService.publishSubscriberDeleted(id);
        log.info("Deleted subscriber with ID: {}", id);

       cdrLogger.logCdr(CdrOperation.DELETE_SUBSCRIBER,
               id.toString(),
               204,
               CdrInternalResult.SUCCESS,
               CdrBuilder.buildDeleteSubscriberSection(id, toDelete.getMsisdn(), customerId));
        return true;
    }

    public List<Subscriber> findByMsisdn(String msisdn) {
        if (msisdn == null || msisdn.isBlank()) {
            throw new ValidationException("MSISDN must not be blank");
        }
        validateMsisdn(msisdn);
        return subscriberRepository.findByMsisdn(msisdn);
    }

    public List<Subscriber> findBySubscriptionType(String subscriptionType) {
        if (subscriptionType == null || subscriptionType.isBlank()) {
            throw new ValidationException("Subscription type must not be blank");
        }
        validateSubscriptionType(subscriptionType);
        log.info("Finding subscribers with subscription type: {}", subscriptionType);
        return subscriberRepository.findBySubscriptionTypeIgnoreCase(subscriptionType);
    }
}
