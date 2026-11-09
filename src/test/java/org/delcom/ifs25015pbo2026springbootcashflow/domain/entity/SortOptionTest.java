package org.delcom.ifs25015pbo2026springbootcashflow.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortOptionTest {

    private List<Transaction> sample() {
        return new ArrayList<>(List.of(
                new Transaction(1, "A", new BigDecimal("300.00"), TransactionType.EXPENSE),
                new Transaction(2, "B", new BigDecimal("100.00"), TransactionType.INCOME),
                new Transaction(3, "C", new BigDecimal("200.00"), TransactionType.EXPENSE),
                new Transaction(4, "D", new BigDecimal("400.00"), TransactionType.INCOME)));
    }

    private List<Integer> ids(SortOption option) {
        List<Transaction> list = sample();
        list.sort(option.comparator());
        return list.stream().map(Transaction::getId).toList();
    }

    @Test
    void amountAscending() {
        assertEquals(List.of(2, 3, 1, 4), ids(SortOption.AMOUNT_ASC));
    }

    @Test
    void amountDescending() {
        assertEquals(List.of(4, 1, 3, 2), ids(SortOption.AMOUNT_DESC));
    }

    @Test
    void incomeFirstKeepsRecordingOrderWithinGroup() {
        assertEquals(List.of(2, 4, 1, 3), ids(SortOption.INCOME_FIRST));
    }

    @Test
    void expenseFirstKeepsRecordingOrderWithinGroup() {
        assertEquals(List.of(1, 3, 2, 4), ids(SortOption.EXPENSE_FIRST));
    }

    @Test
    void hasFourOptions() {
        assertEquals(4, SortOption.values().length);
    }
}
