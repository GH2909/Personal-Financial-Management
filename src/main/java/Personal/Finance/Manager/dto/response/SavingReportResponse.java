package Personal.Finance.Manager.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingReportResponse {

    private BigDecimal totalSavingDeposit;
    private BigDecimal totalSavingWithdraw;

    private BigDecimal currentSavingAmount;

    private Integer activeGoals;
    private Integer completedGoals;
}