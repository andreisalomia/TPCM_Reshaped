package com.example.tpcm_spring.service.app;

import com.example.tpcm_spring.exceptions.ValidationException;
import com.example.tpcm_spring.models.app.*;
import com.example.tpcm_spring.repository.app.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionFlowService {

    private final TransactionRepository transactionRepository;
    private final SubscriberRepositoryApp subscriberRepository;
    private final LimitRepository limitRepository;
    private final ThirdPartyRepository thirdPartyRepository;
    private final LogEventRepository logEventRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Transaction processPendingTransaction(String msisdn, Double amount, Long thirdPartyId, String partialReservation, String channel) {
        log.info("Processing transaction for MSISDN: {}, Amount: {}", msisdn, amount);

        if (msisdn == null || msisdn.isEmpty()) {
            throw new ValidationException("MSISDN cannot be null or empty");
        }

        if (amount == null || amount <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        List<Subscriber> subscribers = subscriberRepository.findByMsisdn(msisdn);
        Subscriber subscriber = subscribers.stream().findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscriber not found for MSISDN: " + msisdn));

        if (!"ACTIVE".equals(subscriber.getStatus())) {
            logEvent("TRANSACTION_REJECTED_NO_SUBSCRIBER", subscriber.getSubscriberID(), null, "Subscriber is not active: " + msisdn);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active subscriber not found for MSISDN: " + msisdn);
        }

        Limit limit = entityManager.find(Limit.class, subscriber.getSubscriberID(), LockModeType.PESSIMISTIC_WRITE);

        if(limit == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Limit not found for subscriber: " + subscriber.getSubscriberID());
        }

        ThirdParty thirdParty = null;
        if (thirdPartyId != null && !channel.equals("CALL")) {
            thirdParty = thirdPartyRepository.findById(thirdPartyId)
                    .filter(tp -> "Y".equals(tp.getActive()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Third party not found or inactive: " + thirdPartyId));
        }

        Transaction transaction = new Transaction();
        transaction.setSubscriber(subscriber);
        transaction.setMsisdn(msisdn);
        transaction.setAmount(amount);
        transaction.setChannel(channel);
        transaction.setThirdParty(thirdParty);
        transaction.setStatus("PENDING");
        transaction.setPartialReservation("Y".equalsIgnoreCase(partialReservation) ? "Y" : "N");
        transaction.setCreatedDate(new Timestamp(System.currentTimeMillis()));

        transaction = transactionRepository.save(transaction);
        logEvent("TRANSACTION_PENDING", subscriber.getSubscriberID(), transaction.getTransactionId(), "Transaction initialized and pending.");

        if (amount > limit.getMaxAmountTransaction()) {
            return rejectTransaction(transaction, subscriber.getSubscriberID(),
                    "Amount exceeds maximum per transaction limit. Requested: " + amount +
                            ", Max allowed per transaction: " + limit.getMaxAmountTransaction(),
                    "TRANSACTION_REJECTED_MAX_TRANSACTION");
        }
        double availableBalance = limit.getMaxAmountCycle() - limit.getConsumedAmount();
        if (amount > availableBalance) {
            return rejectTransaction(transaction, subscriber.getSubscriberID(), "Insufficient balance. Available: " + availableBalance + ", Requested: " + amount, "TRANSACTION_REJECTED_BALANCE");
        }

        limit.setConsumedAmount(limit.getConsumedAmount() + amount);
        limit.setLastReset(new Timestamp(System.currentTimeMillis()));
        limitRepository.save(limit);

        return transaction;
    }

    @Transactional
    public Transaction commitTransaction(Long transactionId, Double amount) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with ID: " + transactionId));

        if (!"PENDING".equals(transaction.getStatus())) {
            logEvent("TRANSACTION_COMMIT_REJECTED", transaction.getSubscriber().getSubscriberID(), transactionId, "Transaction is not in PENDING status: " + transaction.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction is not in PENDING status: " + transaction.getStatus());
        }

        if ("Y".equals(transaction.getPartialReservation()) && amount > transaction.getAmount()) {
            logEvent("TRANSACTION_COMMIT_REJECTED", transaction.getSubscriber().getSubscriberID(), transactionId, "Amount exceeds reserved amount: " + amount + " > " + transaction.getAmount());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount exceeds reserved amount: " + amount + " > " + transaction.getAmount());
        }

        if("N".equals(transaction.getPartialReservation()) && !amount.equals(transaction.getAmount())) {
            logEvent("TRANSACTION_COMMIT_REJECTED", transaction.getSubscriber().getSubscriberID(), transactionId, "Amount does not match the reserved amount: " + amount + " != " + transaction.getAmount());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount does not match the reserved amount: " + amount + " != " + transaction.getAmount());
        }

        Timestamp oneDayAgo = new Timestamp(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        if (transaction.getCreatedDate().before(oneDayAgo)) {
            logEvent("TRANSACTION_COMMIT_REJECTED", transaction.getSubscriber().getSubscriberID(), transactionId, "Transaction is older than one day and cannot be committed.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction is older than one day and cannot be committed.");
        }

        transaction.setStatus("COMMITTED");
        transactionRepository.save(transaction);
        logEvent("TRANSACTION_COMMITTED", transaction.getSubscriber().getSubscriberID(), transaction.getTransactionId(), "Transaction resources committed.");
        log.info("Transaction {} committed successfully for MSISDN: {}, Amount: {}", transaction.getTransactionId(), transaction.getMsisdn(), amount);
        return transaction;
    }

    @Transactional
    public Transaction cancelTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with ID: " + transactionId));

        if ("PENDING".equals(transaction.getStatus())) {
            Subscriber subscriber = transaction.getSubscriber();

            Timestamp oneDayAgo = new Timestamp(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            if (transaction.getCreatedDate().before(oneDayAgo)) {
                logEvent("TRANSACTION_CANCEL_REJECTED", subscriber.getSubscriberID(), transactionId, "Transaction is older than one day and cannot be cancelled.");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction is older than one day and cannot be cancelled.");
            }

            Limit limit = entityManager.find(Limit.class, subscriber.getSubscriberID(), LockModeType.PESSIMISTIC_WRITE);

            if (limit == null) {
                logEvent("TRANSACTION_CANCEL_REJECTED", subscriber.getSubscriberID(), transactionId, "Limit not found for subscriber: " + subscriber.getSubscriberID());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Limit not found for subscriber: " + subscriber.getSubscriberID());
            }

            limit.setConsumedAmount(limit.getConsumedAmount() - transaction.getAmount());
            limitRepository.save(limit);

            transaction.setStatus("FAILED");
            transactionRepository.save(transaction);
            logEvent("TRANSACTION_CANCELLED", subscriber.getSubscriberID(), transactionId, "Transaction cancelled successfully.");
            log.info("Transaction {} cancelled successfully for MSISDN: {}, Amount: {}", transaction.getTransactionId(), transaction.getMsisdn(), transaction.getAmount());
        } else {
            logEvent("TRANSACTION_CANCEL_REJECTED", transaction.getSubscriber().getSubscriberID(), transactionId, "Transaction is not in PENDING status: " + transaction.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction is not in PENDING status: " + transaction.getStatus());
        }

        return transaction;
    }

    private Transaction rejectTransaction(Transaction transaction, Long subscriberId, String reason, String eventType) {
        transaction.setStatus("FAILED");
        transactionRepository.save(transaction);
        logEvent(eventType, subscriberId, transaction.getTransactionId(), reason);
        log.warn("Transaction {} rejected: {}", transaction.getTransactionId(), reason);
        return transaction;
    }

    public Double getAvailableBalance(String msisdn) {
        List<Subscriber> subscribers = subscriberRepository.findByMsisdn(msisdn);
        Subscriber subscriber = subscribers.getFirst();
        if (subscriber == null) {
            logEvent("BALANCE_CHECK_REJECTED_NO_SUBSCRIBER", null, null, "Subscriber not found for MSISDN: " + msisdn);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscriber not found for MSISDN: " + msisdn);
        }
        if (!"ACTIVE".equals(subscriber.getStatus())) {
            logEvent("BALANCE_CHECK_REJECTED_NO_SUBSCRIBER", subscriber.getSubscriberID(), null, "Subscriber is not active: " + msisdn);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active subscriber not found for MSISDN: " + msisdn);
        }
        log.info("Checking available balance for MSISDN: {}", msisdn);

        Limit limit = limitRepository.findBySubscriberID(subscriber.getSubscriberID());
        if (limit == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Limit not found for subscriber");
        }

        return limit.getMaxAmountCycle() - limit.getConsumedAmount();
    }

    private void logEvent(String eventType, Long subscriberId, Long transactionId, String detail) {
        try {
            LogEvent event = new LogEvent();
            event.setEventType(eventType);
            event.setLogTimestamp(new Timestamp(System.currentTimeMillis()));

            if (subscriberId != null) {
                event.setSubscriber(subscriberRepository.findById(subscriberId).orElse(null));
            }

            if (transactionId != null) {
                event.setTransaction(transactionRepository.findById(transactionId).orElse(null));
            }

            event.setDetailedEvent(detail);
            logEventRepository.save(event);
        } catch (Exception e) {
            log.error("Failed to log event: {}", e.getMessage());
        }
    }
}
