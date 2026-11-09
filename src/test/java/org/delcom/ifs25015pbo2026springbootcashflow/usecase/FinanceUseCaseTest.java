package org.delcom.ifs25015pbo2026springbootcashflow.usecase;

import org.delcom.ifs25015pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinanceUseCaseTest {

    private FinanceUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FinanceUseCase(new TransactionRepository());
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual), "diharapkan " + expected + " tetapi " + actual);
    }

    @Test
    void incomeIncreasesAndExpenseDecreasesBalance() throws Exception {
        useCase.addIncome("Gaji", "5000000");
        useCase.addExpense("Belanja", "25000.50");
        assertMoney("4974999.50", useCase.getBalance());
        assertMoney("5000000", useCase.getTotalIncome());
        assertMoney("25000.50", useCase.getTotalExpense());
    }

    @Test
    void balanceCanBeNegativeAndIsConsistentAfterDelete() throws Exception {
        Transaction expense = useCase.addExpense("Sewa", "100");
        assertMoney("-100", useCase.getBalance());
        useCase.removeTransaction(expense.getId());
        assertMoney("0", useCase.getBalance());
    }

    @Test
    void descriptionIsTrimmedAndAmountScaledToTwoDecimals() throws Exception {
        Transaction t = useCase.addIncome("  Bonus  ", "10.005");
        assertEquals("Bonus", t.getDescription());
        assertEquals("10.01", t.getAmount().toPlainString());
    }

    @Test
    void emptyDescriptionIsRejected() {
        assertThrows(ValidationException.class, () -> useCase.addIncome("   ", "100"));
        assertThrows(ValidationException.class, () -> useCase.addExpense(null, "100"));
    }

    @Test
    void tooLongDescriptionIsRejected() {
        assertThrows(ValidationException.class, () -> useCase.addIncome("x".repeat(101), "100"));
    }

    @Test
    void invalidAmountsAreRejected() {
        for (String bad : new String[]{null, "", "  ", "abc", "12,5", "NaN", "0", "-5", "0.001",
                "1000000000000", "1e999"}) {
            assertThrows(ValidationException.class, () -> useCase.addExpense("Tes", bad), "input: " + bad);
        }
        assertTrue(useCase.getAllTransactions().isEmpty());
    }

    @Test
    void removeValidatesIdAndExistence() throws Exception {
        assertThrows(ValidationException.class, () -> useCase.removeTransaction(0));
        assertThrows(ValidationException.class, () -> useCase.removeTransaction(-3));
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> useCase.removeTransaction(99));
        assertEquals(99, ex.getId());
        Transaction t = useCase.addIncome("Ada", "1");
        useCase.removeTransaction(t.getId());
        assertTrue(useCase.getAllTransactions().isEmpty());
    }

    @Test
    void searchIsCaseInsensitiveOnDescriptionAndDoesNotMutate() throws Exception {
        useCase.addIncome("Gaji Bulanan", "100");
        useCase.addExpense("Beli GAJI-an", "10");
        useCase.addExpense("Makan", "5");
        assertEquals(2, useCase.searchTransactions("gaji").size());
        assertEquals(0, useCase.searchTransactions("zzz").size());
        assertEquals(3, useCase.searchTransactions("").size());
        assertEquals(3, useCase.searchTransactions(null).size());
        assertEquals(3, useCase.getAllTransactions().size());
    }

    @Test
    void sortAndListUseSortOption() throws Exception {
        useCase.addExpense("A", "300");
        useCase.addIncome("B", "100");
        useCase.addExpense("C", "200");
        assertEquals(List.of(2, 3, 1), useCase.sortTransactions(SortOption.AMOUNT_ASC)
                .stream().map(Transaction::getId).toList());
        assertEquals(List.of(2, 1, 3), useCase.sortTransactions(SortOption.INCOME_FIRST)
                .stream().map(Transaction::getId).toList());
        assertEquals(List.of(3, 1), useCase.listTransactions("", SortOption.AMOUNT_ASC).stream()
                .filter(Transaction::isExpense).map(Transaction::getId).toList());
        assertEquals(List.of(1, 3), useCase.listTransactions("", null).stream()
                .filter(Transaction::isExpense).map(Transaction::getId).toList());
        assertEquals(1, useCase.listTransactions("b", SortOption.AMOUNT_DESC).size());
    }
}
