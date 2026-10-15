package dio.budgeting.infrastructure.web;

import dio.budgeting.application.CreateTransactionUseCase;
import dio.budgeting.application.ListTransactionsUseCase;
import dio.budgeting.application.SummarizeSpendingUseCase;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    public record CreateRequest(TransactionType type, BigDecimal amount, String description,
                                String category, LocalDate date) {
    }

    private final CreateTransactionUseCase create;
    private final ListTransactionsUseCase list;
    private final SummarizeSpendingUseCase summarize;

    public TransactionController(CreateTransactionUseCase create, ListTransactionsUseCase list,
                                 SummarizeSpendingUseCase summarize) {
        this.create = create;
        this.list = list;
        this.summarize = summarize;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction create(@RequestBody CreateRequest request) {
        return create.execute(new CreateTransactionUseCase.Command(request.type(), request.amount(),
                request.description(), request.category(), request.date()));
    }

    @GetMapping
    public List<Transaction> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return list.execute(from, to);
    }

    @GetMapping("/summary")
    public SummarizeSpendingUseCase.Summary summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return summarize.execute(from, to);
    }
}
