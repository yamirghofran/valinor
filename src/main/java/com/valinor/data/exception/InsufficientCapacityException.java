package com.valinor.data.exception;

/**
 * Exception thrown when a table's capacity is insufficient for the requested party size.
 */
public class InsufficientCapacityException extends ReservationException {
    
    private Integer requiredCapacity;
    private Integer availableCapacity;
    
    /**
     * Constructs a new InsufficientCapacityException with the specified detail message.
     * 
     * @param message the detail message
     */
    public InsufficientCapacityException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new InsufficientCapacityException with the specified detail message,
     * required capacity, and available capacity.
     * 
     * @param message the detail message
     * @param requiredCapacity the required party size
     * @param availableCapacity the available table capacity
     */
    public InsufficientCapacityException(String message, Integer requiredCapacity, Integer availableCapacity) {
        super(message);
        this.requiredCapacity = requiredCapacity;
        this.availableCapacity = availableCapacity;
    }
    
    /**
     * Gets the required capacity.
     * 
     * @return the required capacity
     */
    public Integer getRequiredCapacity() {
        return requiredCapacity;
    }
    
    /**
     * Gets the available capacity.
     * 
     * @return the available capacity
     */
    public Integer getAvailableCapacity() {
        return availableCapacity;
    }
}
