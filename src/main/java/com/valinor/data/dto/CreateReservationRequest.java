package com.valinor.data.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object for creating a new reservation.
 * Contains all required and optional fields for reservation creation.
 */
public class CreateReservationRequest {
    
    private Long customerId;
    private Long restaurantId;
    private Long tableId; // Optional for auto-assignment
    private Integer partySize;
    private LocalDateTime reservationDatetime;
    private String specialRequests;
    
    /**
     * Default constructor.
     */
    public CreateReservationRequest() {
    }
    
    /**
     * Constructor with required fields (without table for auto-assignment).
     * 
     * @param customerId customer ID making the reservation
     * @param restaurantId restaurant ID for the reservation
     * @param partySize size of the party
     * @param reservationDatetime date and time of the reservation
     */
    public CreateReservationRequest(Long customerId, Long restaurantId, Integer partySize, 
                                    LocalDateTime reservationDatetime) {
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.partySize = partySize;
        this.reservationDatetime = reservationDatetime;
    }
    
    /**
     * Constructor with specific table (without special requests).
     * 
     * @param customerId customer ID making the reservation
     * @param restaurantId restaurant ID for the reservation
     * @param tableId table ID for the reservation
     * @param partySize size of the party
     * @param reservationDatetime date and time of the reservation
     */
    public CreateReservationRequest(Long customerId, Long restaurantId, Long tableId, 
                                    Integer partySize, LocalDateTime reservationDatetime) {
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.partySize = partySize;
        this.reservationDatetime = reservationDatetime;
    }
    
    /**
     * Full constructor with specific table and special requests.
     * 
     * @param customerId customer ID making the reservation
     * @param restaurantId restaurant ID for the reservation
     * @param tableId table ID for the reservation
     * @param partySize size of the party
     * @param reservationDatetime date and time of the reservation
     * @param specialRequests special requests (optional)
     */
    public CreateReservationRequest(Long customerId, Long restaurantId, Long tableId, 
                                    Integer partySize, LocalDateTime reservationDatetime, 
                                    String specialRequests) {
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.partySize = partySize;
        this.reservationDatetime = reservationDatetime;
        this.specialRequests = specialRequests;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public Long getTableId() {
        return tableId;
    }
    
    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }
    
    public Integer getPartySize() {
        return partySize;
    }
    
    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }
    
    public LocalDateTime getReservationDatetime() {
        return reservationDatetime;
    }
    
    public void setReservationDatetime(LocalDateTime reservationDatetime) {
        this.reservationDatetime = reservationDatetime;
    }
    
    public String getSpecialRequests() {
        return specialRequests;
    }
    
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateReservationRequest that = (CreateReservationRequest) o;
        return Objects.equals(customerId, that.customerId) &&
               Objects.equals(restaurantId, that.restaurantId) &&
               Objects.equals(tableId, that.tableId) &&
               Objects.equals(partySize, that.partySize) &&
               Objects.equals(reservationDatetime, that.reservationDatetime) &&
               Objects.equals(specialRequests, that.specialRequests);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customerId, restaurantId, tableId, partySize, reservationDatetime, specialRequests);
    }
    
    @Override
    public String toString() {
        return "CreateReservationRequest{" +
               "customerId=" + customerId +
               ", restaurantId=" + restaurantId +
               ", tableId=" + tableId +
               ", partySize=" + partySize +
               ", reservationDatetime=" + reservationDatetime +
               ", specialRequests='" + specialRequests + '\'' +
               '}';
    }
}
