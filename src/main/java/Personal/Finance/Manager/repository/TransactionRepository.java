package Personal.Finance.Manager.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Personal.Finance.Manager.model.CategoryType;
import Personal.Finance.Manager.model.Transaction;
import Personal.Finance.Manager.model.User;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);

    List<Transaction> findByUserOrderByCreatedAtDesc(User user);

    List<Transaction> findByCategory_CategoryId(Long categoryId);

    List<Transaction> findByUserAndCategory_CategoryId(
            User user,
            Long categoryId
    );

    boolean existsByUserAndCategory_CategoryIdAndCreatedAtBetween(
            User user,
            Long categoryId,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Calculate total expense of a user
     * in a specific date range.
     *
     * Category.type is used to determine
     * whether the transaction is INCOME or EXPENSE.
     */
    @Query("""
        SELECT COALESCE(SUM(t.spendingMoney), 0)
        FROM Transaction t
        WHERE t.user = :user
          AND t.category.type = :type
          AND t.createdAt >= :start
          AND t.createdAt < :end
    """)
    BigDecimal sumSpendingMoneyByUserAndTypeAndDateRange(
            @Param("user") User user,
            @Param("type") CategoryType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT t.category.categoryId,
               t.category.categoryName,
               COALESCE(SUM(t.spendingMoney), 0)
        FROM Transaction t
        WHERE t.user = :user
          AND t.category.type = :type
          AND t.createdAt >= :start
          AND t.createdAt < :end
        GROUP BY t.category.categoryId, t.category.categoryName
        ORDER BY SUM(t.spendingMoney) DESC
    """)
    List<Object[]> sumByCategory(
            @Param("user") User user,
            @Param("type") CategoryType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}