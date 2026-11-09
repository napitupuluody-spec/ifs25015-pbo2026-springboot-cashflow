package org.delcom.ifs25015pbo2026springbootcashflow.adapter.repository;

import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;

/** Penyimpanan transaksi di memori; ID bertambah otomatis (1, 2, 3, ...). */
public class TransactionRepository extends InMemoryRepository<Transaction, Integer>
        implements ITransactionRepository {

    private int lastId = 0;

    public TransactionRepository() {
        super(Transaction::getId);
    }

    @Override
    public Transaction saveIncome(String description, BigDecimal amount) {
        return save(new Transaction(++lastId, description, amount, TransactionType.INCOME));
    }

    @Override
    public Transaction saveExpense(String description, BigDecimal amount) {
        return save(new Transaction(++lastId, description, amount, TransactionType.EXPENSE));
    }
}
