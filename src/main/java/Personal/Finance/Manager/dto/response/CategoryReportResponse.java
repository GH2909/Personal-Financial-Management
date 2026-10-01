package Personal.Finance.Manager.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryReportResponse {

    private Long categoryId;
    private String categoryName;

    private BigDecimal totalAmount;

    private BigDecimal percentage;
}