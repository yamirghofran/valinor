package com.valinor.exception;

/**
 * Exception thrown when entity validation fails.
 * This includes missing required fields, invalid data types, or constraint violations.
 */
public class EntityValidationException extends RepositoryException {
    
    /**
     * Constructs a new EntityValidationException with the specified detail message.
     * 
     * @param message the detail message explaining the validation failure
     */
    public EntityValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new EntityValidationException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the validation failure
     * @param cause the cause of the exception
     */
    public EntityValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new EntityValidationException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public EntityValidationException(Throwable cause) {
        super(cause);
    }
}