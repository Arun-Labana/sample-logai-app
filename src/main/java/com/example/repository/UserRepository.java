package com.example.repository;

import com.example.model.User;
import com.example.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Repository for User data access.
 * Simulates database operations with in-memory storage.
 */
public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();
    private long nextId = 1;

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        
        logger.debug("Saving user: {}", user);
        users.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
        
        return user;
    }

    public Optional<User> findById(Long id) {
        logger.debug("Finding user by ID: {}", id);
        
        // Simulate database latency
        simulateDbLatency();
        
        return Optional.ofNullable(users.get(id));
    }

    public User findByIdOrThrow(Long id) {
        return findById(id)
            .orElseThrow(() -> {
                logger.error("User not found with ID: {}", id);
                return new UserNotFoundException(id);
            });
    }

    public Optional<User> findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        
        // Simulate database query
        simulateDbLatency();
        
        return Optional.ofNullable(usersByEmail.get(email));
    }

    public void delete(Long id) {
        logger.info("Deleting user: {}", id);
        User user = users.remove(id);
        if (user != null) {
            usersByEmail.remove(user.getEmail());
        }
    }

    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(email);
    }

    private void simulateDbLatency() {
        try {
            Thread.sleep(10); // Simulate DB query time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

