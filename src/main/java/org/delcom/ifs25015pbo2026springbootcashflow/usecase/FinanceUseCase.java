package org.delcom.ifs25015pbo2026springbootcashflow.usecase;

import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25015pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Proses bisnis pencatatan keuangan. Hanya bergantung pada port
 * {@link ITransactionRepository}; tidak ada I/O, Vaadin, maupun System.out.
 */
public class FinanceUseCase {

    static final int MAX_DESCRIPTION_LENGTH = 100;
    static final BigDecimal MAX_AMOUNT = new BigDecimal("999999999999.99");

    private final ITransactionRepository repository;

    public FinanceUseCase(ITransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    /**
     * Mencatat pemasukan.
     *
     * @param amount jumlah mentah dari input pengguna (mis. "150000" atau "150000.50")
     */
    public Transaction addIncome(String description, String amount) throws ValidationException {
        String cleanDescription = validateDescription(description);
        BigDecimal value = parseAmount(amount);
        return repository.saveIncome(cleanDescription, value);
    }

    /** Mencatat pengeluaran; aturan validasi sama dengan {@link #addIncome}. */
    public Transaction addExpense(String description, String amount) throws ValidationException {
        String cleanDescription = validateDescription(description);
        BigDecimal value = parseAmount(amount);
        return repository.saveExpense(cleanDescription, value);
    }

    /** Menghapus transaksi; ID harus positif dan harus ada. */
    public void removeTransaction(int id) throws ValidationException, EntityNotFoundException {
        if (id <= 0) {
            throw new ValidationException("ID transaksi tidak valid");
        }
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari berdasarkan keterangan (case-insensitive); kata kunci kosong = semua data. */
    public List<Transaction> searchTransactions(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return repository.findAll();
        }
        String needle = keyword.strip().toLowerCase();
        return repository.findBy(t -> t.getDescription().toLowerCase().contains(needle));
    }

    /** Mengurutkan semua transaksi menurut {@link SortOption}. */
    public List<Transaction> sortTransactions(SortOption option) {
        return sorted(repository.findAll(), option);
    }

    /** Gabungan cari lalu urut; {@code option == null} berarti urutan pencatatan. */
    public List<Transaction> listTransactions(String keyword, SortOption option) {
        return sorted(searchTransactions(keyword), option);
    }

    public BigDecimal getTotalIncome() {
        return sum(true);
    }

    public BigDecimal getTotalExpense() {
        return sum(false);
    }

    /** Saldo = total pemasukan - total pengeluaran. */
    public BigDecimal getBalance() {
        return repository.findAll().stream()
                .map(Transaction::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sum(boolean income) {
        return repository.findAll().stream()
                .filter(t -> t.isIncome() == income)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Transaction> sorted(List<Transaction> source, SortOption option) {
        if (option == null) {
            return source;
        }
        return source.stream().sorted(option.comparator()).toList();
    }

    private String validateDescription(String description) throws ValidationException {
        if (description == null || description.isBlank()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        String clean = description.strip();
        if (clean.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException(
                    "Keterangan maksimal " + MAX_DESCRIPTION_LENGTH + " karakter");
        }
        return clean;
    }

    private BigDecimal parseAmount(String raw) throws ValidationException {
        if (raw == null || raw.isBlank()) {
            throw new ValidationException("Jumlah tidak boleh kosong");
        }
        BigDecimal value;
        try {
            value = new BigDecimal(raw.strip());
        } catch (NumberFormatException e) {
            throw new ValidationException("Jumlah harus berupa angka (contoh: 150000 atau 150000.50)");
        }
        if (value.signum() <= 0) {
            throw new ValidationException("Jumlah harus lebih besar dari 0");
        }
        if (value.compareTo(MAX_AMOUNT) > 0) {
            throw new ValidationException("Jumlah terlalu besar");
        }
        BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
        if (scaled.signum() <= 0) {
            throw new ValidationException("Jumlah minimal 0.01");
        }
        return scaled;
    }
}
