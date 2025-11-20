package com.valinor.data.exception;

/**
 * Exception thrown when reservation-related operations fail.
 * This is the base exception for all reservation-specific errors.
 */
public class ReservationException extends Exception {
    
    /**
     * Constructs a new ReservationException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ReservationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ReservationException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}
