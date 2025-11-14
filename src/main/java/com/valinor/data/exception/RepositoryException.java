package com.valinor.data.exception;

/**
 * Base exception for all repository-related errors.
 * This exception wraps underlying storage-specific exceptions
 * and provides a consistent error interface for the repository layer.
 */
public class RepositoryException extends Exception {
    
    /**
     * Constructs a new RepositoryException with the specified detail message.
     * 
     * @param message the detail message explaining the exception
     */
    public RepositoryException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new RepositoryException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the exception
     * @param cause the cause of the exception
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new RepositoryException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public RepositoryException(Throwable cause) {
        super(cause);
    }
}