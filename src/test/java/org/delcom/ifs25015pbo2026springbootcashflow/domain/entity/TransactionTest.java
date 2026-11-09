package org.delcom.ifs25015pbo2026springbootcashflow.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest {

    @Test
    void incomeHasPositiveSignedAmount() {
        Transaction t = new Transaction(1, "Gaji", new BigDecimal("1000.00"), TransactionType.INCOME);
        assertTrue(t.isIncome());
        assertFalse(t.isExpense());
        assertEquals(new BigDecimal("1000.00"), t.signedAmount());
        assertNotNull(t.getCreatedAt());
    }

    @Test
    void expenseHasNegativeSignedAmount() {
        Transaction t = new Transaction(2, "Makan", new BigDecimal("25.50"), TransactionType.EXPENSE);
        assertTrue(t.isExpense());
        assertEquals(new BigDecimal("-25.50"), t.signedAmount());
    }

    @Test
    void changeMethodsUpdateFieldsButNotIdOrType() {
        Transaction t = new Transaction(3, "Lama", new BigDecimal("10.00"), TransactionType.INCOME);
        t.changeDescription("Baru");
        t.changeAmount(new BigDecimal("20.00"));
        assertEquals("Baru", t.getDescription());
        assertEquals(new BigDecimal("20.00"), t.getAmount());
        assertEquals(3, t.getId());
        assertEquals(TransactionType.INCOME, t.getType());
    }
}
