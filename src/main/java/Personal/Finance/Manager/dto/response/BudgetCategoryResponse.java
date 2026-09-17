package Personal.Finance.Manager.dto.response;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetCategoryResponse {

    private Long budgetCategoryId;

    private Long categoryId;

    private String categoryName;

    private BigDecimal allocatedAmount;

    private BigDecimal allocationPercentage;
}