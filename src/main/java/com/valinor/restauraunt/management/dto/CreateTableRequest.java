package com.valinor.restauraunt.management.dto;

import java.util.Objects;

/**
 * Request object for creating a new table in a section.
 */
public class CreateTableRequest {
    
    private Long sectionId;
    private String tableNumber;
    private Integer capacity;
    private Boolean isActive;
    
    /**
     * Default constructor.
     */
    public CreateTableRequest() {
        this.isActive = true; // Default to active
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param sectionId the section ID
     * @param tableNumber the table number
     * @param capacity the seating capacity
     */
    public CreateTableRequest(Long sectionId, String tableNumber, Integer capacity) {
        this.sectionId = sectionId;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.isActive = true;
    }
    
    /**
     * Full constructor.
     * 
     * @param sectionId the section ID
     * @param tableNumber the table number
     * @param capacity the seating capacity
     * @param isActive whether the table is active
     */
    public CreateTableRequest(Long sectionId, String tableNumber, Integer capacity, Boolean isActive) {
        this.sectionId = sectionId;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.isActive = isActive != null ? isActive : true;
    }
    
    public Long getSectionId() {
        return sectionId;
    }
    
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
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
        CreateTableRequest that = (CreateTableRequest) o;
        return Objects.equals(sectionId, that.sectionId) &&
               Objects.equals(tableNumber, that.tableNumber) &&
               Objects.equals(capacity, that.capacity) &&
               Objects.equals(isActive, that.isActive);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sectionId, tableNumber, capacity, isActive);
    }
    
    @Override
    public String toString() {
        return "CreateTableRequest{" +
               "sectionId=" + sectionId +
               ", tableNumber='" + tableNumber + '\'' +
               ", capacity=" + capacity +
               ", isActive=" + isActive +
               '}';
    }
}

