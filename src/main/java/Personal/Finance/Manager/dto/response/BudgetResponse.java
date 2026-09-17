package Personal.Finance.Manager.dto.response;

import java.math.BigDecimal;
import java.util.List;

import Personal.Finance.Manager.model.AllocationMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetResponse {

    private Long budgetId;

    private BigDecimal totalBudget;

    private BigDecimal savingAmount;

    private BigDecimal rolloverAmount;

    private BigDecimal expenseBudget;

    private BigDecimal allocatedAmount;

    private BigDecimal remainingAmount;

    private Integer month;

    private Integer year;

    private AllocationMode allocationMode;

    private List<BudgetCategoryResponse> categories;
}