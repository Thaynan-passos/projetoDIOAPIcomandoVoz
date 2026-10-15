package dio.budgeting.domain;

/** Violação de uma regra de negócio (valor inválido, período inválido etc.). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
