package com.example.service;

import com.example.model.*;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for order management operations.
 */
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        ProductService productService, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.paymentService = paymentService;
    }

    public Order createOrder(Long userId, String shippingAddress) {
        logger.info("Creating new order for user {}", userId);
        
        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            logger.error("Invalid shipping address for user {}", userId);
            throw new ValidationException("Shipping address is required");
        }
        
        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(shippingAddress);
        
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created: {} for user {}", savedOrder.getId(), userId);
        
        return savedOrder;
    }

    public Order addItemToOrder(Long orderId, Long productId, int quantity) {
        logger.info("Adding {} x product {} to order {}", quantity, productId, orderId);
        
        if (quantity <= 0) {
            logger.error("Invalid quantity {} for order {}", quantity, orderId);
            throw new ValidationException("Quantity must be positive");
        }
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> {
                logger.error("Order not found: {}", orderId);
                return new OrderProcessingException(orderId, "Order not found");
            });
        
        if (order.getStatus() != OrderStatus.PENDING) {
            logger.error("Cannot modify order {} - status is {}", orderId, order.getStatus());
            throw new OrderProcessingException(orderId, "Cannot modify non-pending order");
        }
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> {
                logger.error("Product not found: {}", productId);
                return new IllegalArgumentException("Product not found: " + productId);
            });
        
        // Check stock
        if (product.getStockQuantity() < quantity) {
            logger.warn("Insufficient stock for product {} in order {}", productId, orderId);
            throw new InsufficientStockException(productId, quantity, product.getStockQuantity());
        }
        
        OrderItem item = new OrderItem(productId, product.getName(), quantity, product.getPrice());
        order.addItem(item);
        
        orderRepository.save(order);
        logger.info("Item added to order {}: {} x {} = ${}", 
            orderId, quantity, product.getName(), item.getSubtotal());
        
        return order;
    }

    public Order processOrder(Long orderId, String cardNumber) {
        logger.info("Processing order {}", orderId);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderProcessingException(orderId, "Order not found"));
        
        if (order.getItems().isEmpty()) {
            logger.error("Cannot process empty order {}", orderId);
            throw new OrderProcessingException(orderId, "Order has no items");
        }
        
        if (order.getStatus() != OrderStatus.PENDING) {
            logger.error("Order {} already processed (status: {})", orderId, order.getStatus());
            throw new OrderProcessingException(orderId, "Order already processed");
        }
        
        try {
            // Reserve stock for all items
            logger.info("Reserving stock for order {}", orderId);
            for (OrderItem item : order.getItems()) {
                productService.reserveStock(item.getProductId(), item.getQuantity());
            }
            
            // Process payment
            logger.info("Processing payment for order {}: ${}", orderId, order.getTotalAmount());
            String transactionId = paymentService.processPayment(orderId, order.getTotalAmount(), cardNumber);
            
            // Update order status
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            
            logger.info("Order {} processed successfully. Transaction: {}", orderId, transactionId);
            return order;
            
        } catch (InsufficientStockException e) {
            logger.error("Stock reservation failed for order {}: {}", orderId, e.getMessage());
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            throw new OrderProcessingException(orderId, "Stock reservation failed", e);
            
        } catch (PaymentFailedException e) {
            logger.error("Payment failed for order {}: {}", orderId, e.getMessage());
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            throw new OrderProcessingException(orderId, "Payment failed: " + e.getErrorCode(), e);
        }
    }

    public void cancelOrder(Long orderId) {
        logger.info("Cancelling order {}", orderId);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderProcessingException(orderId, "Order not found"));
        
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            logger.error("Cannot cancel order {} - already shipped/delivered", orderId);
            throw new OrderProcessingException(orderId, "Cannot cancel shipped order");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        logger.info("Order {} cancelled successfully", orderId);
    }

    public List<Order> getUserOrders(Long userId) {
        logger.debug("Getting orders for user {}", userId);
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderProcessingException(orderId, "Order not found"));
    }
}

