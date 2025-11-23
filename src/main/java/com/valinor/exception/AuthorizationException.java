package com.valinor.exception;

/**
 * Exception thrown when authorization checks fail.
 * This includes insufficient permissions, access denied to resources,
 * and cross-restaurant access attempts.
 */
public class AuthorizationException extends UserServiceException {
    
    /**
     * Constructs a new AuthorizationException with the specified detail message.
     * 
     * @param message the detail message explaining the authorization failure
     */
    public AuthorizationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new AuthorizationException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the authorization failure
     * @param cause the cause of the exception
     */
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new AuthorizationException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public AuthorizationException(Throwable cause) {
        super(cause);
    }
}
