package com.valinor.domain.model;

import java.util.Objects;

/**
 * Represents a physical subdivision of a restaurant (such as patio, bar, dining room).
 */
public class Section {
    
    private Long sectionId;
    private Long restaurantId;
    private String name;
    private Integer numTables;
    private String notes;
    
    /**
     * Default constructor.
     */
    public Section() {
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param restaurantId the restaurant ID this section belongs to
     * @param name the section name
     */
    public Section(Long restaurantId, String name) {
        this.restaurantId = restaurantId;
        this.name = name;
    }
    
    /**
     * Full constructor.
     * 
     * @param sectionId the section ID
     * @param restaurantId the restaurant ID this section belongs to
     * @param name the section name
     * @param numTables the number of tables in this section (optional)
     * @param notes additional notes about this section (optional)
     */
    public Section(Long sectionId, Long restaurantId, String name, Integer numTables, String notes) {
        this.sectionId = sectionId;
        this.restaurantId = restaurantId;
        this.name = name;
        this.numTables = numTables;
        this.notes = notes;
    }
    
    /**
     * Gets the section ID.
     * 
     * @return the section ID
     */
    public Long getSectionId() {
        return sectionId;
    }
    
    /**
     * Sets the section ID.
     * 
     * @param sectionId the section ID to set
     */
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }
    
    /**
     * Gets the restaurant ID this section belongs to.
     * 
     * @return the restaurant ID
     */
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Sets the restaurant ID this section belongs to.
     * 
     * @param restaurantId the restaurant ID to set
     */
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    /**
     * Gets the section name.
     * 
     * @return the section name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the section name.
     * 
     * @param name the section name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the number of tables in this section.
     * 
     * @return the number of tables, or null if not specified
     */
    public Integer getNumTables() {
        return numTables;
    }
    
    /**
     * Sets the number of tables in this section.
     * 
     * @param numTables the number of tables to set, or null if not specified
     */
    public void setNumTables(Integer numTables) {
        this.numTables = numTables;
    }
    
    /**
     * Gets additional notes about this section.
     * 
     * @return the notes, or null if not specified
     */
    public String getNotes() {
        return notes;
    }
    
    /**
     * Sets additional notes about this section.
     * 
     * @param notes the notes to set, or null if not specified
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(sectionId, section.sectionId) &&
               Objects.equals(restaurantId, section.restaurantId) &&
               Objects.equals(name, section.name) &&
               Objects.equals(numTables, section.numTables) &&
               Objects.equals(notes, section.notes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sectionId, restaurantId, name, numTables, notes);
    }
    
    @Override
    public String toString() {
        return "Section{" +
               "sectionId=" + sectionId +
               ", restaurantId=" + restaurantId +
               ", name='" + name + '\'' +
               ", numTables=" + numTables +
               ", notes='" + notes + '\'' +
               '}';
    }
}