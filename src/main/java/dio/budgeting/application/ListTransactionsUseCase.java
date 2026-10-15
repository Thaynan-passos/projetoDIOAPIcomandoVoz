package dio.budgeting.application;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ListTransactionsUseCase {

    private final TransactionRepository repository;

    public ListTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> execute(LocalDate from, LocalDate to) {
        Period period = Period.of(from, to);
        return repository.findBetween(period.from(), period.to());
    }
}
