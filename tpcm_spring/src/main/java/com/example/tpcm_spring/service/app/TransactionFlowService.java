package com.example.tpcm_spring.service.app;

import com.example.tpcm_spring.cdr.*;
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
    private final CdrLogger cdrLogger;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Transaction processPendingTransaction(String msisdn, Double amount, Long thirdPartyId,
                                                 String partialReservation, String channel) {
        log.info("Processing transaction for MSISDN: {}, Amount: {}", msisdn, amount);

        if (msisdn == null || msisdn.isEmpty()) {
            cdrLogger.logCdr(CdrOperation.RESERVE, msisdn, 400, CdrInternalResult.VALIDATION_FAILED,
                    CdrBuilder.buildReserveSection(msisdn, thirdPartyId, amount, null, null));
            throw new ValidationException("MSISDN cannot be null or empty");
        }

        if (amount == null || amount <= 0) {
            cdrLogger.logCdr(CdrOperation.RESERVE, msisdn, 400, CdrInternalResult.VALIDATION_FAILED,
                    CdrBuilder.buildReserveSection(msisdn, thirdPartyId, amount, null, null));
            throw new ValidationException("Amount must be greater than zero");
        }

        List<Subscriber> subscribers = subscriberRepository.findByMsisdn(msisdn);
        Subscriber subscriber = subscribers.stream().findFirst().orElse(null);

        if (subscriber == null || !"ACTIVE".equals(subscriber.getStatus())) {
            cdrLogger.logCdr(CdrOperation.RESERVE, msisdn, 404, CdrInternalResult.SUBSCRIBER_NOT_FOUND,
                    CdrBuilder.buildReserveSection(msisdn, thirdPartyId, amount, null, null));
            logEvent("TRANSACTION_REJECTED_NO_SUBSCRIBER",
                    subscriber != null ? subscriber.getSubscriberID() : null, null,
                    "Subscriber is not active: " + msisdn);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active subscriber not found for MSISDN: " + msisdn);
        }

        Limit limit = entityManager.find(Limit.class, subscriber.getSubscriberID(), LockModeType.PESSIMISTIC_WRITE);

        if (limit == null) {
            cdrLogger.logCdr(CdrOperation.RESERVE, msisdn, 404, CdrInternalResult.SUBSCRIBER_NOT_FOUND,
                    CdrBuilder.buildReserveSection(msisdn, thirdPartyId, amount, null, null));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Limit not found for subscriber: " + subscriber.getSubscriberID());
        }

        ThirdParty thirdParty = null;
        if (thirdPartyId != null && !channel.equals("CALL")) {
            thirdParty = thirdPartyRepository.findById(thirdPartyId)
                    .filter(tp -> "Y".equals(tp.getActive()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Third party not found or inactive: " + thirdPartyId));
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
        logEvent("TRANSACTION_PENDING", subscriber.getSubscriberID(), transaction.getTransactionId(),
                "Transaction initialized and pending.");

        if (amount > limit.getMaxAmountTransaction()) {
            transaction.setStatus("FAILED");
            transactionRepository.save(transaction);

            double remainingLimit = limit.getMaxAmountCycle() - limit.getConsumedAmount();

            cdrLogger.logCdr(CdrOperation.RESERVE, transaction.getTransactionId().toString(), 422,
                    CdrInternalResult.TRANSACTION_LIMIT_REACHED,
                    CdrBuilder.buildReserveSection(msisdn, thirdPartyId, amount,
                            transaction.getTransactionId(), remainingLimit));

            logEvent("TRANSACTION_REJECTED_MAX_TRANSACTION", subscriber.getSubscriberID(), transaction.getTransactionId(),
                    "Amount exceeds maximum per transaction limit");
            log.warn("Transaction {} rejected: Amount exceeds limit", transaction.getTransactionId());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Amount exceeds maximum per transaction limit. Requested: " + amount +
                            ", Max allowed per transaction: " + limit.getMaxAmountTransaction());
        }

        double availableBalance = limit.getMaxAmountCycle() - limit.getConsumedAmount();
        if (amount > availableBalance) {
            transaction.setStatus("FAILED");
            transactionRepository.save(transaction);

            double remainingBalance = limit.getMaxAmountCycle() - limit.getConsumedAmount();

            cdrLogger.logCdr(CdrOperation.RESERVE, transaction.getTransactionId().toString(), 422,
                    CdrInternalResult.THRESHOLD_REACHED,
                    CdrBuilder.buildReserveSection(msisdn, thirdPartyId, amount,
                            transaction.getTransactionId(), remainingBalance));

            logEvent("TRANSACTION_REJECTED_BALANCE", subscriber.getSubscriberID(), transaction.getTransactionId(),
                    "Insufficient balance. Available: " + availableBalance + ", Requested: " + amount);
            log.warn("Transaction {} rejected: Insufficient balance", transaction.getTransactionId());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient balance. Available: " + availableBalance + ", Requested: " + amount);
        }

        limit.setConsumedAmount(limit.getConsumedAmount() + amount);
        limit.setLastReset(new Timestamp(System.currentTimeMillis()));
        limitRepository.save(limit);

        double remainingBalance = limit.getMaxAmountCycle() - limit.getConsumedAmount();

        cdrLogger.logCdr(CdrOperation.RESERVE, transaction.getTransactionId().toString(), 201,
                CdrInternalResult.SUCCESS,
                CdrBuilder.buildReserveSection(msisdn, thirdPartyId, amount,
                        transaction.getTransactionId(), remainingBalance));

        log.info("Transaction {} reserved successfully for MSISDN: {}, Amount: {}",
                transaction.getTransactionId(), msisdn, amount);

        return transaction;
    }

    @Transactional
    public Transaction commitTransaction(Long transactionId, Double amount) {
        log.info("Committing transaction ID: {}, Amount: {}", transactionId, amount);

        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);

        if (transaction == null) {
            cdrLogger.logCdr(CdrOperation.COMMIT, transactionId.toString(), 404,
                    CdrInternalResult.TRANSACTION_NOT_FOUND,
                    CdrBuilder.buildCommitSection(transactionId, amount));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with ID: " + transactionId);
        }

        if ("COMMITTED".equals(transaction.getStatus())) {
            cdrLogger.logCdr(CdrOperation.COMMIT, transactionId.toString(), 400,
                    CdrInternalResult.TRANSACTION_ALREADY_COMMITTED,
                    CdrBuilder.buildCommitSection(transactionId, amount));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction already committed");
        }

        if ("FAILED".equals(transaction.getStatus())) {
            cdrLogger.logCdr(CdrOperation.COMMIT, transactionId.toString(), 400,
                    CdrInternalResult.TRANSACTION_ALREADY_CANCELLED,
                    CdrBuilder.buildCommitSection(transactionId, amount));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction already cancelled");
        }

        if (!"PENDING".equals(transaction.getStatus())) {
            cdrLogger.logCdr(CdrOperation.COMMIT, transactionId.toString(), 400,
                    CdrInternalResult.VALIDATION_FAILED,
                    CdrBuilder.buildCommitSection(transactionId, amount));
            logEvent("TRANSACTION_COMMIT_REJECTED", transaction.getSubscriber().getSubscriberID(), transactionId,
                    "Transaction is not in PENDING status: " + transaction.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Transaction is not in PENDING status: " + transaction.getStatus());
        }

        if ("Y".equals(transaction.getPartialReservation()) && amount > transaction.getAmount()) {
            cdrLogger.logCdr(CdrOperation.COMMIT, transactionId.toString(), 400,
                    CdrInternalResult.COMMIT_INVALID,
                    CdrBuilder.buildCommitSection(transactionId, amount));
            logEvent("TRANSACTION_COMMIT_REJECTED", transaction.getSubscriber().getSubscriberID(), transactionId,
                    "Amount exceeds reserved amount: " + amount + " > " + transaction.getAmount());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount exceeds reserved amount: " + amount + " > " + transaction.getAmount());
        }

        if ("N".equals(transaction.getPartialReservation()) && !amount.equals(transaction.getAmount())) {
            cdrLogger.logCdr(CdrOperation.COMMIT, transactionId.toString(), 400,
                    CdrInternalResult.COMMIT_INVALID,
                    CdrBuilder.buildCommitSection(transactionId, amount));
            logEvent("TRANSACTION_COMMIT_REJECTED", transaction.getSubscriber().getSubscriberID(), transactionId,
                    "Amount does not match the reserved amount: " + amount + " != " + transaction.getAmount());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount does not match the reserved amount: " + amount + " != " + transaction.getAmount());
        }

        Timestamp oneDayAgo = new Timestamp(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        if (transaction.getCreatedDate().before(oneDayAgo)) {
            cdrLogger.logCdr(CdrOperation.COMMIT, transactionId.toString(), 400,
                    CdrInternalResult.TRANSACTION_EXPIRED,
                    CdrBuilder.buildCommitSection(transactionId, amount));
            logEvent("TRANSACTION_COMMIT_REJECTED", transaction.getSubscriber().getSubscriberID(), transactionId,
                    "Transaction is older than one day and cannot be committed.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Transaction is older than one day and cannot be committed.");
        }

        transaction.setStatus("COMMITTED");
        transactionRepository.save(transaction);

        cdrLogger.logCdr(CdrOperation.COMMIT, transactionId.toString(), 200,
                CdrInternalResult.SUCCESS,
                CdrBuilder.buildCommitSection(transactionId, amount));

        logEvent("TRANSACTION_COMMITTED", transaction.getSubscriber().getSubscriberID(), transaction.getTransactionId(),
                "Transaction resources committed.");
        log.info("Transaction {} committed successfully for MSISDN: {}, Amount: {}",
                transaction.getTransactionId(), transaction.getMsisdn(), amount);

        return transaction;
    }

    @Transactional
    public Transaction cancelTransaction(Long transactionId) {
        log.info("Cancelling transaction ID: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);

        if (transaction == null) {
            cdrLogger.logCdr(CdrOperation.CANCEL, transactionId.toString(), 404,
                    CdrInternalResult.TRANSACTION_NOT_FOUND,
                    CdrBuilder.buildCancelSection(transactionId));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with ID: " + transactionId);
        }

        if ("FAILED".equals(transaction.getStatus())) {
            cdrLogger.logCdr(CdrOperation.CANCEL, transactionId.toString(), 400,
                    CdrInternalResult.TRANSACTION_ALREADY_CANCELLED,
                    CdrBuilder.buildCancelSection(transactionId));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction already cancelled");
        }

        if ("COMMITTED".equals(transaction.getStatus())) {
            cdrLogger.logCdr(CdrOperation.CANCEL, transactionId.toString(), 400,
                    CdrInternalResult.TRANSACTION_ALREADY_COMMITTED,
                    CdrBuilder.buildCancelSection(transactionId));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction already committed");
        }

        if ("PENDING".equals(transaction.getStatus())) {
            Subscriber subscriber = transaction.getSubscriber();

            Timestamp oneDayAgo = new Timestamp(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            if (transaction.getCreatedDate().before(oneDayAgo)) {
                cdrLogger.logCdr(CdrOperation.CANCEL, transactionId.toString(), 400,
                        CdrInternalResult.TRANSACTION_EXPIRED,
                        CdrBuilder.buildCancelSection(transactionId));
                logEvent("TRANSACTION_CANCEL_REJECTED", subscriber.getSubscriberID(), transactionId,
                        "Transaction is older than one day and cannot be cancelled.");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Transaction is older than one day and cannot be cancelled.");
            }

            Limit limit = entityManager.find(Limit.class, subscriber.getSubscriberID(), LockModeType.PESSIMISTIC_WRITE);

            if (limit == null) {
                cdrLogger.logCdr(CdrOperation.CANCEL, transactionId.toString(), 404,
                        CdrInternalResult.SUBSCRIBER_NOT_FOUND,
                        CdrBuilder.buildCancelSection(transactionId));
                logEvent("TRANSACTION_CANCEL_REJECTED", subscriber.getSubscriberID(), transactionId,
                        "Limit not found for subscriber: " + subscriber.getSubscriberID());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Limit not found for subscriber: " + subscriber.getSubscriberID());
            }

            limit.setConsumedAmount(limit.getConsumedAmount() - transaction.getAmount());
            limitRepository.save(limit);

            transaction.setStatus("FAILED");
            transactionRepository.save(transaction);

            cdrLogger.logCdr(CdrOperation.CANCEL, transactionId.toString(), 200,
                    CdrInternalResult.SUCCESS,
                    CdrBuilder.buildCancelSection(transactionId));

            logEvent("TRANSACTION_CANCELLED", subscriber.getSubscriberID(), transactionId,
                    "Transaction cancelled successfully.");
            log.info("Transaction {} cancelled successfully for MSISDN: {}, Amount: {}",
                    transaction.getTransactionId(), transaction.getMsisdn(), transaction.getAmount());
        } else {
            cdrLogger.logCdr(CdrOperation.CANCEL, transactionId.toString(), 400,
                    CdrInternalResult.VALIDATION_FAILED,
                    CdrBuilder.buildCancelSection(transactionId));
            logEvent("TRANSACTION_CANCEL_REJECTED", transaction.getSubscriber().getSubscriberID(), transactionId,
                    "Transaction is not in PENDING status: " + transaction.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Transaction is not in PENDING status: " + transaction.getStatus());
        }

        return transaction;
    }

    public Transaction getTransactionDetails(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);

        if (transaction == null) {
            cdrLogger.logCdr(CdrOperation.GET_TRANSACTION_DETAILS, transactionId.toString(), 404,
                    CdrInternalResult.TRANSACTION_NOT_FOUND, "");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with ID: " + transactionId);
        }

        cdrLogger.logCdr(CdrOperation.GET_TRANSACTION_DETAILS, transactionId.toString(), 200,
                CdrInternalResult.SUCCESS,
                CdrBuilder.buildGetTransactionDetailsSection(transaction));

        return transaction;
    }

    public List<Transaction> getTransactionsForMsisdn(String msisdn) {
        if (msisdn == null || msisdn.isEmpty()) {
            cdrLogger.logCdr(CdrOperation.GET_TRANSACTIONS_FOR_MSISDN, msisdn, 400,
                    CdrInternalResult.VALIDATION_FAILED,
                    CdrBuilder.buildGetTransactionsForMsisdnSection(msisdn));
            throw new ValidationException("MSISDN cannot be null or empty");
        }

        List<Transaction> transactions = transactionRepository.findByMsisdn(msisdn);

        cdrLogger.logCdr(CdrOperation.GET_TRANSACTIONS_FOR_MSISDN, msisdn, 200,
                CdrInternalResult.SUCCESS,
                CdrBuilder.buildGetTransactionsForMsisdnSection(msisdn));

        return transactions;
    }

    public Double getAvailableBalance(String msisdn) {
        if (msisdn == null || msisdn.isBlank()) {
            cdrLogger.logCdr(CdrOperation.GET_SUBSCRIBER_AVAILABLE_BALANCE, msisdn, 400,
                    CdrInternalResult.VALIDATION_FAILED,
                    CdrBuilder.buildGetSubscriberBalanceSection(msisdn, null, null, null, null));
            throw new ValidationException("MSISDN cannot be null or empty");
        }

        List<Subscriber> subscribers = subscriberRepository.findByMsisdn(msisdn);
        Subscriber subscriber = subscribers.stream().findFirst().orElse(null);

        if (subscriber == null || !"ACTIVE".equals(subscriber.getStatus())) {
            cdrLogger.logCdr(CdrOperation.GET_SUBSCRIBER_AVAILABLE_BALANCE, msisdn, 404,
                    CdrInternalResult.SUBSCRIBER_NOT_FOUND,
                    CdrBuilder.buildGetSubscriberBalanceSection(msisdn, null, null, null, null));
            logEvent("BALANCE_CHECK_REJECTED_NO_SUBSCRIBER",
                    subscriber != null ? subscriber.getSubscriberID() : null, null,
                    "Subscriber is not active: " + msisdn);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active subscriber not found for MSISDN: " + msisdn);
        }

        log.info("Checking available balance for MSISDN: {}", msisdn);

        Limit limit = limitRepository.findBySubscriberID(subscriber.getSubscriberID());
        if (limit == null) {
            cdrLogger.logCdr(CdrOperation.GET_SUBSCRIBER_AVAILABLE_BALANCE, msisdn, 404,
                    CdrInternalResult.GENERIC_ERROR,
                    CdrBuilder.buildGetSubscriberBalanceSection(msisdn, subscriber.getSubscriberID(), null, null, null));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Limit not found for subscriber");
        }

        double available = limit.getMaxAmountCycle() - limit.getConsumedAmount();

        cdrLogger.logCdr(CdrOperation.GET_SUBSCRIBER_AVAILABLE_BALANCE, msisdn, 200,
                CdrInternalResult.SUCCESS,
                CdrBuilder.buildGetSubscriberBalanceSection(msisdn, subscriber.getSubscriberID(), available,
                        limit.getMaxAmountCycle(), limit.getConsumedAmount()));

        return available;
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
