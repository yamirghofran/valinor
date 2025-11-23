package com.valinor.exception;

import java.time.LocalDateTime;

/**
 * Exception thrown when a reservation conflicts with an existing reservation.
 * This typically occurs when attempting to book a table that is already reserved
 * at the requested time.
 */
public class ReservationConflictException extends ReservationException {
    
    private Long tableId;
    private LocalDateTime requestedTime;
    
    /**
     * Constructs a new ReservationConflictException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ReservationConflictException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ReservationConflictException with the specified detail message,
     * table ID, and requested time.
     * 
     * @param message the detail message
     * @param tableId the ID of the table that has a conflict
     * @param requestedTime the requested reservation time
     */
    public ReservationConflictException(String message, Long tableId, LocalDateTime requestedTime) {
        super(message);
        this.tableId = tableId;
        this.requestedTime = requestedTime;
    }
    
    /**
     * Gets the table ID that has a conflict.
     * 
     * @return the table ID
     */
    public Long getTableId() {
        return tableId;
    }
    
    /**
     * Gets the requested reservation time.
     * 
     * @return the requested time
     */
    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }
}
