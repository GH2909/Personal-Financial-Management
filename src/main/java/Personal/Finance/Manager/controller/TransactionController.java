package Personal.Finance.Manager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Personal.Finance.Manager.dto.request.TransactionRequest;
import Personal.Finance.Manager.dto.response.TransactionResponse;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestAttribute("user") User user,
            @Valid @RequestBody TransactionRequest request) {

        return ResponseEntity.ok(
                transactionService.createTransaction(request, user));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                transactionService.getAllTransactions(user));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long transactionId,
            @RequestAttribute("user") User user) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(transactionId, user));
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long transactionId,
            @RequestAttribute("user") User user,
            @Valid @RequestBody TransactionRequest request) {

        return ResponseEntity.ok(
                transactionService.updateTransaction(
                        transactionId, request, user));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<?> deleteTransaction(
            @PathVariable Long transactionId,
            @RequestAttribute("user") User user) {

        transactionService.deleteTransaction(transactionId, user);

        return ResponseEntity.ok("Transaction deleted successfully");
    }
}