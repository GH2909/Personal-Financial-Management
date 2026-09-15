package Personal.Finance.Manager.model;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "saving_goals")
public class SavingGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saving_goal_id")
    private Long savingGoalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false)
    private GoalType goalType;

    @Column(name = "target_amount", precision = 15, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "saving_method", nullable = false)
    private SavingMethod savingMethod;

    @Column(name = "saving_value", precision = 15, scale = 2, nullable = false)
    private BigDecimal savingValue;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SavingStatus status = SavingStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SavingGoal() {
    }

}