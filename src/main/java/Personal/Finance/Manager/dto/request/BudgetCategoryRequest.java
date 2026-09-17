package Personal.Finance.Manager.dto.request;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetCategoryRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private BigDecimal allocatedAmount;

    private BigDecimal allocationPercentage;
}