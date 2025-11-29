package com.example.exception;

/**
 * Exception thrown when order processing fails.
 */
public class OrderProcessingException extends RuntimeException {
    private final Long orderId;
    private final String reason;

    public OrderProcessingException(Long orderId, String reason) {
        super("Failed to process order " + orderId + ": " + reason);
        this.orderId = orderId;
        this.reason = reason;
    }

    public OrderProcessingException(Long orderId, String reason, Throwable cause) {
        super("Failed to process order " + orderId + ": " + reason, cause);
        this.orderId = orderId;
        this.reason = reason;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
    }
}

