package com.example.exception;

import java.math.BigDecimal;

/**
 * Exception thrown when payment processing fails.
 */
public class PaymentFailedException extends RuntimeException {
    private final String transactionId;
    private final BigDecimal amount;
    private final String errorCode;

    public PaymentFailedException(String transactionId, BigDecimal amount, String errorCode) {
        super("Payment failed for transaction " + transactionId + " (amount: $" + amount + "): " + errorCode);
        this.transactionId = transactionId;
        this.amount = amount;
        this.errorCode = errorCode;
    }

    public PaymentFailedException(String transactionId, BigDecimal amount, String errorCode, Throwable cause) {
        super("Payment failed for transaction " + transactionId + " (amount: $" + amount + "): " + errorCode, cause);
        this.transactionId = transactionId;
        this.amount = amount;
        this.errorCode = errorCode;
    }

    public String getTransactionId() { return transactionId; }
    public BigDecimal getAmount() { return amount; }
    public String getErrorCode() { return errorCode; }
}

