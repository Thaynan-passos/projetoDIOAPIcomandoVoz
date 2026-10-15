package dio.budgeting.infrastructure.ai;

import dio.budgeting.application.CreateTransactionUseCase;
import dio.budgeting.application.ListTransactionsUseCase;
import dio.budgeting.application.SummarizeSpendingUseCase;
import dio.budgeting.domain.BusinessRuleException;
import dio.budgeting.domain.TransactionType;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Ferramentas que o modelo de linguagem pode chamar (Tool Calling).
 * Elas só traduzem parâmetros e delegam para os casos de uso: a regra de
 * negócio continua na camada de aplicação/domínio.
 */
@Component
public class BudgetingTools {

    private final CreateTransactionUseCase createTransaction;
    private final ListTransactionsUseCase listTransactions;
    private final SummarizeSpendingUseCase summarizeSpending;

    public BudgetingTools(CreateTransactionUseCase createTransaction,
                          ListTransactionsUseCase listTransactions,
                          SummarizeSpendingUseCase summarizeSpending) {
        this.createTransaction = createTransaction;
        this.listTransactions = listTransactions;
        this.summarizeSpending = summarizeSpending;
    }

    @Tool(description = "Registra uma nova transação financeira: uma receita (INCOME) ou uma despesa (EXPENSE).")
    public Object createTransaction(
            @ToolParam(description = "INCOME para receita/entrada, EXPENSE para despesa/gasto") String type,
            @ToolParam(description = "Valor positivo, sem símbolo de moeda") BigDecimal amount,
            @ToolParam(description = "Descrição curta, ex: compras no mercado") String description,
            @ToolParam(description = "Categoria, ex: mercado, transporte, salário, lazer", required = false) String category,
            @ToolParam(description = "Data no formato yyyy-MM-dd. Se não informada, usa hoje", required = false) String date) {
        return safely(() -> createTransaction.execute(new CreateTransactionUseCase.Command(
                parseType(type), amount, description, category, parseDate(date))));
    }

    @Tool(description = "Lista as transações de um período. Sem datas, usa do início do mês atual até hoje.")
    public Object listTransactions(
            @ToolParam(description = "Data inicial yyyy-MM-dd", required = false) String from,
            @ToolParam(description = "Data final yyyy-MM-dd", required = false) String to) {
        return safely(() -> listTransactions.execute(parseDate(from), parseDate(to)));
    }

    @Tool(description = "Resume um período: total de receitas, total de gastos, saldo e gastos agrupados por categoria. "
            + "Use para perguntas como 'quanto gastei com mercado?' ou 'como está meu mês?'.")
    public Object summarizeSpending(
            @ToolParam(description = "Data inicial yyyy-MM-dd", required = false) String from,
            @ToolParam(description = "Data final yyyy-MM-dd", required = false) String to) {
        return safely(() -> summarizeSpending.execute(parseDate(from), parseDate(to)));
    }

    // Se algo falhar, devolvemos o erro ao modelo para ele explicar à pessoa usuária.
    private Object safely(java.util.function.Supplier<Object> action) {
        try {
            return action.get();
        } catch (BusinessRuleException e) {
            return Map.of("error", e.getMessage());
        }
    }

    private TransactionType parseType(String type) {
        try {
            return TransactionType.valueOf(type == null ? "" : type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Tipo inválido. Use INCOME ou EXPENSE.");
        }
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(date.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessRuleException("Data inválida: use o formato yyyy-MM-dd.");
        }
    }
}
