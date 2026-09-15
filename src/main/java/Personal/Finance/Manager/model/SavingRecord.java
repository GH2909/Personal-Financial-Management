package Personal.Finance.Manager.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "saving_records")
public class SavingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saving_record_id")
    private Long savingRecordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saving_goal_id", nullable = false)
    private SavingGoal savingGoal;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SavingRecordType type;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "description", length = 225)
    private String description;

    public SavingRecord() {
    }

    // getters/setters
}