package org.delcom.ifs25015pbo2026springbootcashflow.domain.exception;

/** Checked exception dasar untuk seluruh kegagalan aturan domain Cashflow. */
public class DomainException extends Exception {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
