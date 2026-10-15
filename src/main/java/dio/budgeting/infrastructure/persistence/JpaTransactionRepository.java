package dio.budgeting.infrastructure.persistence;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/** Adaptador: implementa o contrato do domínio usando Spring Data JPA. */
@Repository
class JpaTransactionRepository implements TransactionRepository {

    private final SpringDataTransactionRepository jpa;

    JpaTransactionRepository(SpringDataTransactionRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return jpa.save(TransactionEntity.fromDomain(transaction)).toDomain();
    }

    @Override
    public List<Transaction> findBetween(LocalDate from, LocalDate to) {
        return jpa.findByDateBetweenOrderByDateDesc(from, to).stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
