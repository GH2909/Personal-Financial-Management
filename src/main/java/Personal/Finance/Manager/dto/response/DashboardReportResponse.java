package Personal.Finance.Manager.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardReportResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

    private BigDecimal totalSavingDeposit;
    private BigDecimal totalSavingWithdraw;

    private BigDecimal availableMoney;

    private BigDecimal totalBudget;
    private BigDecimal expenseBudget;
    private BigDecimal totalBudgetUsed;
    private BigDecimal remainingBudget;

    private BigDecimal budgetUsagePercentage;
}