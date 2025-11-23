package com.valinor.service.dto.restaurant;

import java.util.Objects;

/**
 * Request object for updating an existing table.
 */
public class UpdateTableRequest {
    
    private String tableNumber;
    private Integer capacity;
    private Boolean isActive;
    
    /**
     * Default constructor.
     */
    public UpdateTableRequest() {
    }
    
    /**
     * Constructor with capacity.
     * 
     * @param capacity the new capacity
     */
    public UpdateTableRequest(Integer capacity) {
        this.capacity = capacity;
    }
    
    public String getTableNumber() {
        return tableNumber;
    }
    
    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateTableRequest that = (UpdateTableRequest) o;
        return Objects.equals(tableNumber, that.tableNumber) &&
               Objects.equals(capacity, that.capacity) &&
               Objects.equals(isActive, that.isActive);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(tableNumber, capacity, isActive);
    }
    
    @Override
    public String toString() {
        return "UpdateTableRequest{" +
               "tableNumber='" + tableNumber + '\'' +
               ", capacity=" + capacity +
               ", isActive=" + isActive +
               '}';
    }
}

