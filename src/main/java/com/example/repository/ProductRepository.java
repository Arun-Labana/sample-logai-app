package com.example.repository;

import com.example.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Product data access.
 * Simulates database operations with in-memory storage.
 */
public class ProductRepository {
    private static final Logger logger = LoggerFactory.getLogger(ProductRepository.class);
    
    private final Map<Long, Product> products = new HashMap<>();

    public ProductRepository() {
        // Initialize with sample products
        initializeSampleProducts();
    }

    private void initializeSampleProducts() {
        save(new Product(1L, "Laptop Pro 15", new BigDecimal("1299.99"), 50));
        save(new Product(2L, "Wireless Mouse", new BigDecimal("29.99"), 200));
        save(new Product(3L, "USB-C Hub", new BigDecimal("49.99"), 100));
        save(new Product(4L, "Mechanical Keyboard", new BigDecimal("149.99"), 75));
        save(new Product(5L, "Monitor 27 inch", new BigDecimal("399.99"), 30));
        save(new Product(6L, "Webcam HD", new BigDecimal("79.99"), 0)); // Out of stock!
        
        logger.info("Initialized {} sample products", products.size());
    }

    public Product save(Product product) {
        logger.debug("Saving product: {}", product);
        products.put(product.getId(), product);
        return product;
    }

    public Optional<Product> findById(Long id) {
        logger.debug("Finding product by ID: {}", id);
        simulateDbLatency();
        return Optional.ofNullable(products.get(id));
    }

    public List<Product> findAll() {
        simulateDbLatency();
        return new ArrayList<>(products.values());
    }

    public List<Product> findInStock() {
        return products.values().stream()
            .filter(Product::isInStock)
            .collect(Collectors.toList());
    }

    public List<Product> findByCategory(String category) {
        return products.values().stream()
            .filter(p -> category.equals(p.getCategory()))
            .collect(Collectors.toList());
    }

    public boolean decreaseStock(Long productId, int quantity) {
        Product product = products.get(productId);
        if (product == null) {
            logger.error("Cannot decrease stock - product not found: {}", productId);
            return false;
        }
        
        if (product.getStockQuantity() < quantity) {
            logger.warn("Insufficient stock for product {}: requested {}, available {}", 
                productId, quantity, product.getStockQuantity());
            return false;
        }
        
        product.decreaseStock(quantity);
        logger.info("Decreased stock for product {} by {}. New stock: {}", 
            productId, quantity, product.getStockQuantity());
        return true;
    }

    private void simulateDbLatency() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

