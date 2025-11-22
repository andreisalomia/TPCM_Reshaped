package com.example.kafkaconsumer.domain.tpcm;

import com.example.kafkaconsumer.entity.CloseSubscriber;
import com.example.kafkaconsumer.model.app.Subscriber;
import com.example.kafkaconsumer.repository.app.CustomerRepositoryApp;
import com.example.kafkaconsumer.repository.app.SubscriberRepositoryApp;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.kafkaconsumer.repository.app.LimitRepository;
import com.example.kafkaconsumer.model.app.Limit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloseSubscriberProcessor extends AbstractMessageProcessor<CloseSubscriber> {

    private final SubscriberRepositoryApp subscriberRepository;
    private final CustomerRepositoryApp customerRepository;
    private final LimitRepository limitRepository;

    @Override
    @Counted("processCloseSubscriber")
    @Timed("processCloseSubscriber")
    @Transactional
    public void process(CloseSubscriber closeSubscriber) {
        log.info("Processing CLOSE_SUBSCRIBER for subscriber ID: {}", closeSubscriber.getSubscriberID());

        try {
            Subscriber subscriber = subscriberRepository.findById(closeSubscriber.getSubscriberID()).orElse(null);

            if (subscriber == null) {
                log.warn("Subscriber with ID {} not found in app database, nothing to delete",
                        closeSubscriber.getSubscriberID());
                return;
            }

            Long customerId = subscriber.getCustomer().getCustomerID();

            deleteSubscriberLimit(closeSubscriber.getSubscriberID());

            subscriberRepository.delete(subscriber);
            log.info("Successfully deleted subscriber with ID {} from app database",
                    closeSubscriber.getSubscriberID());

            updateCustomerSubscriberCount(customerId);

        } catch (Exception e) {
            log.error("Error processing CLOSE_SUBSCRIBER for subscriber ID: {}",
                    closeSubscriber.getSubscriberID(), e);
            throw new RuntimeException("Failed to process CLOSE_SUBSCRIBER event", e);
        }
    }

    private void updateCustomerSubscriberCount(Long customerId) {
        customerRepository.findById(customerId).ifPresent(customer -> {
            int count = subscriberRepository.countByCustomerCustomerID(customerId);
            customer.setNrSubscribers(count);
            customerRepository.save(customer);
            log.debug("Updated subscriber count for customer {} to {}", customerId, count);
        });
    }

    private void deleteSubscriberLimit(Long subscriberId) {
        try {
            Limit existingLimit = limitRepository.findBySubscriberID(subscriberId);
            if (existingLimit != null) {
                limitRepository.delete(existingLimit);
                log.info("Deleted limit for subscriber ID: {}", subscriberId);
            } else {
                log.info("No limit found for subscriber ID: {}", subscriberId);
            }
        } catch (Exception e) {
            log.error("Error deleting limit for subscriber ID: {}", subscriberId, e);
        }
    }
}