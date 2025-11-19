package com.valinor.data.exception;

/**
 * Exception thrown when authentication fails.
 * This includes invalid credentials, account locked/disabled,
 * and session-related authentication errors.
 */
public class AuthenticationException extends UserServiceException {
    
    /**
     * Constructs a new AuthenticationException with the specified detail message.
     * 
     * @param message the detail message explaining the authentication failure
     */
    public AuthenticationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new AuthenticationException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the authentication failure
     * @param cause the cause of the exception
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new AuthenticationException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
