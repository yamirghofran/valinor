package com.valinor.domain.model;

import java.util.Objects;

/**
 * Represents individual tables within a section.
 */
public class Table {
    
    private Long tableId;
    private Long sectionId;
    private String tableNumber;
    private Integer capacity;
    private Boolean isActive;
    
    /**
     * Default constructor.
     */
    public Table() {
        this.isActive = true; // Default to active
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param sectionId the section ID this table belongs to
     * @param tableNumber the table number/identifier
     * @param capacity the seating capacity of the table
     */
    public Table(Long sectionId, String tableNumber, Integer capacity) {
        this.sectionId = sectionId;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.isActive = true; // Default to active
    }
    
    /**
     * Full constructor.
     * 
     * @param tableId the table ID
     * @param sectionId the section ID this table belongs to
     * @param tableNumber the table number/identifier
     * @param capacity the seating capacity of the table
     * @param isActive whether the table is currently active
     */
    public Table(Long tableId, Long sectionId, String tableNumber, Integer capacity, Boolean isActive) {
        this.tableId = tableId;
        this.sectionId = sectionId;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.isActive = isActive != null ? isActive : true;
    }
    
    /**
     * Gets the table ID.
     * 
     * @return the table ID
     */
    public Long getTableId() {
        return tableId;
    }
    
    /**
     * Sets the table ID.
     * 
     * @param tableId the table ID to set
     */
    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }
    
    /**
     * Gets the section ID this table belongs to.
     * 
     * @return the section ID
     */
    public Long getSectionId() {
        return sectionId;
    }
    
    /**
     * Sets the section ID this table belongs to.
     * 
     * @param sectionId the section ID to set
     */
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }
    
    /**
     * Gets the table number/identifier.
     * 
     * @return the table number
     */
    public String getTableNumber() {
        return tableNumber;
    }
    
    /**
     * Sets the table number/identifier.
     * 
     * @param tableNumber the table number to set
     */
    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    /**
     * Gets the seating capacity of the table.
     * 
     * @return the capacity
     */
    public Integer getCapacity() {
        return capacity;
    }
    
    /**
     * Sets the seating capacity of the table.
     * 
     * @param capacity the capacity to set
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    /**
     * Gets whether the table is currently active.
     * 
     * @return true if active, false otherwise
     */
    public Boolean getIsActive() {
        return isActive;
    }
    
    /**
     * Sets whether the table is currently active.
     * 
     * @param isActive true if active, false otherwise
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    /**
     * Convenience method to check if table is active.
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(tableId, table.tableId) &&
               Objects.equals(sectionId, table.sectionId) &&
               Objects.equals(tableNumber, table.tableNumber) &&
               Objects.equals(capacity, table.capacity) &&
               Objects.equals(isActive, table.isActive);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(tableId, sectionId, tableNumber, capacity, isActive);
    }
    
    @Override
    public String toString() {
        return "Table{" +
               "tableId=" + tableId +
               ", sectionId=" + sectionId +
               ", tableNumber='" + tableNumber + '\'' +
               ", capacity=" + capacity +
               ", isActive=" + isActive +
               '}';
    }
}