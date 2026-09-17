package Personal.Finance.Manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Personal.Finance.Manager.model.Budget;
import Personal.Finance.Manager.model.User;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUser(User user);

    Optional<Budget> findByUserAndYearAndMonth(
            User user,
            Integer year,
            Integer month
    );

    // Check xem tháng đó đã tồn tại trong năm chưa
    boolean existsByUserAndYearAndMonth(
            User user,
            Integer year,
            Integer month
    );
}