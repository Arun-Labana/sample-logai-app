package com.example;

import com.example.model.*;
import com.example.repository.*;
import com.example.service.*;
import com.example.exception.*;
import com.example.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Sample E-Commerce Application for testing LogAI
 * 
 * This application simulates various error scenarios that will be
 * captured by the LogAI SDK and analyzed by the AI engine.
 */
public class SampleApp {
    private static final Logger logger = LoggerFactory.getLogger(SampleApp.class);

    // Repositories
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    // Services
    private final UserService userService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final OrderService orderService;

    public SampleApp() {
        // Initialize repositories
        this.userRepository = new UserRepository();
        this.orderRepository = new OrderRepository();
        this.productRepository = new ProductRepository();

        // Initialize services
        this.productService = new ProductService(productRepository);
        this.paymentService = new PaymentService();
        this.userService = new UserService(userRepository);
        this.orderService = new OrderService(orderRepository, productRepository, productService, paymentService);
    }

    public static void main(String[] args) {
        logger.info("=".repeat(60));
        logger.info("Starting Sample E-Commerce Application");
        logger.info("=".repeat(60));

        SampleApp app = new SampleApp();
        
        try {
            // Run various scenarios
            app.runAllScenarios();
        } catch (Exception e) {
            logger.error("Application failed with unexpected error", e);
        }

        logger.info("=".repeat(60));
        logger.info("Application finished - check LogAI dashboard for analysis");
        logger.info("=".repeat(60));

        // Wait for logs to flush
        try {
            logger.info("Waiting for logs to flush to cloud...");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void runAllScenarios() {
        logger.info("\n--- Running All Test Scenarios ---\n");

        // Scenario 1: Successful user registration and order
        runSuccessfulOrderScenario();

        // Scenario 2: Invalid email registration
        runInvalidEmailScenario();

        // Scenario 3: User not found
        runUserNotFoundScenario();

        // Scenario 4: Out of stock product
        runOutOfStockScenario();

        // Scenario 5: Payment failure
        runPaymentFailureScenario();

        // Scenario 6: Invalid quantity
        runInvalidQuantityScenario();

        // Scenario 7: Empty order processing
        runEmptyOrderScenario();

        // Scenario 8: Duplicate email registration
        runDuplicateEmailScenario();

        // Scenario 9: Weak password
        runWeakPasswordScenario();

        // Scenario 10: Check low stock
        productService.checkLowStock();
    }

    private void runSuccessfulOrderScenario() {
        logger.info("\n[Scenario 1] Successful Order Flow");
        try {
            // Create user
            User user = userService.createUser("john@example.com", "John Doe", "SecurePass123");
            logger.info("Created user: {}", user);

            // Create order
            Order order = orderService.createOrder(user.getId(), "123 Main St, City, 12345");
            
            // Add items
            orderService.addItemToOrder(order.getId(), 1L, 1); // Laptop
            orderService.addItemToOrder(order.getId(), 2L, 2); // 2x Mouse
            
            logger.info("Order created with {} items, total: {}", 
                order.getItems().size(), 
                StringUtils.formatCurrency(order.getTotalAmount()));

            // Note: We skip processing to avoid payment gateway failure in success scenario
            logger.info("Scenario 1 completed successfully!");

        } catch (Exception e) {
            logger.error("Scenario 1 failed unexpectedly", e);
        }
    }

    private void runInvalidEmailScenario() {
        logger.info("\n[Scenario 2] Invalid Email Registration");
        try {
            userService.createUser("not-an-email", "Test User", "Password123");
            logger.warn("Expected validation error but succeeded!");
        } catch (ValidationException e) {
            logger.info("Caught expected validation error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in scenario 2", e);
        }
    }

    private void runUserNotFoundScenario() {
        logger.info("\n[Scenario 3] User Not Found");
        try {
            userService.getUserById(99999L);
            logger.warn("Expected UserNotFoundException but succeeded!");
        } catch (UserNotFoundException e) {
            logger.info("Caught expected error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in scenario 3", e);
        }
    }

    private void runOutOfStockScenario() {
        logger.info("\n[Scenario 4] Out of Stock Product");
        try {
            // Product 6 (Webcam) has 0 stock
            productService.reserveStock(6L, 1);
            logger.warn("Expected InsufficientStockException but succeeded!");
        } catch (InsufficientStockException e) {
            logger.info("Caught expected stock error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in scenario 4", e);
        }
    }

    private void runPaymentFailureScenario() {
        logger.info("\n[Scenario 5] Payment Failure");
        try {
            // Create a user and order for payment test
            User user = userService.createUser("payment-test@example.com", "Payment Test", "TestPass123");
            Order order = orderService.createOrder(user.getId(), "456 Test Ave");
            orderService.addItemToOrder(order.getId(), 3L, 1); // USB-C Hub

            // Try to process - will fail due to payment gateway connection
            orderService.processOrder(order.getId(), "4111111111111111");
            
        } catch (OrderProcessingException e) {
            logger.error("Order processing failed: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error in scenario 5", e);
        }
    }

    private void runInvalidQuantityScenario() {
        logger.info("\n[Scenario 6] Invalid Quantity");
        try {
            User user = userService.createUser("qty-test@example.com", "Qty Test", "TestPass123");
            Order order = orderService.createOrder(user.getId(), "789 Qty St");
            
            // Try to add negative quantity
            orderService.addItemToOrder(order.getId(), 1L, -5);
            logger.warn("Expected ValidationException but succeeded!");
            
        } catch (ValidationException e) {
            logger.info("Caught expected validation error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in scenario 6", e);
        }
    }

    private void runEmptyOrderScenario() {
        logger.info("\n[Scenario 7] Empty Order Processing");
        try {
            User user = userService.createUser("empty-order@example.com", "Empty Order", "TestPass123");
            Order order = orderService.createOrder(user.getId(), "Empty St");
            
            // Try to process empty order
            orderService.processOrder(order.getId(), "4111111111111111");
            logger.warn("Expected OrderProcessingException but succeeded!");
            
        } catch (OrderProcessingException e) {
            logger.info("Caught expected error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in scenario 7", e);
        }
    }

    private void runDuplicateEmailScenario() {
        logger.info("\n[Scenario 8] Duplicate Email Registration");
        try {
            // First registration should succeed (if not already registered)
            try {
                userService.createUser("duplicate@example.com", "First User", "Password123");
            } catch (ValidationException e) {
                // Already exists from previous run, continue
            }
            
            // Second registration should fail
            userService.createUser("duplicate@example.com", "Second User", "Password456");
            logger.warn("Expected ValidationException but succeeded!");
            
        } catch (ValidationException e) {
            logger.info("Caught expected validation error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in scenario 8", e);
        }
    }

    private void runWeakPasswordScenario() {
        logger.info("\n[Scenario 9] Weak Password");
        try {
            userService.createUser("weak-pass@example.com", "Weak Pass User", "abc");
            logger.warn("Expected ValidationException but succeeded!");
        } catch (ValidationException e) {
            logger.info("Caught expected validation error: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in scenario 9", e);
        }
    }
}
