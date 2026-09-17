package Personal.Finance.Manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

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
}