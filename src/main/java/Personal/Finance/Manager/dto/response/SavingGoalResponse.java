package Personal.Finance.Manager.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import Personal.Finance.Manager.model.GoalType;
import Personal.Finance.Manager.model.SavingMethod;
import Personal.Finance.Manager.model.SavingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingGoalResponse {

    private Long savingGoalId;
    private String name;

    private GoalType goalType;

    private BigDecimal targetAmount;
    private BigDecimal currentAmount;

    private SavingMethod savingMethod;
    private BigDecimal savingValue;

    private Integer durationMonths;

    private LocalDate startDate;
    private LocalDate targetDate;

    private SavingStatus status;
}