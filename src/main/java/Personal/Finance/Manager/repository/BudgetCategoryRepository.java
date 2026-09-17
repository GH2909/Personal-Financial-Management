package Personal.Finance.Manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Personal.Finance.Manager.model.Budget;
import Personal.Finance.Manager.model.BudgetCategory;
import Personal.Finance.Manager.model.Category;

public interface BudgetCategoryRepository
        extends JpaRepository<BudgetCategory, Long> {

    List<BudgetCategory> findByBudget(Budget budget);

    Optional<BudgetCategory> findByBudgetAndCategory(
            Budget budget,
            Category category
    );
}