package Personal.Finance.Manager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Personal.Finance.Manager.dto.response.RolloverResponse;
import Personal.Finance.Manager.model.Budget;
import Personal.Finance.Manager.model.CategoryType;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.repository.BudgetRepository;
import Personal.Finance.Manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolloverService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Calculate rollover from one month
     * and apply it to the next month.
     *
     * Formula:
     *
     * Expense Budget
     * - Total Expense
     * = Rollover
     *
     * Example:
     *
     * Expense Budget = 16,000,000
     * Total Expense  = 14,300,000
     *
     * Rollover = +1,700,000
     */
    @Transactional
    public RolloverResponse calculateAndApplyRollover(
            Integer year,
            Integer month,
            User user) {

        if (user == null) {
            throw new RuntimeException("User is required");
        }

        if (year == null || month == null) {
            throw new RuntimeException("Year and month are required");
        }

        if (month < 1 || month > 12) {
            throw new RuntimeException("Month must be between 1 and 12");
        }

        // =========================================================
        // 1. Find current month's Budget
        // =========================================================

        Budget currentBudget = budgetRepository
                .findByUserAndYearAndMonth(user, year, month)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Budget not found for "
                                + month + "/" + year
                        )
                );

        // =========================================================
        // 2. Calculate Expense Budget
        // =========================================================
        //
        // Expense Budget
        // = Total Budget
        // + Rollover
        // - Saving Amount
        //
        // Rollover can be positive or negative.
        // =========================================================

        BigDecimal totalBudget =
                getOrZero(currentBudget.getTotalBudget());

        BigDecimal currentRollover =
                getOrZero(currentBudget.getRolloverAmount());

        BigDecimal savingAmount =
                getOrZero(currentBudget.getSavingAmount());

        BigDecimal expenseBudget =
                totalBudget
                        .add(currentRollover)
                        .subtract(savingAmount);

        // =========================================================
        // 3. Get start and end date of current month
        // =========================================================

        LocalDate firstDay =
                LocalDate.of(year, month, 1);

        LocalDateTime start =
                firstDay.atStartOfDay();

        LocalDateTime end =
                firstDay
                        .plusMonths(1)
                        .atStartOfDay();

        // =========================================================
        // 4. Calculate total actual EXPENSE
        // =========================================================
        //
        // Only transactions whose Category.type = EXPENSE
        // are counted.
        // =========================================================

        BigDecimal totalExpense =
                transactionRepository
                        .sumSpendingMoneyByUserAndTypeAndDateRange(
                                user,
                                CategoryType.EXPENSE,
                                start,
                                end
                        );

        totalExpense = getOrZero(totalExpense);

        // =========================================================
        // 5. Calculate Rollover
        // =========================================================
        //
        // Rollover
        // = Expense Budget - Total Expense
        //
        // Positive:
        //   user spent less than budget
        //
        // Negative:
        //   user exceeded budget
        // =========================================================

        BigDecimal rolloverAmount =
                expenseBudget.subtract(totalExpense);

        // =========================================================
        // 6. Find next month
        // =========================================================

        LocalDate nextMonthDate =
                firstDay.plusMonths(1);

        Integer nextMonth =
                nextMonthDate.getMonthValue();

        Integer nextYear =
                nextMonthDate.getYear();

        // =========================================================
        // 7. Find next month's Budget
        // =========================================================

        Budget nextBudget = budgetRepository
                .findByUserAndYearAndMonth(
                        user,
                        nextYear,
                        nextMonth
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Next month's budget not found for "
                                + nextMonth + "/" + nextYear
                        )
                );

        // =========================================================
        // 8. Apply rollover to next month's Budget
        // =========================================================
        //
        // IMPORTANT:
        // Set directly instead of adding.
        //
        // This makes the operation idempotent.
        //
        // If API is called twice:
        //
        // First:
        //   rollover = 1,700,000
        //
        // Second:
        //   rollover = 1,700,000
        //
        // NOT:
        //   3,400,000
        // =========================================================

        nextBudget.setRolloverAmount(rolloverAmount);

        budgetRepository.save(nextBudget);

        // =========================================================
        // 9. Calculate next month's effective budget
        // =========================================================
        //
        // Effective Budget:
        // = Next Total Budget + Rollover
        //
        // Note:
        // This is before deducting next month's saving.
        //
        // Expense Budget of next month will be:
        //
        // = Total Budget
        // + Rollover
        // - Saving Amount
        // =========================================================

        BigDecimal nextTotalBudget =
                getOrZero(nextBudget.getTotalBudget());

        BigDecimal nextEffectiveBudget =
                nextTotalBudget.add(rolloverAmount);

        // =========================================================
        // 10. Build response
        // =========================================================

        RolloverResponse response =
                new RolloverResponse();

        response.setSourceMonth(month);
        response.setSourceYear(year);

        response.setTargetMonth(nextMonth);
        response.setTargetYear(nextYear);

        response.setExpenseBudget(expenseBudget);

        response.setTotalExpense(totalExpense);

        response.setRolloverAmount(rolloverAmount);

        response.setNextEffectiveBudget(nextEffectiveBudget);

        return response;
    }

    /**
     * Get value or ZERO when value is null.
     */
    private BigDecimal getOrZero(BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}