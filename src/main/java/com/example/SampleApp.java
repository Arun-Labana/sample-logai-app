package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample application to test LogAI remote logging.
 * This app generates various log levels including errors with stack traces.
 */
public class SampleApp {

    private static final Logger logger = LoggerFactory.getLogger(SampleApp.class);

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║     LogAI Sample Application Started      ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.println();

        SampleApp app = new SampleApp();
        
        // Generate various logs
        app.runNormalOperations();
        app.simulateWarnings();
        app.simulateErrors();
        
        System.out.println();
        System.out.println("✅ Sample logs generated! Check your LogAI dashboard.");
        System.out.println("   Waiting 5 seconds for logs to flush...");
        
        // Wait for async logs to be sent
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("✅ Done! Go to http://localhost:3000 and run a scan.");
    }

    /**
     * Simulate normal application operations
     */
    private void runNormalOperations() {
        logger.info("Application starting up...");
        logger.info("Loading configuration from environment");
        logger.debug("Debug: Config loaded successfully");
        logger.info("Connecting to database...");
        logger.info("Database connection established");
        logger.info("Starting background services...");
        logger.info("Application ready to serve requests");
    }

    /**
     * Simulate warning conditions
     */
    private void simulateWarnings() {
        logger.warn("Connection pool running low - 2 connections remaining");
        logger.warn("Request took longer than expected: 2500ms");
        logger.warn("Cache miss rate is high: 45%");
        logger.warn("Memory usage above threshold: 85%");
    }

    /**
     * Simulate various error conditions
     */
    private void simulateErrors() {
        // Error 1: NullPointerException
        simulateNullPointerException();
        
        // Error 2: IllegalArgumentException
        simulateIllegalArgumentException();
        
        // Error 3: Database connection error
        simulateDatabaseError();
        
        // Error 4: Service timeout
        simulateServiceTimeout();
        
        // Error 5: Validation error
        simulateValidationError();
    }

    private void simulateNullPointerException() {
        try {
            UserService userService = new UserService();
            userService.processUser(null);
        } catch (Exception e) {
            logger.error("Failed to process user request", e);
        }
    }

    private void simulateIllegalArgumentException() {
        try {
            OrderService orderService = new OrderService();
            orderService.createOrder(-1, "invalid");
        } catch (Exception e) {
            logger.error("Failed to create order", e);
        }
    }

    private void simulateDatabaseError() {
        try {
            DatabaseService dbService = new DatabaseService();
            dbService.executeQuery("SELECT * FROM non_existent_table");
        } catch (Exception e) {
            logger.error("Database query failed", e);
        }
    }

    private void simulateServiceTimeout() {
        try {
            PaymentService paymentService = new PaymentService();
            paymentService.processPayment("PAY-123", 99.99);
        } catch (Exception e) {
            logger.error("Payment processing failed", e);
        }
    }

    private void simulateValidationError() {
        try {
            ValidationService validationService = new ValidationService();
            validationService.validateEmail("not-an-email");
        } catch (Exception e) {
            logger.error("Validation failed for user input", e);
        }
    }
}

/**
 * Simulated User Service
 */
class UserService {
    public void processUser(String userId) {
        if (userId == null) {
            throw new NullPointerException("User ID cannot be null");
        }
        // Process user...
    }
}

/**
 * Simulated Order Service
 */
class OrderService {
    public void createOrder(int quantity, String productId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive, got: " + quantity);
        }
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        // Create order...
    }
}

/**
 * Simulated Database Service
 */
class DatabaseService {
    public void executeQuery(String sql) {
        // Simulate a database error
        throw new RuntimeException("Database error: Table 'non_existent_table' doesn't exist");
    }
}

/**
 * Simulated Payment Service
 */
class PaymentService {
    public void processPayment(String paymentId, double amount) {
        // Simulate nested exception
        try {
            connectToPaymentGateway();
        } catch (Exception e) {
            throw new RuntimeException("Payment processing failed for " + paymentId, e);
        }
    }

    private void connectToPaymentGateway() {
        throw new RuntimeException("Connection timed out: payment-gateway.example.com:443");
    }
}

/**
 * Simulated Validation Service
 */
class ValidationService {
    public void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalStateException("Invalid email format: " + email);
        }
    }
}

