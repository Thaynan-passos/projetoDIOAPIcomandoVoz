package dio.budgeting.application;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** MELHORIA 2: resumo do período com gastos agrupados por categoria. */
@Service
public class SummarizeSpendingUseCase {

    public record CategoryTotal(String category, BigDecimal total) {
    }

    public record Summary(LocalDate from, LocalDate to, BigDecimal totalIncome,
                          BigDecimal totalExpenses, BigDecimal balance,
                          List<CategoryTotal> expensesByCategory) {
    }

    private final TransactionRepository repository;

    public SummarizeSpendingUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public Summary execute(LocalDate from, LocalDate to) {
        Period period = Period.of(from, to);
        List<Transaction> transactions = repository.findBetween(period.from(), period.to());

        BigDecimal income = sum(transactions, TransactionType.INCOME);
        BigDecimal expenses = sum(transactions, TransactionType.EXPENSE);

        Map<String, BigDecimal> byCategory = transactions.stream()
                .filter(t -> t.type() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(Transaction::category,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::amount, BigDecimal::add)));

        List<CategoryTotal> categories = byCategory.entrySet().stream()
                .map(e -> new CategoryTotal(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(CategoryTotal::total).reversed())
                .toList();

        return new Summary(period.from(), period.to(), income, expenses,
                income.subtract(expenses), categories);
    }

    private BigDecimal sum(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.type() == type)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
