package com.example.repository;

import com.example.model.Order;
import com.example.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for Order data access.
 * Simulates database operations with in-memory storage.
 */
public class OrderRepository {
    private static final Logger logger = LoggerFactory.getLogger(OrderRepository.class);
    
    private final Map<Long, Order> orders = new HashMap<>();
    private long nextId = 1;

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(nextId++);
        }
        
        logger.debug("Saving order: {}", order);
        orders.put(order.getId(), order);
        
        return order;
    }

    public Optional<Order> findById(Long id) {
        logger.debug("Finding order by ID: {}", id);
        simulateDbLatency();
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> findByUserId(Long userId) {
        logger.debug("Finding orders for user: {}", userId);
        simulateDbLatency();
        
        return orders.values().stream()
            .filter(o -> o.getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    public List<Order> findByStatus(OrderStatus status) {
        logger.debug("Finding orders with status: {}", status);
        simulateDbLatency();
        
        return orders.values().stream()
            .filter(o -> o.getStatus() == status)
            .collect(Collectors.toList());
    }

    public List<Order> findPendingOrders() {
        return findByStatus(OrderStatus.PENDING);
    }

    public void updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orders.get(orderId);
        if (order != null) {
            logger.info("Updating order {} status from {} to {}", orderId, order.getStatus(), newStatus);
            order.setStatus(newStatus);
        } else {
            logger.warn("Cannot update status - order not found: {}", orderId);
        }
    }

    public void delete(Long id) {
        logger.info("Deleting order: {}", id);
        orders.remove(id);
    }

    public long count() {
        return orders.size();
    }

    private void simulateDbLatency() {
        try {
            Thread.sleep(15); // Simulate DB query time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

