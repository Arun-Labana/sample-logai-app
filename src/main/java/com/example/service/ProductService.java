package com.example.service;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import com.example.exception.InsufficientStockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service for product catalog operations.
 */
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        logger.debug("Getting all products");
        return productRepository.findAll();
    }

    public List<Product> getAvailableProducts() {
        logger.debug("Getting available products");
        return productRepository.findInStock();
    }

    public Optional<Product> getProductById(Long id) {
        logger.debug("Getting product by ID: {}", id);
        return productRepository.findById(id);
    }

    public void reserveStock(Long productId, int quantity) {
        logger.info("Reserving {} units of product {}", quantity, productId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> {
                logger.error("Product not found: {}", productId);
                return new IllegalArgumentException("Product not found: " + productId);
            });
        
        if (!product.isInStock()) {
            logger.error("Product {} is out of stock", productId);
            throw new InsufficientStockException(productId, quantity, 0);
        }
        
        if (product.getStockQuantity() < quantity) {
            logger.error("Insufficient stock for product {}: requested {}, available {}", 
                productId, quantity, product.getStockQuantity());
            throw new InsufficientStockException(productId, quantity, product.getStockQuantity());
        }
        
        boolean success = productRepository.decreaseStock(productId, quantity);
        if (!success) {
            throw new InsufficientStockException(productId, quantity, product.getStockQuantity());
        }
        
        logger.info("Stock reserved successfully for product {}", productId);
    }

    public void checkLowStock() {
        logger.info("Checking for low stock products...");
        
        List<Product> allProducts = productRepository.findAll();
        
        for (Product product : allProducts) {
            if (product.getStockQuantity() <= 10) {
                logger.warn("Low stock alert: {} has only {} units left", 
                    product.getName(), product.getStockQuantity());
            }
            
            if (product.getStockQuantity() == 0) {
                logger.error("OUT OF STOCK: {} (ID: {})", product.getName(), product.getId());
            }
        }
    }
}

