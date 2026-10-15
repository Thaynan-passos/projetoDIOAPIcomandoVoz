package dio.budgeting;

import dio.budgeting.domain.BusinessRuleException;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void rejectsZeroOrNegativeAmount() {
        assertThrows(BusinessRuleException.class, () ->
                new Transaction(null, TransactionType.EXPENSE, BigDecimal.ZERO, "mercado", "mercado", null));
        assertThrows(BusinessRuleException.class, () ->
                new Transaction(null, TransactionType.EXPENSE, new BigDecimal("-5"), "mercado", "mercado", null));
    }

    @Test
    void rejectsBlankDescription() {
        assertThrows(BusinessRuleException.class, () ->
                new Transaction(null, TransactionType.EXPENSE, BigDecimal.TEN, "  ", "mercado", null));
    }

    @Test
    void rejectsFutureDate() {
        assertThrows(BusinessRuleException.class, () ->
                new Transaction(null, TransactionType.EXPENSE, BigDecimal.TEN, "cinema", "lazer",
                        LocalDate.now().plusDays(1)));
    }

    @Test
    void appliesDefaults() {
        Transaction t = new Transaction(null, TransactionType.INCOME, BigDecimal.TEN, " freela ", null, null);
        assertEquals("freela", t.description());
        assertEquals("outros", t.category());
        assertEquals(LocalDate.now(), t.date());
    }
}
