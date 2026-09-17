package Personal.Finance.Manager.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import Personal.Finance.Manager.model.SavingRecordType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingRecordResponse {

    private Long savingRecordId;

    private BigDecimal amount;

    private SavingRecordType type;

    private LocalDateTime createdAt;

    private String description;
}