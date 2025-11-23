package com.valinor.service.dto.restaurant;

import java.util.Objects;

/**
 * Request object for updating an existing section.
 */
public class UpdateSectionRequest {
    
    private String name;
    private Integer numTables;
    private String notes;
    
    /**
     * Default constructor.
     */
    public UpdateSectionRequest() {
    }
    
    /**
     * Constructor with name.
     * 
     * @param name the new section name
     */
    public UpdateSectionRequest(String name) {
        this.name = name;
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
        UpdateSectionRequest that = (UpdateSectionRequest) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(numTables, that.numTables) &&
               Objects.equals(notes, that.notes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, numTables, notes);
    }
    
    @Override
    public String toString() {
        return "UpdateSectionRequest{" +
               "name='" + name + '\'' +
               ", numTables=" + numTables +
               ", notes='" + notes + '\'' +
               '}';
    }
}

