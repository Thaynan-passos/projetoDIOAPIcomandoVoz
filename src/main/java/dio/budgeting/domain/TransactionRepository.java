package dio.budgeting.domain;

import java.time.LocalDate;
import java.util.List;

/** Contrato do repositório: o domínio não sabe nada sobre JPA. */
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findBetween(LocalDate from, LocalDate to);
}
