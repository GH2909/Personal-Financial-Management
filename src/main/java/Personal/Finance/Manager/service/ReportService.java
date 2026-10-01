package Personal.Finance.Manager.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Personal.Finance.Manager.dto.response.CategoryReportResponse;
import Personal.Finance.Manager.dto.response.DashboardReportResponse;
import Personal.Finance.Manager.dto.response.MonthlyReportResponse;
import Personal.Finance.Manager.dto.response.SavingReportResponse;
import Personal.Finance.Manager.model.Budget;
import Personal.Finance.Manager.model.CategoryType;
import Personal.Finance.Manager.model.SavingRecordType;
import Personal.Finance.Manager.model.SavingStatus;
import Personal.Finance.Manager.model.User;
import Personal.Finance.Manager.repository.BudgetRepository;
import Personal.Finance.Manager.repository.SavingGoalRepository;
import Personal.Finance.Manager.repository.SavingRecordRepository;
import Personal.Finance.Manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final SavingGoalRepository savingGoalRepository;
    private final SavingRecordRepository savingRecordRepository;


    @Transactional(readOnly = true)
    public DashboardReportResponse getDashboardReport(User user) {

        if (user == null) {
            throw new RuntimeException("User is required");
        }

        LocalDate today = LocalDate.now();

        LocalDateTime start = today
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime end = start.plusMonths(1);

        BigDecimal totalIncome =
                transactionRepository
                        .sumSpendingMoneyByUserAndTypeAndDateRange(
                                user,
                                CategoryType.INCOME,
                                start,
                                end
                        );

        BigDecimal totalExpense =
                transactionRepository
                        .sumSpendingMoneyByUserAndTypeAndDateRange(
                                user,
                                CategoryType.EXPENSE,
                                start,
                                end
                        );

        BigDecimal totalSavingDeposit =
                savingRecordRepository.sumSavingAmount(
                        user,
                        SavingRecordType.DEPOSIT,
                        start,
                        end
                );

        BigDecimal totalSavingWithdraw =
                savingRecordRepository.sumSavingAmount(
                        user,
                        SavingRecordType.WITHDRAW,
                        start,
                        end
                );

        BigDecimal availableMoney =
                totalIncome
                        .subtract(totalExpense)
                        .subtract(totalSavingDeposit)
                        .add(totalSavingWithdraw);

        DashboardReportResponse response =
                new DashboardReportResponse();

        response.setTotalIncome(totalIncome);
        response.setTotalExpense(totalExpense);
        response.setTotalSavingDeposit(totalSavingDeposit);
        response.setTotalSavingWithdraw(totalSavingWithdraw);
        response.setAvailableMoney(availableMoney);

        Budget budget = budgetRepository
                .findByUserAndYearAndMonth(
                        user,
                        today.getYear(),
                        today.getMonthValue()
                )
                .orElse(null);

        if (budget != null) {

            BigDecimal expenseBudget =
                    budget.getTotalBudget()
                            .add(budget.getRolloverAmount())
                            .subtract(budget.getSavingAmount());

            BigDecimal remainingBudget =
                    expenseBudget.subtract(totalExpense);

            BigDecimal usagePercentage = BigDecimal.ZERO;

            if (expenseBudget.compareTo(BigDecimal.ZERO) > 0) {
                usagePercentage =
                        totalExpense
                                .multiply(BigDecimal.valueOf(100))
                                .divide(
                                        expenseBudget,
                                        2,
                                        RoundingMode.HALF_UP
                                );
            }

            response.setTotalBudget(budget.getTotalBudget());
            response.setExpenseBudget(expenseBudget);
            response.setTotalBudgetUsed(totalExpense);
            response.setRemainingBudget(remainingBudget);
            response.setBudgetUsagePercentage(usagePercentage);
        } else {

            response.setTotalBudget(BigDecimal.ZERO);
            response.setExpenseBudget(BigDecimal.ZERO);
            response.setTotalBudgetUsed(totalExpense);
            response.setRemainingBudget(BigDecimal.ZERO);
            response.setBudgetUsagePercentage(BigDecimal.ZERO);
        }

        return response;
    }


    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(
            Integer month,
            Integer year,
            User user
    ) {

        if (user == null) {
            throw new RuntimeException("User is required");
        }

        if (month == null || month < 1 || month > 12) {
            throw new RuntimeException("Month must be between 1 and 12");
        }

        if (year == null || year < 2000) {
            throw new RuntimeException("Invalid year");
        }

        LocalDateTime start =
                LocalDate.of(year, month, 1)
                        .atStartOfDay();

        LocalDateTime end = start.plusMonths(1);

        BigDecimal income =
                transactionRepository
                        .sumSpendingMoneyByUserAndTypeAndDateRange(
                                user,
                                CategoryType.INCOME,
                                start,
                                end
                        );

        BigDecimal expense =
                transactionRepository
                        .sumSpendingMoneyByUserAndTypeAndDateRange(
                                user,
                                CategoryType.EXPENSE,
                                start,
                                end
                        );

        BigDecimal deposit =
                savingRecordRepository.sumSavingAmount(
                        user,
                        SavingRecordType.DEPOSIT,
                        start,
                        end
                );

        BigDecimal withdraw =
                savingRecordRepository.sumSavingAmount(
                        user,
                        SavingRecordType.WITHDRAW,
                        start,
                        end
                );

        BigDecimal available =
                income
                        .subtract(expense)
                        .subtract(deposit)
                        .add(withdraw);

        MonthlyReportResponse response =
                new MonthlyReportResponse();

        response.setMonth(month);
        response.setYear(year);

        response.setTotalIncome(income);
        response.setTotalExpense(expense);

        response.setTotalSavingDeposit(deposit);
        response.setTotalSavingWithdraw(withdraw);

        response.setAvailableMoney(available);

        return response;
    }


    @Transactional(readOnly = true)
    public List<CategoryReportResponse> getCategoryReport(
            Integer month,
            Integer year,
            User user
    ) {

        if (user == null) {
            throw new RuntimeException("User is required");
        }

        LocalDateTime start =
                LocalDate.of(year, month, 1)
                        .atStartOfDay();

        LocalDateTime end = start.plusMonths(1);

        BigDecimal totalExpense =
                transactionRepository
                        .sumSpendingMoneyByUserAndTypeAndDateRange(
                                user,
                                CategoryType.EXPENSE,
                                start,
                                end
                        );

        List<Object[]> results =
                transactionRepository.sumByCategory(
                        user,
                        CategoryType.EXPENSE,
                        start,
                        end
                );

        return results.stream()
                .map(row -> {

                    Long categoryId = ((Number) row[0]).longValue();

                    String categoryName = (String) row[1];

                    BigDecimal amount = (BigDecimal) row[2];

                    BigDecimal percentage = BigDecimal.ZERO;

                    if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {

                        percentage =
                                amount
                                        .multiply(BigDecimal.valueOf(100))
                                        .divide(
                                                totalExpense,
                                                2,
                                                RoundingMode.HALF_UP
                                        );
                    }

                    CategoryReportResponse response =
                            new CategoryReportResponse();

                    response.setCategoryId(categoryId);
                    response.setCategoryName(categoryName);
                    response.setTotalAmount(amount);
                    response.setPercentage(percentage);

                    return response;
                })
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public SavingReportResponse getSavingReport(User user) {

        if (user == null) {
            throw new RuntimeException("User is required");
        }

        LocalDateTime start =
                LocalDate.now()
                        .withDayOfMonth(1)
                        .atStartOfDay();

        LocalDateTime end = start.plusMonths(1);

        BigDecimal deposit =
                savingRecordRepository.sumSavingAmount(
                        user,
                        SavingRecordType.DEPOSIT,
                        start,
                        end
                );

        BigDecimal withdraw =
                savingRecordRepository.sumSavingAmount(
                        user,
                        SavingRecordType.WITHDRAW,
                        start,
                        end
                );

        BigDecimal currentSaving =
                savingGoalRepository
                        .findByUser(user)
                        .stream()
                        .map(goal -> goal.getCurrentAmount())
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        long activeGoals =
                savingGoalRepository.countByUserAndStatus(
                        user,
                        SavingStatus.ACTIVE
                );

        long completedGoals =
                savingGoalRepository.countByUserAndStatus(
                        user,
                        SavingStatus.COMPLETED
                );

        SavingReportResponse response =
                new SavingReportResponse();

        response.setTotalSavingDeposit(deposit);
        response.setTotalSavingWithdraw(withdraw);
        response.setCurrentSavingAmount(currentSaving);
        response.setActiveGoals((int) activeGoals);
        response.setCompletedGoals((int) completedGoals);

        return response;
    }
}