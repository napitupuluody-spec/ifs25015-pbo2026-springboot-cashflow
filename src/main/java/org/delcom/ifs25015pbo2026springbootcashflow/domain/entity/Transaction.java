package org.delcom.ifs25015pbo2026springbootcashflow.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity inti: satu catatan transaksi keuangan.
 * Murni domain — tanpa urusan tampilan, penyimpanan, maupun framework.
 */
public class Transaction {

    /** ID unik; tidak berubah setelah transaksi dibuat. */
    private final int id;

    /** Jenis transaksi; tidak berubah setelah dibuat. */
    private final TransactionType type;

    /** Waktu transaksi dicatat. */
    private final LocalDateTime createdAt;

    private String description;
    private BigDecimal amount;

    public Transaction(int id, String description, BigDecimal amount, TransactionType type) {
        this(id, description, amount, type, LocalDateTime.now());
    }

    public Transaction(int id, String description, BigDecimal amount, TransactionType type,
                       LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    /** Jumlah bertanda: positif untuk pemasukan, negatif untuk pengeluaran. */
    public BigDecimal signedAmount() {
        return isIncome() ? amount : amount.negate();
    }

    /** Mengganti keterangan transaksi. */
    public void changeDescription(String description) {
        this.description = description;
    }

    /** Mengganti jumlah uang transaksi. */
    public void changeAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
