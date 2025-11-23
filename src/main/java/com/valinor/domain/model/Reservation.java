package com.valinor.domain.model;

import com.valinor.domain.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a booking made by a customer for a specific table and time.
 */
public class Reservation {
    
    private Long reservationId;
    private Long customerId;
    private Long restaurantId;
    private Long tableId;
    private Integer partySize;
    private LocalDateTime reservationDatetime;
    private ReservationStatus status;
    private String specialRequests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor.
     */
    public Reservation() {
        this.status = ReservationStatus.CONFIRMED; // Default status
        this.createdAt = LocalDateTime.now(); // Set creation time
        this.updatedAt = LocalDateTime.now(); // Set update time
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param customerId customer ID making the reservation
     * @param restaurantId restaurant ID for the reservation
     * @param tableId table ID for the reservation
     * @param partySize the of the reservation party
     * @param reservationDatetime the date and time of the reservation
     */
    public Reservation(Long customerId, Long restaurantId, Long tableId, Integer partySize, LocalDateTime reservationDatetime) {
        this();
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.partySize = partySize;
        this.reservationDatetime = reservationDatetime;
    }
    
    /**
     * Full constructor.
     */
    public Reservation(Long reservationId, Long customerId, Long restaurantId, Long tableId, 
                   Integer partySize, LocalDateTime reservationDatetime, ReservationStatus status, 
                   String specialRequests, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.partySize = partySize;
        this.reservationDatetime = reservationDatetime;
        this.status = status != null ? status : ReservationStatus.CONFIRMED;
        this.specialRequests = specialRequests;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }
    
    /**
     * Gets the reservation ID.
     * 
     * @return the reservation ID
     */
    public Long getReservationId() {
        return reservationId;
    }
    
    /**
     * Sets the reservation ID.
     * 
     * @param reservationId the reservation ID to set
     */
    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
    
    /**
     * Gets the customer ID making the reservation.
     * 
     * @return the customer ID
     */
    public Long getCustomerId() {
        return customerId;
    }
    
    /**
     * Sets the customer ID making the reservation.
     * 
     * @param customerId the customer ID to set
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    /**
     * Gets the restaurant ID for the reservation.
     * 
     * @return the restaurant ID
     */
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Sets the restaurant ID for the reservation.
     * 
     * @param restaurantId the restaurant ID to set
     */
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    /**
     * Gets the table ID for the reservation.
     * 
     * @return the table ID
     */
    public Long getTableId() {
        return tableId;
    }
    
    /**
     * Sets the table ID for the reservation.
     * 
     * @param tableId the table ID to set
     */
    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }
    
    /**
     * Gets the size of the reservation party.
     * 
     * @return the party size
     */
    public Integer getPartySize() {
        return partySize;
    }
    
    /**
     * Sets the size of the reservation party.
     * 
     * @param partySize the party size to set
     */
    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }
    
    /**
     * Gets the date and time of the reservation.
     * 
     * @return the reservation datetime
     */
    public LocalDateTime getReservationDatetime() {
        return reservationDatetime;
    }
    
    /**
     * Sets the date and time of the reservation.
     * 
     * @param reservationDatetime the reservation datetime to set
     */
    public void setReservationDatetime(LocalDateTime reservationDatetime) {
        this.reservationDatetime = reservationDatetime;
    }
    
    /**
     * Gets the reservation status.
     * 
     * @return the reservation status
     */
    public ReservationStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the reservation status.
     * 
     * @param status the reservation status to set
     */
    public void setStatus(ReservationStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now(); // Update timestamp when status changes
    }
    
    /**
     * Gets any special requests for the reservation.
     * 
     * @return the special requests, or null if not specified
     */
    public String getSpecialRequests() {
        return specialRequests;
    }
    
    /**
     * Sets any special requests for the reservation.
     * 
     * @param specialRequests the special requests to set, or null if not specified
     */
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
    
    /**
     * Gets the creation timestamp.
     * 
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp.
     * 
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the last update timestamp.
     * 
     * @return the update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the last update timestamp.
     * 
     * @param updatedAt the update timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Updates the last update timestamp to current time.
     * Call this method when making changes to the reservation.
     */
    public void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if the reservation is currently active (not cancelled, completed, or no-show).
     * 
     * @return true if the reservation is active, false otherwise
     */
    public boolean isActive() {
        return ReservationStatus.CONFIRMED.equals(status);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(reservationId, that.reservationId) &&
               Objects.equals(customerId, that.customerId) &&
               Objects.equals(restaurantId, that.restaurantId) &&
               Objects.equals(tableId, that.tableId) &&
               Objects.equals(partySize, that.partySize) &&
               Objects.equals(reservationDatetime, that.reservationDatetime) &&
               Objects.equals(status, that.status) &&
               Objects.equals(specialRequests, that.specialRequests) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(reservationId, customerId, restaurantId, tableId, partySize, 
                          reservationDatetime, status, specialRequests, createdAt, updatedAt);
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
               "reservationId=" + reservationId +
               ", customerId=" + customerId +
               ", restaurantId=" + restaurantId +
               ", tableId=" + tableId +
               ", partySize=" + partySize +
               ", reservationDatetime=" + reservationDatetime +
               ", status=" + status +
               ", specialRequests='" + specialRequests + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
