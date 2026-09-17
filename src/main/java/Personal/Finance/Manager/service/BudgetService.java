package Personal.Finance.Manager.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import Personal.Finance.Manager.dto.request.BudgetCategoryRequest;
import Personal.Finance.Manager.dto.request.BudgetRequest;
import Personal.Finance.Manager.dto.response.BudgetCategoryResponse;
import Personal.Finance.Manager.dto.response.BudgetResponse;

import Personal.Finance.Manager.model.AllocationMode;
import Personal.Finance.Manager.model.Budget;
import Personal.Finance.Manager.model.BudgetCategory;
import Personal.Finance.Manager.model.Category;
import Personal.Finance.Manager.model.User;

import Personal.Finance.Manager.repository.BudgetCategoryRepository;
import Personal.Finance.Manager.repository.BudgetRepository;
import Personal.Finance.Manager.repository.CategoryRepository;
import Personal.Finance.Manager.repository.TransactionRepository;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(
            BudgetRepository budgetRepository,
            BudgetCategoryRepository budgetCategoryRepository,
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository) {

        this.budgetRepository = budgetRepository;
        this.budgetCategoryRepository = budgetCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    // =========================================================
    // CREATE BUDGET
    // =========================================================

    @Transactional
    public BudgetResponse createBudget(
            BudgetRequest request,
            User user) {

        if (budgetRepository.existsByUserAndYearAndMonth(
                user,
                request.getYear(),
                request.getMonth())) {

            throw new RuntimeException(
                    "Budget already exists for this month");
        }

        Budget budget = new Budget();

        budget.setUser(user);
        budget.setTotalBudget(request.getTotalBudget());
        budget.setSavingAmount(BigDecimal.ZERO);
        budget.setRolloverAmount(BigDecimal.ZERO);

        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        budget.setAllocationMode(
                request.getAllocationMode());

        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());

        budget = budgetRepository.save(budget);

        return mapToResponse(budget);
    }

    // =========================================================
    // GET ALL BUDGETS
    // =========================================================

    public List<BudgetResponse> getAllBudgets(User user) {

        return budgetRepository
                .findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // GET BUDGET BY ID
    // =========================================================

    public BudgetResponse getBudgetById(
            Long budgetId,
            User user) {

        Budget budget = getUserBudget(
                budgetId,
                user);

        return mapToResponse(budget);
    }

    // =========================================================
    // UPDATE BUDGET
    // =========================================================

    @Transactional
    public BudgetResponse updateBudget(
            Long budgetId,
            BudgetRequest request,
            User user) {

        Budget budget = getUserBudget(
                budgetId,
                user);

        // Không cho thay đổi tháng
        if (!budget.getMonth().equals(request.getMonth())) {

            throw new RuntimeException(
                    "Budget month cannot be changed");
        }

        // Không cho thay đổi năm
        if (!budget.getYear().equals(request.getYear())) {

            throw new RuntimeException(
                    "Budget year cannot be changed");
        }

        // Không cho đổi mode sau khi tạo
        if (budget.getAllocationMode()
                != request.getAllocationMode()) {

            throw new RuntimeException(
                    "Allocation mode cannot be changed");
        }

        validateTotalBudget(
                request.getTotalBudget());

        budget.setTotalBudget(
                request.getTotalBudget());

        budget.setUpdatedAt(
                LocalDateTime.now());

        /*
         * PERCENTAGE:
         * Giữ nguyên % của category
         * rồi tính lại allocatedAmount.
         */
        if (budget.getAllocationMode()
                == AllocationMode.PERCENTAGE) {

            recalculateBudgetCategories(budget);
        }

        /*
         * AMOUNT:
         * Giữ nguyên allocatedAmount.
         *
         * Nếu Expense Budget mới nhỏ hơn
         * tổng amount hiện tại -> reject.
         */
        else {

            BigDecimal totalAllocated =
                    calculateTotalAllocatedAmount(budget);

            BigDecimal expenseBudget =
                    calculateExpenseBudget(budget);

            if (totalAllocated.compareTo(expenseBudget) > 0) {

                throw new RuntimeException(
                        "Total allocated amount exceeds new expense budget");
            }

            updatePercentagesFromAmounts(budget);
        }

        budget = budgetRepository.save(budget);

        return mapToResponse(budget);
    }

    // =========================================================
    // ADD BUDGET CATEGORY
    // =========================================================

    @Transactional
    public BudgetCategoryResponse addBudgetCategory(
            Long budgetId,
            BudgetCategoryRequest request,
            User user) {

        Budget budget = getUserBudget(
                budgetId,
                user);

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found"));

        // Category phải thuộc user hiện tại
        if (!category.getUser()
                .getUserId()
                .equals(user.getUserId())) {

            throw new RuntimeException(
                    "Category does not belong to user");
        }

        // Không cho add category trùng
        Optional<BudgetCategory> existing =
                budgetCategoryRepository
                        .findByBudgetAndCategory(
                                budget,
                                category);

        if (existing.isPresent()) {

            throw new RuntimeException(
                    "Category already exists in this budget");
        }

        BudgetCategory budgetCategory =
                new BudgetCategory();

        budgetCategory.setBudget(budget);
        budgetCategory.setCategory(category);

        budgetCategory.setCreatedAt(
                LocalDateTime.now());

        budgetCategory.setUpdatedAt(
                LocalDateTime.now());

        // =====================================================
        // PERCENTAGE MODE
        // =====================================================

        if (budget.getAllocationMode()
                == AllocationMode.PERCENTAGE) {

            BigDecimal percentage =
                    request.getAllocationPercentage();

            validatePercentageInput(percentage);

            /*
             * Nhập 0 => không tạo category.
             */
            if (percentage.compareTo(
                    BigDecimal.ZERO) == 0) {

                return null;
            }

            BigDecimal currentPercentage =
                    calculateTotalPercentage(budget);

            BigDecimal newTotal =
                    currentPercentage.add(percentage);

            /*
             * Trong quá trình nhập:
             * cho phép < 100
             * không cho > 100
             */
            if (newTotal.compareTo(
                    BigDecimal.valueOf(100)) > 0) {

                throw new RuntimeException(
                        "Total allocation percentage cannot exceed 100%");
            }

            budgetCategory.setAllocationPercentage(
                    percentage);

            BigDecimal amount =
                    calculateAllocatedAmount(
                            budget,
                            budgetCategory);

            budgetCategory.setAllocatedAmount(amount);
        }

        // =====================================================
        // AMOUNT MODE
        // =====================================================

        else {

            BigDecimal amount =
                    request.getAllocatedAmount();

            validateAmountInput(amount);

            /*
             * Nhập 0 => không tạo category.
             */
            if (amount.compareTo(
                    BigDecimal.ZERO) == 0) {

                return null;
            }

            BigDecimal expenseBudget =
                    calculateExpenseBudget(budget);

            BigDecimal currentTotal =
                    calculateTotalAllocatedAmount(budget);

            BigDecimal newTotal =
                    currentTotal.add(amount);

            /*
             * Trong quá trình nhập:
             * cho phép < Expense Budget
             * không cho > Expense Budget
             */
            if (newTotal.compareTo(
                    expenseBudget) > 0) {

                throw new RuntimeException(
                        "Total allocated amount exceeds expense budget");
            }

            budgetCategory.setAllocatedAmount(
                    amount);

            budgetCategory.setAllocationPercentage(
                    calculateAllocationPercentage(
                            budget,
                            amount));
        }

        budgetCategory =
                budgetCategoryRepository.save(
                        budgetCategory);

        return mapBudgetCategoryToResponse(
                budgetCategory);
    }

    // =========================================================
    // UPDATE BUDGET CATEGORY
    // =========================================================

    @Transactional
    public BudgetCategoryResponse updateBudgetCategory(
            Long budgetId,
            Long categoryId,
            BudgetCategoryRequest request,
            User user) {

        Budget budget = getUserBudget(
                budgetId,
                user);

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found"));

        BudgetCategory budgetCategory =
                budgetCategoryRepository
                        .findByBudgetAndCategory(
                                budget,
                                category)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Budget category not found"));

        // =====================================================
        // PERCENTAGE MODE
        // =====================================================

        if (budget.getAllocationMode()
                == AllocationMode.PERCENTAGE) {

            BigDecimal percentage =
                    request.getAllocationPercentage();

            validatePercentageInput(percentage);

            /*
             * 0 => xóa category
             */
            if (percentage.compareTo(
                    BigDecimal.ZERO) == 0) {

                if (hasTransactionsInBudgetMonth(
                        budget,
                        category)) {

                    throw new RuntimeException(
                            "Cannot remove category because transactions exist");
                }

                budgetCategoryRepository.delete(
                        budgetCategory);

                return null;
            }

            /*
             * Tổng % của các category khác
             */
            BigDecimal totalWithoutCurrent =
                    calculateTotalPercentage(budget)
                            .subtract(
                                    getPercentageOrZero(
                                            budgetCategory));

            BigDecimal newTotal =
                    totalWithoutCurrent.add(
                            percentage);

            if (newTotal.compareTo(
                    BigDecimal.valueOf(100)) > 0) {

                throw new RuntimeException(
                        "Total allocation percentage cannot exceed 100%");
            }

            budgetCategory.setAllocationPercentage(
                    percentage);

            budgetCategory.setAllocatedAmount(
                    calculateAllocatedAmount(
                            budget,
                            budgetCategory));
        }

        // =====================================================
        // AMOUNT MODE
        // =====================================================

        else {

            BigDecimal amount =
                    request.getAllocatedAmount();

            validateAmountInput(amount);

            /*
             * 0 => xóa category
             */
            if (amount.compareTo(
                    BigDecimal.ZERO) == 0) {

                if (hasTransactionsInBudgetMonth(
                        budget,
                        category)) {

                    throw new RuntimeException(
                            "Cannot remove category because transactions exist");
                }

                budgetCategoryRepository.delete(
                        budgetCategory);

                return null;
            }

            BigDecimal totalWithoutCurrent =
                    calculateTotalAllocatedAmount(budget)
                            .subtract(
                                    getAmountOrZero(
                                            budgetCategory));

            BigDecimal newTotal =
                    totalWithoutCurrent.add(amount);

            BigDecimal expenseBudget =
                    calculateExpenseBudget(budget);

            if (newTotal.compareTo(
                    expenseBudget) > 0) {

                throw new RuntimeException(
                        "Total allocated amount exceeds expense budget");
            }

            budgetCategory.setAllocatedAmount(
                    amount);

            budgetCategory.setAllocationPercentage(
                    calculateAllocationPercentage(
                            budget,
                            amount));
        }

        budgetCategory.setUpdatedAt(
                LocalDateTime.now());

        budgetCategory =
                budgetCategoryRepository.save(
                        budgetCategory);

        return mapBudgetCategoryToResponse(
                budgetCategory);
    }

    // =========================================================
    // DELETE BUDGET CATEGORY
    // =========================================================

    @Transactional
    public void deleteBudgetCategory(
            Long budgetId,
            Long categoryId,
            User user) {

        Budget budget = getUserBudget(
                budgetId,
                user);

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found"));

        BudgetCategory budgetCategory =
                budgetCategoryRepository
                        .findByBudgetAndCategory(
                                budget,
                                category)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Budget category not found"));

        /*
         * Nếu category đã có transaction
         * trong tháng của Budget -> không được xóa.
         */
        if (hasTransactionsInBudgetMonth(
                budget,
                category)) {

            throw new RuntimeException(
                    "Cannot delete category because transactions exist in this budget month");
        }

        budgetCategoryRepository.delete(
                budgetCategory);
    }

    // =========================================================
    // RECALCULATE CATEGORY AMOUNTS
    // PERCENTAGE MODE
    // =========================================================

    private void recalculateBudgetCategories(
            Budget budget) {

        List<BudgetCategory> categories =
                budgetCategoryRepository
                        .findByBudget(budget);

        for (BudgetCategory category : categories) {

            BigDecimal amount =
                    calculateAllocatedAmount(
                            budget,
                            category);

            category.setAllocatedAmount(
                    amount);

            category.setUpdatedAt(
                    LocalDateTime.now());

            budgetCategoryRepository.save(
                    category);
        }
    }

    // =========================================================
    // UPDATE PERCENTAGES FROM AMOUNTS
    // AMOUNT MODE
    // =========================================================

    private void updatePercentagesFromAmounts(
            Budget budget) {

        List<BudgetCategory> categories =
                budgetCategoryRepository
                        .findByBudget(budget);

        for (BudgetCategory category : categories) {

            BigDecimal amount =
                    category.getAllocatedAmount();

            if (amount == null) {
                amount = BigDecimal.ZERO;
            }

            BigDecimal percentage =
                    calculateAllocationPercentage(
                            budget,
                            amount);

            category.setAllocationPercentage(
                    percentage);

            category.setUpdatedAt(
                    LocalDateTime.now());

            budgetCategoryRepository.save(
                    category);
        }
    }

    // =========================================================
    // CALCULATE EXPENSE BUDGET
    // =========================================================

    private BigDecimal calculateExpenseBudget(
            Budget budget) {

        BigDecimal totalBudget =
                budget.getTotalBudget() != null
                        ? budget.getTotalBudget()
                        : BigDecimal.ZERO;

        BigDecimal savingAmount =
                budget.getSavingAmount() != null
                        ? budget.getSavingAmount()
                        : BigDecimal.ZERO;

        BigDecimal rolloverAmount =
                budget.getRolloverAmount() != null
                        ? budget.getRolloverAmount()
                        : BigDecimal.ZERO;

        return totalBudget
                .add(rolloverAmount)
                .subtract(savingAmount);
    }

    // =========================================================
    // CALCULATE ALLOCATED AMOUNT
    // =========================================================

    private BigDecimal calculateAllocatedAmount(
            Budget budget,
            BudgetCategory budgetCategory) {

        if (budget.getAllocationMode()
                == AllocationMode.PERCENTAGE) {

            BigDecimal percentage =
                    budgetCategory
                            .getAllocationPercentage();

            if (percentage == null) {
                return BigDecimal.ZERO;
            }

            BigDecimal expenseBudget =
                    calculateExpenseBudget(budget);

            return expenseBudget
                    .multiply(percentage)
                    .divide(
                            BigDecimal.valueOf(100),
                            2,
                            RoundingMode.HALF_UP);
        }

        return budgetCategory.getAllocatedAmount()
                != null
                ? budgetCategory.getAllocatedAmount()
                : BigDecimal.ZERO;
    }

    // =========================================================
    // CALCULATE PERCENTAGE
    // =========================================================

    private BigDecimal calculateAllocationPercentage(
            Budget budget,
            BigDecimal amount) {

        BigDecimal expenseBudget =
                calculateExpenseBudget(budget);

        if (expenseBudget.compareTo(
                BigDecimal.ZERO) <= 0) {

            return BigDecimal.ZERO;
        }

        return amount
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        expenseBudget,
                        2,
                        RoundingMode.HALF_UP);
    }

    // =========================================================
    // CALCULATE TOTAL ALLOCATED AMOUNT
    // =========================================================

    private BigDecimal calculateTotalAllocatedAmount(
            Budget budget) {

        List<BudgetCategory> categories =
                budgetCategoryRepository
                        .findByBudget(budget);

        return categories.stream()
                .map(BudgetCategory::getAllocatedAmount)
                .filter(Objects::nonNull)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);
    }

    // =========================================================
    // CALCULATE TOTAL PERCENTAGE
    // =========================================================

    private BigDecimal calculateTotalPercentage(
            Budget budget) {

        List<BudgetCategory> categories =
                budgetCategoryRepository
                        .findByBudget(budget);

        return categories.stream()
                .map(BudgetCategory::getAllocationPercentage)
                .filter(Objects::nonNull)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);
    }

    // =========================================================
    // VALIDATE TOTAL PERCENTAGE
    // =========================================================

    public void validateTotalPercentage(
            Budget budget) {

        BigDecimal total =
                calculateTotalPercentage(budget);

        if (total.compareTo(
                BigDecimal.valueOf(100)) != 0) {

            throw new RuntimeException(
                    "Total allocation percentage must equal 100%");
        }
    }

    // =========================================================
    // VALIDATE TOTAL AMOUNT
    // =========================================================

    public void validateTotalAmount(
            Budget budget) {

        BigDecimal total =
                calculateTotalAllocatedAmount(
                        budget);

        BigDecimal expenseBudget =
                calculateExpenseBudget(budget);

        if (total.compareTo(expenseBudget) != 0) {

            throw new RuntimeException(
                    "Total allocated amount must equal expense budget");
        }
    }

    // =========================================================
    // CHECK TRANSACTION IN BUDGET MONTH
    // =========================================================

    private boolean hasTransactionsInBudgetMonth(
            Budget budget,
            Category category) {

        LocalDateTime start =
                LocalDateTime.of(
                        budget.getYear(),
                        budget.getMonth(),
                        1,
                        0,
                        0);

        LocalDateTime end =
                start.plusMonths(1);

        return transactionRepository
                .existsByUserAndCategory_CategoryIdAndCreatedAtBetween(
                        budget.getUser(),
                        category.getCategoryId(),
                        start,
                        end);
    }

    // =========================================================
    // VALIDATE TOTAL BUDGET
    // =========================================================

    private void validateTotalBudget(
            BigDecimal totalBudget) {

        if (totalBudget == null) {

            throw new RuntimeException(
                    "Total budget is required");
        }

        if (totalBudget.compareTo(
                BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Total budget must be greater than 0");
        }
    }

    // =========================================================
    // VALIDATE PERCENTAGE INPUT
    // =========================================================

    private void validatePercentageInput(
            BigDecimal percentage) {

        if (percentage == null) {

            throw new RuntimeException(
                    "Allocation percentage is required");
        }

        if (percentage.compareTo(
                BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Allocation percentage cannot be negative");
        }

        if (percentage.compareTo(
                BigDecimal.valueOf(100)) > 0) {

            throw new RuntimeException(
                    "Allocation percentage cannot exceed 100");
        }
    }

    // =========================================================
    // VALIDATE AMOUNT INPUT
    // =========================================================

    private void validateAmountInput(
            BigDecimal amount) {

        if (amount == null) {

            throw new RuntimeException(
                    "Allocated amount is required");
        }

        if (amount.compareTo(
                BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Allocated amount cannot be negative");
        }
    }

    // =========================================================
    // GET PERCENTAGE OR ZERO
    // =========================================================

    private BigDecimal getPercentageOrZero(
            BudgetCategory category) {

        return category.getAllocationPercentage()
                != null
                ? category.getAllocationPercentage()
                : BigDecimal.ZERO;
    }

    // =========================================================
    // GET AMOUNT OR ZERO
    // =========================================================

    private BigDecimal getAmountOrZero(
            BudgetCategory category) {

        return category.getAllocatedAmount()
                != null
                ? category.getAllocatedAmount()
                : BigDecimal.ZERO;
    }

    // =========================================================
    // GET USER BUDGET
    // =========================================================

    private Budget getUserBudget(
            Long budgetId,
            User user) {

        return budgetRepository
                .findById(budgetId)
                .filter(budget ->
                        budget.getUser()
                                .getUserId()
                                .equals(user.getUserId()))
                .orElseThrow(() ->
                        new RuntimeException(
                                "Budget not found"));
    }

    // =========================================================
    // MAP BUDGET -> RESPONSE
    // =========================================================

    private BudgetResponse mapToResponse(
            Budget budget) {

        BudgetResponse response =
                new BudgetResponse();

        response.setBudgetId(
                budget.getBudgetId());

        response.setTotalBudget(
                budget.getTotalBudget());

        response.setSavingAmount(
                budget.getSavingAmount());

        response.setRolloverAmount(
                budget.getRolloverAmount());

        response.setMonth(
                budget.getMonth());

        response.setYear(
                budget.getYear());

        response.setAllocationMode(
                budget.getAllocationMode());

        BigDecimal expenseBudget =
                calculateExpenseBudget(budget);

        response.setExpenseBudget(
                expenseBudget);

        BigDecimal allocatedAmount =
                calculateTotalAllocatedAmount(
                        budget);

        response.setAllocatedAmount(
                allocatedAmount);

        response.setRemainingAmount(
                expenseBudget.subtract(
                        allocatedAmount));

        List<BudgetCategory> categories =
                budgetCategoryRepository
                        .findByBudget(budget);

        response.setCategories(
                categories.stream()
                        .map(this::mapBudgetCategoryToResponse)
                        .toList());

        return response;
    }

    // =========================================================
    // MAP BUDGET CATEGORY -> RESPONSE
    // =========================================================

    private BudgetCategoryResponse
    mapBudgetCategoryToResponse(
            BudgetCategory budgetCategory) {

        BudgetCategoryResponse response =
                new BudgetCategoryResponse();

        response.setBudgetCategoryId(
                budgetCategory.getBudgetCategoryId());

        response.setCategoryId(
                budgetCategory.getCategory()
                        .getCategoryId());

        response.setCategoryName(
                budgetCategory.getCategory()
                        .getCategoryName());

        response.setAllocatedAmount(
                budgetCategory.getAllocatedAmount());

        response.setAllocationPercentage(
                budgetCategory.getAllocationPercentage());

        return response;
    }
}