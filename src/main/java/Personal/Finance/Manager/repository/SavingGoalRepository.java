package Personal.Finance.Manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Personal.Finance.Manager.model.SavingGoal;
import Personal.Finance.Manager.model.SavingStatus;
import Personal.Finance.Manager.model.User;

public interface SavingGoalRepository
        extends JpaRepository<SavingGoal, Long> {

    List<SavingGoal> findByUser(User user);

    List<SavingGoal> findByUserAndStatus(
            User user,
            SavingStatus status
    );

    Optional<SavingGoal> findBySavingGoalIdAndUser(
            Long savingGoalId,
            User user
    );
}