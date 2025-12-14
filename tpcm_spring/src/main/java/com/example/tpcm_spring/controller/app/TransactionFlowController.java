package com.example.tpcm_spring.controller.app;

import com.example.tpcm_spring.models.app.Transaction;
import com.example.tpcm_spring.service.app.TransactionFlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/transactions/flow")
@RequiredArgsConstructor
public class TransactionFlowController {

    private final TransactionFlowService transactionFlowService;

    @PostMapping("/request")
    @Operation(summary = "Request transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction requested successfully"),
            @ApiResponse(responseCode = "404", description = "Subscriber not found"),
            @ApiResponse(responseCode = "422", description = "Insufficient balance or limit exceeded")
    })
    public ResponseEntity<Transaction> processTransaction(@RequestBody TransactionRequest request) {
        Transaction transaction = transactionFlowService.processPendingTransaction(
                request.getMsisdn(),
                request.getAmount(),
                request.getThirdPartyId(),
                request.getPartialReservation(),
                request.getChannel()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/commit")
    @Operation(summary = "Commit transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction committed successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
    })
    public ResponseEntity<Transaction> commitTransaction(@RequestBody TransactionCommit request) {
        Transaction transaction = transactionFlowService.commitTransaction(request.getTransactionId(), request.getAmount());
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/cancel/{transactionId}")
    @Operation(summary = "Cancel transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<Transaction> cancelTransaction(@PathVariable Long transactionId) {
        Transaction transaction = transactionFlowService.cancelTransaction(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/details/{transactionId}")
    @Operation(summary = "Get transaction details (CDR logged)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<Transaction> getTransactionDetails(@PathVariable Long transactionId) {
        Transaction transaction = transactionFlowService.getTransactionDetails(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/msisdn/{msisdn}")
    @Operation(summary = "Get transactions for MSISDN (CDR logged)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid MSISDN format")
    })
    public ResponseEntity<List<Transaction>> getTransactionsForMsisdn(@PathVariable String msisdn) {
        List<Transaction> transactions = transactionFlowService.getTransactionsForMsisdn(msisdn);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/balance/{msisdn}")
    @Operation(summary = "Get subscriber available balance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Subscriber not found")
    })
    public ResponseEntity<BalanceResponse> getSubscriberBalance(@PathVariable String msisdn) {
        Double availableBalance = transactionFlowService.getAvailableBalance(msisdn);
        return ResponseEntity.ok(new BalanceResponse(msisdn, availableBalance));
    }

    @Data
    public static class TransactionRequest {
        private String msisdn;
        private Double amount;
        private Long thirdPartyId;
        private String partialReservation;
        private String channel;
    }

    @Data
    public static class TransactionCommit {
        private Long transactionId;
        private Double amount;
    }

    @Data
    @SuppressWarnings("all")
    public static class BalanceResponse {
        private final String msisdn;
        private final Double availableBalance;
    }
}