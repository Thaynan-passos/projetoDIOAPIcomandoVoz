package dio.budgeting.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo de domínio. As validações ficam aqui (MELHORIA 1): uma transação
 * inválida simplesmente não pode existir, venha ela do REST ou da IA.
 */
public record Transaction(
        Long id,
        TransactionType type,
        BigDecimal amount,
        String description,
        String category,
        LocalDate date) {

    public Transaction {
        if (type == null) {
            throw new BusinessRuleException("O tipo da transação é obrigatório (INCOME ou EXPENSE).");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new BusinessRuleException("O valor deve ser maior que zero.");
        }
        if (description == null || description.isBlank()) {
            throw new BusinessRuleException("A descrição é obrigatória.");
        }
        description = description.trim();
        category = (category == null || category.isBlank()) ? "outros" : category.trim().toLowerCase();
        if (date == null) {
            date = LocalDate.now();
        }
        if (date.isAfter(LocalDate.now())) {
            throw new BusinessRuleException("A data não pode estar no futuro.");
        }
    }

    public Transaction withId(Long newId) {
        return new Transaction(newId, type, amount, description, category, date);
    }
}
