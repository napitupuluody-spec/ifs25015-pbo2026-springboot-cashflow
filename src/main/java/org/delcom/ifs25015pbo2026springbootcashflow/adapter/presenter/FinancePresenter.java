package org.delcom.ifs25015pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Mengubah data domain menjadi teks siap tampil. Format uang dan label berada di
 * sini — bukan di entity maupun use case.
 */
public class FinancePresenter {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** 1000000 -> "Rp 1,000,000.00"; nilai negatif -> "-Rp 500.00". */
    public String formatMoney(BigDecimal amount) {
        BigDecimal value = amount.setScale(2, RoundingMode.HALF_UP);
        String digits = String.format(Locale.US, "%,.2f", value.abs());
        return (value.signum() < 0 ? "-Rp " : "Rp ") + digits;
    }

    public String formatType(TransactionType type) {
        return switch (type) {
            case INCOME -> "Pemasukan";
            case EXPENSE -> "Pengeluaran";
        };
    }

    public String formatCreatedAt(Transaction transaction) {
        return transaction.getCreatedAt().format(DATE_TIME);
    }

    /** "[1] Gaji | Rp 5,000,000.00 | [Pemasukan]" */
    public String format(Transaction t) {
        return String.format("[%d] %s | %s | [%s]",
                t.getId(), t.getDescription(), formatMoney(t.getAmount()), formatType(t.getType()));
    }

    /** "Saldo: Rp 4,975,000.00" */
    public String formatBalance(BigDecimal balance) {
        return "Saldo: " + formatMoney(balance);
    }

    public String sortLabel(SortOption option) {
        return switch (option) {
            case AMOUNT_ASC -> "Jumlah terkecil";
            case AMOUNT_DESC -> "Jumlah terbesar";
            case INCOME_FIRST -> "Pemasukan dulu";
            case EXPENSE_FIRST -> "Pengeluaran dulu";
        };
    }

    public String formatList(List<Transaction> transactions, String header, String emptyMessage) {
        if (transactions.isEmpty()) {
            return header + "\n" + emptyMessage;
        }
        return header + "\n" + transactions.stream().map(this::format).collect(Collectors.joining("\n"));
    }

    public String showTransactions(List<Transaction> transactions) {
        return formatList(transactions, "Daftar Transaksi:", emptyMessage(false));
    }

    public String showSearchResults(List<Transaction> transactions, String keyword) {
        return formatList(transactions, listHeader(keyword), emptyMessage(true));
    }

    public String showSortedTransactions(List<Transaction> transactions, SortOption option) {
        return formatList(transactions, "Daftar Transaksi (" + sortLabel(option) + "):", emptyMessage(false));
    }

    /** Judul daftar: biasa atau hasil pencarian. */
    public String listHeader(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return "Daftar Transaksi";
        }
        return "Hasil Pencarian: \"" + keyword.strip() + "\"";
    }

    public String emptyMessage(boolean searching) {
        return searching ? "- Transaksi tidak ditemukan!" : "- Belum ada transaksi!";
    }

    public String showAddIncomeSuccess(Transaction t) {
        return "Berhasil mencatat pemasukan: " + format(t);
    }

    public String showAddExpenseSuccess(Transaction t) {
        return "Berhasil mencatat pengeluaran: " + format(t);
    }

    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    public String showValidationError(String message) {
        return "[!] " + message;
    }
}
