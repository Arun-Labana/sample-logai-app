package com.example.model;

import java.math.BigDecimal;

/**
 * OrderItem model representing a single item in an order.
 */
public class OrderItem {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;

    public OrderItem() {}

    public OrderItem(Long productId, String productName, int quantity, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public String toString() {
        return "OrderItem{productId=" + productId + ", name='" + productName + "', qty=" + quantity + ", price=" + price + "}";
    }
}

