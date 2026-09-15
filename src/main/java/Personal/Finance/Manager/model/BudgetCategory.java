package Personal.Finance.Manager.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(
    name = "budget_categories",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_budget_category",
            columnNames = {"budget_id", "category_id"}
        )
    }
)
public class BudgetCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_category_id")
    private Long budgetCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(
        name = "allocated_amount",
        precision = 15,
        scale = 2,
        nullable = false
    )
    private BigDecimal allocatedAmount = BigDecimal.ZERO;

    @Column(
        name = "allocation_percentage",
        precision = 5,
        scale = 2
    )
    private BigDecimal allocationPercentage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public BudgetCategory() {
    }

}
