package dio.budgeting.application;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CreateTransactionUseCase {

    public record Command(TransactionType type, BigDecimal amount, String description,
                          String category, LocalDate date) {
    }

    private final TransactionRepository repository;

    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction execute(Command command) {
        // O construtor de Transaction valida as regras de negócio.
        Transaction transaction = new Transaction(null, command.type(), command.amount(),
                command.description(), command.category(), command.date());
        return repository.save(transaction);
    }
}
