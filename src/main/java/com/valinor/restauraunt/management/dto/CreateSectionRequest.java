package com.valinor.restauraunt.management.dto;

import java.util.Objects;

/**
 * Request object for creating a new section in a restaurant.
 */
public class CreateSectionRequest {
    
    private Long restaurantId;
    private String name;
    private Integer numTables;
    private String notes;
    
    /**
     * Default constructor.
     */
    public CreateSectionRequest() {
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param restaurantId the restaurant ID
     * @param name the section name
     */
    public CreateSectionRequest(Long restaurantId, String name) {
        this.restaurantId = restaurantId;
        this.name = name;
    }
    
    /**
     * Full constructor.
     * 
     * @param restaurantId the restaurant ID
     * @param name the section name
     * @param numTables the number of tables (optional)
     * @param notes additional notes (optional)
     */
    public CreateSectionRequest(Long restaurantId, String name, Integer numTables, String notes) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.numTables = numTables;
        this.notes = notes;
    }
    
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getNumTables() {
        return numTables;
    }
    
    public void setNumTables(Integer numTables) {
        this.numTables = numTables;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateSectionRequest that = (CreateSectionRequest) o;
        return Objects.equals(restaurantId, that.restaurantId) &&
               Objects.equals(name, that.name) &&
               Objects.equals(numTables, that.numTables) &&
               Objects.equals(notes, that.notes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, name, numTables, notes);
    }
    
    @Override
    public String toString() {
        return "CreateSectionRequest{" +
               "restaurantId=" + restaurantId +
               ", name='" + name + '\'' +
               ", numTables=" + numTables +
               ", notes='" + notes + '\'' +
               '}';
    }
}

