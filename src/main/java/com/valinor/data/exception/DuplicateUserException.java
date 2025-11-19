package com.valinor.data.exception;

/**
 * Exception thrown when attempting to create a user with a username or email
 * that already exists in the system.
 */
public class DuplicateUserException extends UserServiceException {
    
    /**
     * Constructs a new DuplicateUserException with the specified detail message.
     * 
     * @param message the detail message explaining which field is duplicated
     */
    public DuplicateUserException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new DuplicateUserException with the specified detail message and cause.
     * 
     * @param message the detail message explaining which field is duplicated
     * @param cause the cause of the exception
     */
    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new DuplicateUserException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public DuplicateUserException(Throwable cause) {
        super(cause);
    }
}
