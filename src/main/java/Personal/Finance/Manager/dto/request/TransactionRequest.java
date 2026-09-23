package Personal.Finance.Manager.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {

    @NotBlank(message = "Transaction name is required")
    @Size(max = 100, message = "Transaction name must not exceed 100 characters")
    private String transactionName;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Spending money is required")
    @DecimalMin(
        value = "0.01",
        message = "Spending money must be greater than 0"
    )
    private BigDecimal spendingMoney;

    @Size(max = 225, message = "Description must not exceed 225 characters")
    private String description;
}