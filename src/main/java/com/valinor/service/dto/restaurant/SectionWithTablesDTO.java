package com.valinor.service.dto.restaurant;

import com.valinor.domain.model.Section;
import com.valinor.domain.model.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object representing a section with its tables.
 * Used for returning complete section information including nested tables.
 */
public class SectionWithTablesDTO {
    
    private Long sectionId;
    private Long restaurantId;
    private String name;
    private Integer numTables;
    private String notes;
    private List<Table> tables;
    
    /**
     * Default constructor.
     */
    public SectionWithTablesDTO() {
        this.tables = new ArrayList<>();
    }
    
    /**
     * Constructor from Section entity.
     * 
     * @param section the section entity
     */
    public SectionWithTablesDTO(Section section) {
        this.sectionId = section.getSectionId();
        this.restaurantId = section.getRestaurantId();
        this.name = section.getName();
        this.numTables = section.getNumTables();
        this.notes = section.getNotes();
        this.tables = new ArrayList<>();
    }
    
    /**
     * Constructor from Section entity with tables.
     * 
     * @param section the section entity
     * @param tables the list of tables in this section
     */
    public SectionWithTablesDTO(Section section, List<Table> tables) {
        this.sectionId = section.getSectionId();
        this.restaurantId = section.getRestaurantId();
        this.name = section.getName();
        this.numTables = section.getNumTables();
        this.notes = section.getNotes();
        this.tables = tables != null ? new ArrayList<>(tables) : new ArrayList<>();
    }
    
    /**
     * Gets the count of tables in this section.
     * 
     * @return the number of tables
     */
    public int getTableCount() {
        return tables.size();
    }
    
    /**
     * Gets the count of active tables in this section.
     * 
     * @return the number of active tables
     */
    public long getActiveTableCount() {
        return tables.stream().filter(Table::isActive).count();
    }
    
    /**
     * Gets the total seating capacity of all active tables.
     * 
     * @return the total capacity
     */
    public int getTotalCapacity() {
        return tables.stream()
                .filter(Table::isActive)
                .mapToInt(table -> table.getCapacity() != null ? table.getCapacity() : 0)
                .sum();
    }
    
    public Long getSectionId() {
        return sectionId;
    }
    
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
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
    
    public List<Table> getTables() {
        return tables;
    }
    
    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionWithTablesDTO that = (SectionWithTablesDTO) o;
        return Objects.equals(sectionId, that.sectionId) &&
               Objects.equals(restaurantId, that.restaurantId) &&
               Objects.equals(name, that.name) &&
               Objects.equals(numTables, that.numTables) &&
               Objects.equals(notes, that.notes) &&
               Objects.equals(tables, that.tables);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sectionId, restaurantId, name, numTables, notes, tables);
    }
    
    @Override
    public String toString() {
        return "SectionWithTablesDTO{" +
               "sectionId=" + sectionId +
               ", restaurantId=" + restaurantId +
               ", name='" + name + '\'' +
               ", numTables=" + numTables +
               ", notes='" + notes + '\'' +
               ", tables=" + tables.size() + " table(s)" +
               ", activeTableCount=" + getActiveTableCount() +
               ", totalCapacity=" + getTotalCapacity() +
               '}';
    }
}

