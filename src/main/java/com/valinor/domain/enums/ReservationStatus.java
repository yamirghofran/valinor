package com.valinor.domain.enums;

/**
 * Enumeration for reservation status.
 */
public enum ReservationStatus {
    CONFIRMED("confirmed"),
    COMPLETED("completed"),
    NO_SHOW("no_show"),
    CANCELLED("cancelled");
    
    private final String value;
    
    ReservationStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Gets ReservationStatus from string value.
     * 
     * @param value the string value
     * @return the corresponding ReservationStatus, or null if not found
     */
    public static ReservationStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        for (ReservationStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return value;
    }
}