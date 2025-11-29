package com.example.service;

import com.example.exception.PaymentFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;

/**
 * Service for payment processing.
 * Simulates integration with external payment gateway.
 */
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private static final String PAYMENT_GATEWAY_HOST = "payment-gateway.example.com";
    private static final int PAYMENT_GATEWAY_PORT = 443;
    
    private final Random random = new Random();

    public String processPayment(Long orderId, BigDecimal amount, String cardNumber) {
        String transactionId = UUID.randomUUID().toString().substring(0, 8);
        
        logger.info("Processing payment for order {}: ${} (txn: {})", orderId, amount, transactionId);
        
        // Validate card number
        if (!isValidCardNumber(cardNumber)) {
            logger.error("Invalid card number format for order {}", orderId);
            throw new PaymentFailedException(transactionId, amount, "INVALID_CARD_FORMAT");
        }
        
        // Simulate connecting to payment gateway
        try {
            connectToPaymentGateway();
        } catch (Exception e) {
            logger.error("Failed to connect to payment gateway: {}", e.getMessage());
            throw new PaymentFailedException(transactionId, amount, "GATEWAY_UNAVAILABLE", e);
        }
        
        // Simulate payment processing with random failures
        if (random.nextInt(100) < 15) { // 15% failure rate
            String errorCode = getRandomErrorCode();
            logger.error("Payment declined for order {}: {}", orderId, errorCode);
            throw new PaymentFailedException(transactionId, amount, errorCode);
        }
        
        // Simulate processing time
        try {
            Thread.sleep(500 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("Payment successful for order {}: txn={}", orderId, transactionId);
        return transactionId;
    }

    public void refundPayment(String transactionId, BigDecimal amount) {
        logger.info("Processing refund for transaction {}: ${}", transactionId, amount);
        
        // Simulate refund processing
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (random.nextInt(100) < 5) { // 5% failure rate
            logger.error("Refund failed for transaction {}", transactionId);
            throw new PaymentFailedException(transactionId, amount, "REFUND_FAILED");
        }
        
        logger.info("Refund processed successfully for transaction {}", transactionId);
    }

    private void connectToPaymentGateway() throws Exception {
        logger.debug("Connecting to payment gateway: {}:{}", PAYMENT_GATEWAY_HOST, PAYMENT_GATEWAY_PORT);
        
        // This will fail - simulating network error
        try (Socket socket = new Socket(PAYMENT_GATEWAY_HOST, PAYMENT_GATEWAY_PORT)) {
            // Would send payment request here
        } catch (Exception e) {
            // Rethrow to simulate gateway connection failure
            throw new RuntimeException("Connection timed out: " + PAYMENT_GATEWAY_HOST + ":" + PAYMENT_GATEWAY_PORT, e);
        }
    }

    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) return false;
        String cleaned = cardNumber.replaceAll("\\s+", "").replaceAll("-", "");
        return cleaned.matches("\\d{16}");
    }

    private String getRandomErrorCode() {
        String[] errorCodes = {
            "INSUFFICIENT_FUNDS",
            "CARD_EXPIRED", 
            "CARD_DECLINED",
            "CVV_MISMATCH",
            "FRAUD_SUSPECTED"
        };
        return errorCodes[random.nextInt(errorCodes.length)];
    }
}

