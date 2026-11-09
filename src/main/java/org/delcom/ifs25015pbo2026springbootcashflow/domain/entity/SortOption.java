package org.delcom.ifs25015pbo2026springbootcashflow.domain.entity;

import java.util.Comparator;

/**
 * Kriteria pengurutan transaksi. Setiap opsi membawa Comparator-nya sendiri,
 * sehingga aturan urut terpusat di domain (bukan angka ajaib di view).
 * Sort bersifat stabil: transaksi sama-nilai tetap berurutan sesuai pencatatan.
 */
public enum SortOption {
    /** Jumlah terkecil lebih dulu. */
    AMOUNT_ASC(Comparator.comparing(Transaction::getAmount)),

    /** Jumlah terbesar lebih dulu. */
    AMOUNT_DESC(Comparator.comparing(Transaction::getAmount).reversed()),

    /** Pemasukan ditampilkan lebih dulu. */
    INCOME_FIRST(Comparator.comparingInt((Transaction t) -> t.isIncome() ? 0 : 1)),

    /** Pengeluaran ditampilkan lebih dulu. */
    EXPENSE_FIRST(Comparator.comparingInt((Transaction t) -> t.isExpense() ? 0 : 1));

    private final Comparator<Transaction> comparator;

    SortOption(Comparator<Transaction> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Transaction> comparator() {
        return comparator;
    }
}
