package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.exception.UserNotFoundException;
import com.example.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Service for user management operations.
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String email, String name, String password) {
        logger.info("Creating user with email: {}", email);
        
        // Validate email
        validateEmail(email);
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            logger.error("Email already registered: {}", email);
            throw new ValidationException("Email already registered: " + email);
        }
        
        // Validate password strength
        validatePassword(password);
        
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPasswordHash(hashPassword(password));
        
        User savedUser = userRepository.save(user);
        logger.info("User created successfully: {}", savedUser.getId());
        
        return savedUser;
    }

    public User getUserById(Long id) {
        logger.debug("Getting user by ID: {}", id);
        return userRepository.findByIdOrThrow(id);
    }

    public User authenticate(String email, String password) {
        logger.info("Authenticating user: {}", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.warn("Authentication failed - user not found: {}", email);
                return new UserNotFoundException(email);
            });
        
        if (!user.isActive()) {
            logger.warn("Authentication failed - user account is disabled: {}", email);
            throw new ValidationException("User account is disabled");
        }
        
        // BUG: This comparison is intentionally flawed for testing
        String hashedInput = hashPassword(password);
        if (!user.getPasswordHash().equals(hashedInput)) {
            logger.warn("Authentication failed - invalid password for user: {}", email);
            throw new ValidationException("Invalid credentials");
        }
        
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        logger.info("User authenticated successfully: {}", email);
        return user;
    }

    public void deactivateUser(Long userId) {
        logger.info("Deactivating user: {}", userId);
        
        User user = userRepository.findByIdOrThrow(userId);
        user.setActive(false);
        userRepository.save(user);
        
        logger.info("User deactivated: {}", userId);
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            logger.error("Invalid email format: {}", email);
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        
        // Check for at least one number
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Password must contain at least one number");
        }
    }

    private String hashPassword(String password) {
        // Simple hash for demo - in real app use BCrypt
        return Integer.toHexString(password.hashCode());
    }
}

