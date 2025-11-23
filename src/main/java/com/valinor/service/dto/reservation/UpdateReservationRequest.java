package com.valinor.service.dto.reservation;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object for updating an existing reservation.
 * All fields are optional - only provided fields will be updated.
 */
public class UpdateReservationRequest {
    
    private Long tableId;
    private Integer partySize;
    private LocalDateTime reservationDatetime;
    private String specialRequests;
    
    /**
     * Default constructor.
     */
    public UpdateReservationRequest() {
    }
    
    /**
     * Checks if this request has any updates.
     * 
     * @return true if at least one field is set
     */
    public boolean hasUpdates() {
        return tableId != null || partySize != null || 
               reservationDatetime != null || specialRequests != null;
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
        UpdateReservationRequest that = (UpdateReservationRequest) o;
        return Objects.equals(tableId, that.tableId) &&
               Objects.equals(partySize, that.partySize) &&
               Objects.equals(reservationDatetime, that.reservationDatetime) &&
               Objects.equals(specialRequests, that.specialRequests);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(tableId, partySize, reservationDatetime, specialRequests);
    }
    
    @Override
    public String toString() {
        return "UpdateReservationRequest{" +
               "tableId=" + tableId +
               ", partySize=" + partySize +
               ", reservationDatetime=" + reservationDatetime +
               ", specialRequests='" + specialRequests + '\'' +
               '}';
    }
}
