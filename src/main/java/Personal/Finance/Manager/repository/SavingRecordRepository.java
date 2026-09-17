package Personal.Finance.Manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import Personal.Finance.Manager.model.SavingGoal;
import Personal.Finance.Manager.model.SavingRecord;

public interface SavingRecordRepository
        extends JpaRepository<SavingRecord, Long> {

    List<SavingRecord> findBySavingGoal(SavingGoal savingGoal);

    List<SavingRecord> findBySavingGoalOrderByCreatedAtDesc(
            SavingGoal savingGoal
    );
}