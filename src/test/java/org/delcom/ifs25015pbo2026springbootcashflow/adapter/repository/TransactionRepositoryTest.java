package org.delcom.ifs25015pbo2026springbootcashflow.adapter.repository;

import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionRepositoryTest {

    private TransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepository();
    }

    @Test
    void idsAreAutoIncrementedAndTypesAssigned() {
        Transaction a = repository.saveIncome("A", BigDecimal.TEN);
        Transaction b = repository.saveExpense("B", BigDecimal.ONE);
        assertEquals(1, a.getId());
        assertEquals(2, b.getId());
        assertEquals(TransactionType.INCOME, a.getType());
        assertEquals(TransactionType.EXPENSE, b.getType());
    }

    @Test
    void findAllReturnsDefensiveCopy() {
        repository.saveIncome("A", BigDecimal.TEN);
        List<Transaction> copy = repository.findAll();
        copy.clear();
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void findByIdDeleteAndIdsAreNotReused() {
        Transaction a = repository.saveIncome("A", BigDecimal.TEN);
        assertTrue(repository.findById(a.getId()).isPresent());
        assertTrue(repository.deleteById(a.getId()));
        assertFalse(repository.deleteById(a.getId()));
        assertTrue(repository.findById(a.getId()).isEmpty());
        assertEquals(2, repository.saveIncome("B", BigDecimal.TEN).getId());
    }

    @Test
    void updateReplacesStoredEntityAndFindByFilters() {
        Transaction a = repository.saveIncome("A", BigDecimal.TEN);
        a.changeDescription("Berubah");
        repository.update(a);
        assertEquals("Berubah", repository.findById(1).orElseThrow().getDescription());
        assertEquals(1, repository.findBy(t -> t.getDescription().startsWith("Ber")).size());
        repository.update(new Transaction(77, "Tak ada", BigDecimal.ONE, TransactionType.INCOME));
        assertEquals(1, repository.findAll().size());
    }
}
