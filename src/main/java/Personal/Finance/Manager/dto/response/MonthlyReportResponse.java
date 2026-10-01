package Personal.Finance.Manager.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyReportResponse {

    private Integer month;
    private Integer year;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

    private BigDecimal totalSavingDeposit;
    private BigDecimal totalSavingWithdraw;

    private BigDecimal availableMoney;
}