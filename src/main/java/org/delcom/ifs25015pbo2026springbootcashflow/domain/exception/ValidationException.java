package org.delcom.ifs25015pbo2026springbootcashflow.domain.exception;

/** Dilempar ketika input melanggar aturan bisnis (keterangan kosong, jumlah tidak valid, dst). */
public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message);
    }
}
