package dio.budgeting.infrastructure.persistence;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal amount;
    private String description;
    private String category;

    @Column(name = "transaction_date")
    private LocalDate date;

    protected TransactionEntity() {
    }

    static TransactionEntity fromDomain(Transaction t) {
        TransactionEntity e = new TransactionEntity();
        e.id = t.id();
        e.type = t.type();
        e.amount = t.amount();
        e.description = t.description();
        e.category = t.category();
        e.date = t.date();
        return e;
    }

    Transaction toDomain() {
        return new Transaction(id, type, amount, description, category, date);
    }
}
