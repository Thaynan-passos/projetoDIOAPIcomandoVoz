package dio.budgeting.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByDateBetweenOrderByDateDesc(LocalDate from, LocalDate to);
}
