package Personal.Finance.Manager.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Personal.Finance.Manager.model.SavingGoal;
import Personal.Finance.Manager.model.SavingRecord;
import Personal.Finance.Manager.model.SavingRecordType;
import Personal.Finance.Manager.model.User;

public interface SavingRecordRepository
        extends JpaRepository<SavingRecord, Long> {

    List<SavingRecord> findBySavingGoal(SavingGoal savingGoal);

    List<SavingRecord> findBySavingGoalOrderByCreatedAtDesc(
            SavingGoal savingGoal
    );

    @Query("""
        SELECT COALESCE(SUM(r.amount), 0)
        FROM SavingRecord r
        WHERE r.savingGoal.user = :user
          AND r.type = :type
          AND r.createdAt >= :start
          AND r.createdAt < :end
    """)
    BigDecimal sumSavingAmount(
            @Param("user") User user,
            @Param("type") SavingRecordType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}