package Personal.Finance.Manager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Personal.Finance.Manager.dto.request.TransactionRequest;
import Personal.Finance.Manager.dto.response.TransactionResponse;
import Personal.Finance.Manager.model.Category;
import Personal.Finance.Manager.model.Transaction;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.repository.CategoryRepository;
import Personal.Finance.Manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public TransactionResponse createTransaction(
            TransactionRequest request,
            User user) {

        // 1. Check User
        if (user == null) {
            throw new RuntimeException("User is required");
        }

        // 2. Check Category
        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        // 3. Check Category belongs to User
        if (category.getUser() == null ||
                !category.getUser().getUserId().equals(user.getUserId())) {

            throw new RuntimeException(
                    "You do not have permission to use this category");
        }

        // 4. Category.type is the source of transaction type
        // No need to set type into Transaction if Transaction
        // does not have a type field.

        // 5. Create Transaction
        Transaction transaction = new Transaction();

        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setSpendingMoney(request.getSpendingMoney());
        transaction.setDescription(request.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());

        // 6. Save
        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // 7. Return response
        return mapToResponse(savedTransaction);
    }

    /**
     * GET ALL TRANSACTIONS OF CURRENT USER
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions(User user) {

        if (user == null) {
            throw new RuntimeException("User is required");
        }

        List<Transaction> transactions =
                transactionRepository.findByUserOrderByCreatedAtDesc(user);

        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * GET TRANSACTION BY ID
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(
            Long transactionId,
            User user) {

        if (user == null) {
            throw new RuntimeException("User is required");
        }

        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"));

        // Ownership check
        validateOwnership(transaction, user);

        return mapToResponse(transaction);
    }

    /**
     * UPDATE TRANSACTION
     */
    @Transactional
    public TransactionResponse updateTransaction(
            Long transactionId,
            TransactionRequest request,
            User user) {

        if (user == null) {
            throw new RuntimeException("User is required");
        }

        // 1. Find transaction
        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"));

        // 2. Check transaction belongs to current user
        validateOwnership(transaction, user);

        // 3. Find new category
        Category category =
                categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found"));

        // 4. Check category belongs to current user
        if (category.getUser() == null ||
                !category.getUser().getUserId().equals(user.getUserId())) {

            throw new RuntimeException(
                    "You do not have permission to use this category");
        }

        // 5. Update transaction
        transaction.setCategory(category);
        transaction.setSpendingMoney(request.getSpendingMoney());
        transaction.setDescription(request.getDescription());

        // Do not change createdAt

        // 6. Save
        Transaction updatedTransaction =
                transactionRepository.save(transaction);

        // 7. Return response
        return mapToResponse(updatedTransaction);
    }

    /**
     * DELETE TRANSACTION
     */
    @Transactional
    public void deleteTransaction(
            Long transactionId,
            User user) {

        if (user == null) {
            throw new RuntimeException("User is required");
        }

        // 1. Find transaction
        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"));

        // 2. Check ownership
        validateOwnership(transaction, user);

        // 3. Delete
        transactionRepository.delete(transaction);
    }

    /**
     * CHECK TRANSACTION OWNERSHIP
     */
    private void validateOwnership(
            Transaction transaction,
            User user) {

        if (transaction.getUser() == null ||
                !transaction.getUser().getUserId().equals(user.getUserId())) {

            throw new RuntimeException(
                    "You do not have permission to access this transaction");
        }
    }

    /**
     * MAP ENTITY -> RESPONSE
     */
    private TransactionResponse mapToResponse(
            Transaction transaction) {

        TransactionResponse response =
                new TransactionResponse();

        response.setTransactionId(
                transaction.getTransactionId());

        response.setCategoryId(
                transaction.getCategory().getCategoryId());

        response.setCategoryName(
                transaction.getCategory().getCategoryName());

        response.setType(
                transaction.getCategory().getType());

        response.setSpendingMoney(
                transaction.getSpendingMoney());

        response.setDescription(
                transaction.getDescription());

        response.setCreatedAt(
                transaction.getCreatedAt());

        return response;
    }
}