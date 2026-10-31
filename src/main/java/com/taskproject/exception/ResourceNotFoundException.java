package com.taskproject.exception;

/**
 * Thrown when a requested resource (e.g., Task) cannot be found for the current user.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
