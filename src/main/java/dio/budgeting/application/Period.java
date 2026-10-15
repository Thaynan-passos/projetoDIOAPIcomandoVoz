package dio.budgeting.application;

import dio.budgeting.domain.BusinessRuleException;

import java.time.LocalDate;

/** Período de consulta. Padrão: do primeiro dia do mês atual até hoje. */
public record Period(LocalDate from, LocalDate to) {

    public static Period of(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDate start = from != null ? from : today.withDayOfMonth(1);
        LocalDate end = to != null ? to : today;
        if (start.isAfter(end)) {
            throw new BusinessRuleException("A data inicial não pode ser depois da data final.");
        }
        return new Period(start, end);
    }
}
