package com.example.tpcm_spring.service.app;

import com.example.tpcm_spring.exceptions.ConflictException;
import com.example.tpcm_spring.exceptions.NotFoundException;
import com.example.tpcm_spring.exceptions.ValidationException;
import com.example.tpcm_spring.models.app.Limit;
import com.example.tpcm_spring.models.app.Subscriber;
import com.example.tpcm_spring.repository.app.LimitRepository;
import com.example.tpcm_spring.repository.app.SubscriberRepositoryApp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitServiceApp {

    private final LimitRepository limitRepository;
    private final SubscriberRepositoryApp subscriberRepository;

    private void validateLimit(Limit limit) {
        if (limit.getSubscriber() == null || limit.getSubscriber().getSubscriberID() == null) {
            log.warn("Subscriber must be specified for Limit");
            throw new ValidationException("Subscriber must be specified for Limit");
        }

        if (limit.getConsumedAmount() != null && limit.getConsumedAmount() < 0) {
            log.warn("Consumed amount must be >= 0");
            throw new ValidationException("Consumed amount must be >= 0");
        }

        if (limit.getMaxAmountCycle() != null && limit.getMaxAmountCycle() < 1) {
            log.warn("Max amount per cycle must be >= 1");
            throw new ValidationException("Max amount per cycle must be >= 1");
        }

        if (limit.getMaxAmountTransaction() != null &&
                limit.getMaxAmountCycle() != null &&
                limit.getMaxAmountTransaction() > limit.getMaxAmountCycle()) {
            log.warn("Max amount per transaction cannot exceed cycle limit");
            throw new ValidationException("Max amount per transaction cannot exceed cycle limit");
        }
    }

    @Transactional
    public Limit createLimit(Limit limit) {
        validateLimit(limit);

        Long sid = limit.getSubscriber().getSubscriberID();
        Subscriber subscriber = subscriberRepository.findById(sid)
                .orElseThrow(() -> new NotFoundException("Subscriber with ID " + sid + " not found"));

        if (limitRepository.findBySubscriberID(sid) != null) {
            log.warn("Limit already exists for subscriber ID: {}", sid);
            throw new ConflictException("Limit already exists for this subscriber");
        }

        limit.setSubscriber(subscriber);
        if (limit.getLastReset() == null) {
            limit.setLastReset(new Timestamp(System.currentTimeMillis()));
        }

        Limit saved = limitRepository.save(limit);
        log.info("Created limit for subscriber ID: {}", sid);
        return saved;
    }

    public List<Limit> getAllLimits() {
        log.info("Fetching all limits");
        return limitRepository.findAll();
    }

    public Limit getLimitBySubscriberId(Long subscriberId) {
        Limit limit = limitRepository.findBySubscriberID(subscriberId);
        if (limit == null) {
            log.warn("Limit not found for subscriber ID: {}", subscriberId);
            throw new NotFoundException("Limit for subscriber ID " + subscriberId + " not found");
        }
        log.info("Fetched limit for subscriber ID: {}", subscriberId);
        return limit;
    }

    @Transactional
    public Limit updateLimit(Long subscriberId, Limit updated) {
        Limit existing = limitRepository.findBySubscriberID(subscriberId);
        if (existing == null) {
            throw new NotFoundException("Limit for subscriber ID " + subscriberId + " not found");
        }

        validateLimit(updated);

        existing.setConsumedAmount(updated.getConsumedAmount());
        existing.setLastReset(updated.getLastReset());
        existing.setMaxAmountCycle(updated.getMaxAmountCycle());
        existing.setMaxAmountTransaction(updated.getMaxAmountTransaction());

        Limit saved = limitRepository.save(existing);
        log.info("Updated limit for subscriber ID: {}", subscriberId);
        return saved;
    }

    @Transactional
    public void deleteLimit(Long subscriberId) {
        Limit existing = limitRepository.findBySubscriberID(subscriberId);
        if (existing == null) {
            log.warn("Attempt to delete non-existing limit for subscriber ID: {}", subscriberId);
            throw new NotFoundException("Limit for subscriber ID " + subscriberId + " not found");
        }
        limitRepository.delete(existing);
        log.info("Deleted limit for subscriber ID: {}", subscriberId);
    }
}