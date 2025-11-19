package com.valinor.restauraunt.management.service;

/**
 * Exception thrown when restaurant layout validation fails.
 * This indicates a business rule violation in the layout structure.
 */
public class LayoutValidationException extends Exception {
    
    /**
     * Constructs a new LayoutValidationException with the specified message.
     * 
     * @param message the detail message
     */
    public LayoutValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new LayoutValidationException with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public LayoutValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

