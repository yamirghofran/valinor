package com.valinor.data.exception;

/**
 * Exception thrown when user service operations fail.
 * This is the base exception for all user-related errors including
 * creation, update, validation, and business rule violations.
 */
public class UserServiceException extends RepositoryException {
    
    /**
     * Constructs a new UserServiceException with the specified detail message.
     * 
     * @param message the detail message explaining the service failure
     */
    public UserServiceException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new UserServiceException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the service failure
     * @param cause the cause of the exception
     */
    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new UserServiceException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public UserServiceException(Throwable cause) {
        super(cause);
    }
}
