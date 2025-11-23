package com.valinor.service.dto.reservation;

import com.valinor.domain.model.Reservation;
import com.valinor.domain.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object for reservation responses.
 * Contains reservation information to be returned to clients.
 */
public class ReservationResponse {
    
    private Long reservationId;
    private Long customerId;
    private String customerName;
    private Long restaurantId;
    private String restaurantName;
    private Long tableId;
    private String tableNumber;
    private Integer partySize;
    private LocalDateTime reservationDatetime;
    private ReservationStatus status;
    private String specialRequests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor.
     */
    public ReservationResponse() {
    }
    
    /**
     * Creates a ReservationResponse from a Reservation entity.
     * Basic version without related entity information.
     * 
     * @param reservation the reservation entity
     * @return the reservation response DTO
     */
    public static ReservationResponse fromReservation(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        
        ReservationResponse response = new ReservationResponse();
        response.setReservationId(reservation.getReservationId());
        response.setCustomerId(reservation.getCustomerId());
        response.setRestaurantId(reservation.getRestaurantId());
        response.setTableId(reservation.getTableId());
        response.setPartySize(reservation.getPartySize());
        response.setReservationDatetime(reservation.getReservationDatetime());
        response.setStatus(reservation.getStatus());
        response.setSpecialRequests(reservation.getSpecialRequests());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setUpdatedAt(reservation.getUpdatedAt());
        
        return response;
    }
    
    public Long getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public String getRestaurantName() {
        return restaurantName;
    }
    
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    
    public Long getTableId() {
        return tableId;
    }
    
    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }
    
    public String getTableNumber() {
        return tableNumber;
    }
    
    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
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
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public String getSpecialRequests() {
        return specialRequests;
    }
    
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationResponse that = (ReservationResponse) o;
        return Objects.equals(reservationId, that.reservationId) &&
               Objects.equals(customerId, that.customerId) &&
               Objects.equals(customerName, that.customerName) &&
               Objects.equals(restaurantId, that.restaurantId) &&
               Objects.equals(restaurantName, that.restaurantName) &&
               Objects.equals(tableId, that.tableId) &&
               Objects.equals(tableNumber, that.tableNumber) &&
               Objects.equals(partySize, that.partySize) &&
               Objects.equals(reservationDatetime, that.reservationDatetime) &&
               Objects.equals(status, that.status) &&
               Objects.equals(specialRequests, that.specialRequests) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(reservationId, customerId, customerName, restaurantId, restaurantName,
                          tableId, tableNumber, partySize, reservationDatetime, status,
                          specialRequests, createdAt, updatedAt);
    }
    
    @Override
    public String toString() {
        return "ReservationResponse{" +
               "reservationId=" + reservationId +
               ", customerId=" + customerId +
               ", customerName='" + customerName + '\'' +
               ", restaurantId=" + restaurantId +
               ", restaurantName='" + restaurantName + '\'' +
               ", tableId=" + tableId +
               ", tableNumber='" + tableNumber + '\'' +
               ", partySize=" + partySize +
               ", reservationDatetime=" + reservationDatetime +
               ", status=" + status +
               ", specialRequests='" + specialRequests + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}
