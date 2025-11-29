package com.example.exception;

/**
 * Exception thrown when a user is not found in the system.
 */
public class UserNotFoundException extends RuntimeException {
    private final Long userId;

    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
        this.userId = userId;
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
        this.userId = null;
    }

    public Long getUserId() {
        return userId;
    }
}

