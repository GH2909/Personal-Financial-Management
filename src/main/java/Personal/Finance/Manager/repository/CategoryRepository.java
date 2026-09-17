package Personal.Finance.Manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import Personal.Finance.Manager.model.Category;
import Personal.Finance.Manager.model.CategoryType;
import Personal.Finance.Manager.model.User;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);

    List<Category> findByUserAndType(
            User user,
            CategoryType type
    );
}