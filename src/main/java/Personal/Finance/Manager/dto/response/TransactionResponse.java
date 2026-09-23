package Personal.Finance.Manager.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import Personal.Finance.Manager.model.CategoryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResponse {

    private Long transactionId;

    private Long categoryId;

    private String categoryName;

    private CategoryType type;

    private BigDecimal spendingMoney;

    private String description;

    private LocalDateTime createdAt;
}