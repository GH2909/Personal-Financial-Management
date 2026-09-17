package Personal.Finance.Manager.dto.request;

import java.math.BigDecimal;

import Personal.Finance.Manager.model.GoalType;
import Personal.Finance.Manager.model.SavingMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingGoalRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Goal type is required")
    private GoalType goalType;

    private BigDecimal targetAmount;

    private Integer durationMonths;

    @NotNull(message = "Saving method is required")
    private SavingMethod savingMethod;

    private BigDecimal savingValue;
}