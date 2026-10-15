package dio.budgeting;

import dio.budgeting.application.SummarizeSpendingUseCase;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SummarizeSpendingUseCaseTest {

    /** Repositório falso em memória: o teste não precisa de banco nem de IA. */
    static class InMemoryRepository implements TransactionRepository {
        private final List<Transaction> data = new ArrayList<>();

        @Override
        public Transaction save(Transaction t) {
            Transaction saved = t.withId((long) data.size() + 1);
            data.add(saved);
            return saved;
        }

        @Override
        public List<Transaction> findBetween(LocalDate from, LocalDate to) {
            return data.stream().filter(t -> !t.date().isBefore(from) && !t.date().isAfter(to)).toList();
        }
    }

    @Test
    void groupsExpensesByCategoryAndComputesBalance() {
        InMemoryRepository repo = new InMemoryRepository();
        LocalDate today = LocalDate.now();
        repo.save(new Transaction(null, TransactionType.INCOME, new BigDecimal("1000"), "salário", "salário", today));
        repo.save(new Transaction(null, TransactionType.EXPENSE, new BigDecimal("50"), "feira", "mercado", today));
        repo.save(new Transaction(null, TransactionType.EXPENSE, new BigDecimal("30"), "padaria", "mercado", today));
        repo.save(new Transaction(null, TransactionType.EXPENSE, new BigDecimal("20"), "ônibus", "transporte", today));

        var summary = new SummarizeSpendingUseCase(repo).execute(today, today);

        assertEquals(new BigDecimal("100"), summary.totalExpenses());
        assertEquals(new BigDecimal("900"), summary.balance());
        assertEquals("mercado", summary.expensesByCategory().get(0).category());
        assertEquals(new BigDecimal("80"), summary.expensesByCategory().get(0).total());
    }
}
