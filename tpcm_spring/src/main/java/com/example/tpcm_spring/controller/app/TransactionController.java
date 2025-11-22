package com.example.tpcm_spring.controller.app;

import com.example.tpcm_spring.models.app.Transaction;
import com.example.tpcm_spring.service.app.TransactionServiceApp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController("app.TransactionController")
@RequestMapping("/api/app/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionServiceApp transactionService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.createTransaction(transaction));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return transaction != null ? ResponseEntity.ok(transaction) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction updated) {
        Transaction transaction = transactionService.updateTransaction(id, updated);
        return transaction != null ? ResponseEntity.ok(transaction) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/by-subscriber-id")
    public ResponseEntity<List<Transaction>> getBySubscriberId(@RequestParam Long subscriberID) {
        return ResponseEntity.ok(transactionService.findBySubscriberId(subscriberID));
    }

    @GetMapping("/search/by-msisdn")
    public ResponseEntity<List<Transaction>> getByMsisdn(@RequestParam String msisdn) {
        return ResponseEntity.ok(transactionService.findByMsisdn(msisdn));
    }

    @GetMapping("/search/by-date-range")
    public ResponseEntity<List<Transaction>> getByDateRange(@RequestParam Timestamp from, @RequestParam Timestamp to) {
        return ResponseEntity.ok(transactionService.findByCreatedDateBetween(from, to));
    }

    @GetMapping("/search/by-thirdparty-id")
    public ResponseEntity<List<Transaction>> getByThirdPartyId(@RequestParam Long thirdPartyID) {
        return ResponseEntity.ok(transactionService.findByThirdPartyId(thirdPartyID));
    }
}
