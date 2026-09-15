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
import lombok.Data;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "transaction_name", length = 100, nullable = false)
    private String transactionName;

    @Column(name = "spending_money", precision = 15, scale = 2, nullable = false)
    private BigDecimal spendingMoney;

    @Column(name = "description", length = 225)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Transaction() {
    }

    public Transaction(
            String transactionName,
            BigDecimal spendingMoney,
            String description,
            LocalDateTime createdAt,
            Category category,
            User user
    ) {
        this.transactionName = transactionName;
        this.spendingMoney = spendingMoney;
        this.description = description;
        this.createdAt = createdAt;
        this.category = category;
        this.user = user;
    }

}