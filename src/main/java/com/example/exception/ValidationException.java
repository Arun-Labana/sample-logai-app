package com.example.exception;

import java.util.List;
import java.util.ArrayList;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends RuntimeException {
    private final List<String> errors;

    public ValidationException(String error) {
        super("Validation failed: " + error);
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }

    public ValidationException(List<String> errors) {
        super("Validation failed with " + errors.size() + " errors: " + String.join(", ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}

