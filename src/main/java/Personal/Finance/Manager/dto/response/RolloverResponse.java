package Personal.Finance.Manager.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolloverResponse {

    private Integer sourceMonth;
    private Integer sourceYear;

    private Integer targetMonth;
    private Integer targetYear;

    private BigDecimal expenseBudget;

    private BigDecimal totalExpense;

    private BigDecimal rolloverAmount;

    private BigDecimal nextEffectiveBudget;
}