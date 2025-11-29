package com.example.exception;

/**
 * Exception thrown when there's not enough stock for a product.
 */
public class InsufficientStockException extends RuntimeException {
    private final Long productId;
    private final int requestedQuantity;
    private final int availableQuantity;

    public InsufficientStockException(Long productId, int requestedQuantity, int availableQuantity) {
        super("Insufficient stock for product " + productId + 
              ": requested " + requestedQuantity + ", available " + availableQuantity);
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public Long getProductId() { return productId; }
    public int getRequestedQuantity() { return requestedQuantity; }
    public int getAvailableQuantity() { return availableQuantity; }
}

