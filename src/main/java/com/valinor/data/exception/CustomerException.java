package com.valinor.data.exception;

/**
 * Exception thrown when customer-related operations fail.
 * This includes validation errors, duplicate email addresses, and other customer-specific issues.
 */
public class CustomerException extends Exception {
    
    /**
     * Constructs a new CustomerException with the specified detail message.
     * 
     * @param message the detail message
     */
    public CustomerException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new CustomerException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public CustomerException(String message, Throwable cause) {
        super(message, cause);
    }
}
