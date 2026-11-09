package org.delcom.ifs25015pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinancePresenterTest {

    private final FinancePresenter presenter = new FinancePresenter();

    private Transaction income() {
        return new Transaction(1, "Gaji", new BigDecimal("1000000"), TransactionType.INCOME,
                LocalDateTime.of(2026, 11, 6, 9, 5));
    }

    @Test
    void formatsMoneyWithThousandsSeparatorAndTwoDecimals() {
        assertEquals("Rp 1,000,000.00", presenter.formatMoney(new BigDecimal("1000000")));
        assertEquals("Rp 0.50", presenter.formatMoney(new BigDecimal("0.5")));
        assertEquals("Rp 4,975,000.00", presenter.formatMoney(new BigDecimal("4975000.00")));
        assertEquals("-Rp 500.00", presenter.formatMoney(new BigDecimal("-500")));
    }

    @Test
    void formatsTransactionLineAndBalance() {
        assertEquals("[1] Gaji | Rp 1,000,000.00 | [Pemasukan]", presenter.format(income()));
        Transaction expense = new Transaction(2, "Makan", new BigDecimal("25"), TransactionType.EXPENSE);
        assertEquals("[2] Makan | Rp 25.00 | [Pengeluaran]", presenter.format(expense));
        assertEquals("Saldo: Rp 4,975,000.00", presenter.formatBalance(new BigDecimal("4975000")));
        assertEquals("06/11/2026 09:05", presenter.formatCreatedAt(income()));
    }

    @Test
    void sortLabelsExistForAllOptions() {
        for (SortOption option : SortOption.values()) {
            assertTrue(!presenter.sortLabel(option).isBlank());
        }
        assertEquals("Pemasukan dulu", presenter.sortLabel(SortOption.INCOME_FIRST));
    }

    @Test
    void listsAndHeaders() {
        assertEquals("Daftar Transaksi:\n- Belum ada transaksi!", presenter.showTransactions(List.of()));
        assertEquals("Hasil Pencarian: \"x\"\n- Transaksi tidak ditemukan!",
                presenter.showSearchResults(List.of(), " x "));
        assertEquals("Daftar Transaksi (Jumlah terkecil):\n[1] Gaji | Rp 1,000,000.00 | [Pemasukan]",
                presenter.showSortedTransactions(List.of(income()), SortOption.AMOUNT_ASC));
        assertEquals("Daftar Transaksi", presenter.listHeader(null));
        assertEquals("Daftar Transaksi", presenter.listHeader("  "));
    }

    @Test
    void messages() {
        assertTrue(presenter.showAddIncomeSuccess(income()).startsWith("Berhasil mencatat pemasukan: [1]"));
        assertTrue(presenter.showAddExpenseSuccess(income()).startsWith("Berhasil mencatat pengeluaran: [1]"));
        assertEquals("Berhasil menghapus transaksi.", presenter.showRemoveSuccess());
        assertEquals("[!] Gagal menghapus transaksi dengan ID: 7.", presenter.showRemoveFailed(7));
        assertEquals("[!] Jumlah salah", presenter.showValidationError("Jumlah salah"));
    }
}
