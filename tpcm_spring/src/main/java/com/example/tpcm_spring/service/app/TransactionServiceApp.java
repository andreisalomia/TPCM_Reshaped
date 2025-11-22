package com.example.tpcm_spring.service.app;

import com.example.tpcm_spring.exceptions.NotFoundException;
import com.example.tpcm_spring.exceptions.ValidationException;
import com.example.tpcm_spring.models.app.Transaction;
import com.example.tpcm_spring.models.app.Subscriber;
import com.example.tpcm_spring.repository.app.SubscriberRepositoryApp;
import com.example.tpcm_spring.repository.app.ThirdPartyRepository;
import com.example.tpcm_spring.repository.app.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceApp {

    private final TransactionRepository transactionRepository;
    private final SubscriberRepositoryApp subscriberRepository;
    private final ThirdPartyRepository thirdPartyRepository;

    private static final List<String> VALID_CHANNELS = Arrays.asList("SMS", "CALL", "APP");
    private static final List<String> VALID_STATUSES = Arrays.asList("PENDING", "COMMITTED", "FAILED");

    private void validateTransaction(Transaction t) {
        if (t.getAmount() == null || t.getAmount() <= 0 || t.getAmount() > 50) {
            log.warn("Invalid transaction amount: {}", t.getAmount());
            throw new ValidationException("Amount must be greater than 0 and not exceed 50 EUR");
        }

        if (t.getChannel() == null || !VALID_CHANNELS.contains(t.getChannel().toUpperCase())) {
            log.warn("Invalid transaction channel: {}", t.getChannel());
            throw new ValidationException("Channel must be one of: " + VALID_CHANNELS);
        }

        if (t.getStatus() == null || !VALID_STATUSES.contains(t.getStatus().toUpperCase())) {
            log.warn("Invalid transaction status: {}", t.getStatus());
            throw new ValidationException("Status must be one of: " + VALID_STATUSES);
        }

        if (t.getPartialReservation() == null || !(t.getPartialReservation().equals("Y") || t.getPartialReservation().equals("N"))) {
            log.warn("Invalid partialReservation value: {}", t.getPartialReservation());
            throw new ValidationException("partialReservation must be 'Y' or 'N'");
        }

        if (t.getMsisdn() == null || !t.getMsisdn().matches("^\\+?[0-9]{10,15}$")) {
            log.warn("Invalid MSISDN format: {}", t.getMsisdn());
            throw new ValidationException("MSISDN must be a valid phone number (10-15 digits, optional '+')");
        }

        if (t.getSubscriber() == null || t.getSubscriber().getSubscriberID() == null ||
                !subscriberRepository.existsById(t.getSubscriber().getSubscriberID())) {
            log.warn("Subscriber does not exist for transaction: {}", t.getSubscriber());
            throw new NotFoundException("Subscriber does not exist");
        }

        if (t.getThirdParty() != null && t.getThirdParty().getThirdPartyID() != null &&
                !thirdPartyRepository.existsById(t.getThirdParty().getThirdPartyID())) {
            log.warn("Third party does not exist for transaction: {}", t.getThirdParty());
            throw new NotFoundException("Third party does not exist");
        }
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getMsisdn() == null && transaction.getSubscriber() != null) {
            Subscriber subscriber = subscriberRepository.findById(transaction.getSubscriber().getSubscriberID())
                    .orElseThrow(() -> new NotFoundException("Subscriber not found"));
            transaction.setMsisdn(subscriber.getMsisdn());
        }

        validateTransaction(transaction);

        if (transaction.getCreatedDate() == null) {
            transaction.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        }

        log.info("Creating transaction for subscriber ID: {} with MSISDN: {}",
                transaction.getSubscriber().getSubscriberID(), transaction.getMsisdn());
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        log.info("Fetching all transactions");
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        log.info("Fetching transaction with ID: {}", id);
        return transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction with ID " + id + " not found"));
    }

    @Transactional
    public Transaction updateTransaction(Long id, Transaction updated) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction with ID " + id + " not found"));

        if (updated.getMsisdn() == null && updated.getSubscriber() != null) {
            Subscriber subscriber = subscriberRepository.findById(updated.getSubscriber().getSubscriberID())
                    .orElseThrow(() -> new NotFoundException("Subscriber not found"));
            updated.setMsisdn(subscriber.getMsisdn());
        }

        validateTransaction(updated);

        existing.setSubscriber(updated.getSubscriber());
        existing.setMsisdn(updated.getMsisdn());
        existing.setCreatedDate(updated.getCreatedDate());
        existing.setAmount(updated.getAmount());
        existing.setChannel(updated.getChannel());
        existing.setThirdParty(updated.getThirdParty());
        existing.setStatus(updated.getStatus());
        existing.setPartialReservation(updated.getPartialReservation());

        log.info("Updated transaction with ID: {}", existing.getTransactionId());
        return transactionRepository.save(existing);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            log.warn("Attempt to delete non-existing transaction with ID: {}", id);
            throw new NotFoundException("Transaction with ID " + id + " not found");
        }
        transactionRepository.deleteById(id);
        log.info("Deleted transaction with ID: {}", id);
    }

    public List<Transaction> findBySubscriberId(Long subscriberID) {
        if (!subscriberRepository.existsById(subscriberID)) {
            log.warn("Subscriber with ID {} not found", subscriberID);
            throw new NotFoundException("Subscriber with ID " + subscriberID + " not found");
        }
        log.info("Fetching transactions for subscriber ID: {}", subscriberID);
        return transactionRepository.findBySubscriber_SubscriberID(subscriberID);
    }

    public List<Transaction> findByCreatedDateBetween(Timestamp from, Timestamp to) {
        if (from == null || to == null || from.after(to)) {
            log.warn("Invalid date range: from {} to {}", from, to);
            throw new ValidationException("Invalid date range");
        }
        log.info("Fetching transactions between {} and {}", from, to);
        return transactionRepository.findByCreatedDateBetween(from, to);
    }

    public List<Transaction> findByThirdPartyId(Long thirdPartyID) {
        if (thirdPartyID == null || !thirdPartyRepository.existsById(thirdPartyID)) {
            log.warn("Third party with ID {} not found", thirdPartyID);
            throw new NotFoundException("Third party with ID " + thirdPartyID + " not found");
        }
        log.info("Fetching transactions for third party ID: {}", thirdPartyID);
        return transactionRepository.findByThirdParty_ThirdPartyID(thirdPartyID);
    }

    public List<Transaction> findByMsisdn(String msisdn) {
        if (msisdn == null || !msisdn.matches("^\\+?[0-9]{10,15}$")) {
            log.warn("Invalid MSISDN format: {}", msisdn);
            throw new ValidationException("Invalid MSISDN format");
        }
        log.info("Fetching transactions for MSISDN: {}", msisdn);
        return transactionRepository.findByMsisdn(msisdn);
    }
}