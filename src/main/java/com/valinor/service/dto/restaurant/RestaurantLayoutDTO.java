package com.valinor.service.dto.restaurant;

import com.valinor.domain.model.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object representing a complete restaurant layout.
 * Includes the restaurant information with all sections and their tables.
 */
public class RestaurantLayoutDTO {
    
    private Long restaurantId;
    private String name;
    private String location;
    private String contactEmail;
    private String contactPhone;
    private List<SectionWithTablesDTO> sections;
    
    /**
     * Default constructor.
     */
    public RestaurantLayoutDTO() {
        this.sections = new ArrayList<>();
    }
    
    /**
     * Constructor from Restaurant entity.
     * 
     * @param restaurant the restaurant entity
     */
    public RestaurantLayoutDTO(Restaurant restaurant) {
        this.restaurantId = restaurant.getRestaurantId();
        this.name = restaurant.getName();
        this.location = restaurant.getLocation();
        this.contactEmail = restaurant.getContactEmail();
        this.contactPhone = restaurant.getContactPhone();
        this.sections = new ArrayList<>();
    }
    
    /**
     * Constructor from Restaurant entity with sections.
     * 
     * @param restaurant the restaurant entity
     * @param sections the list of sections with tables
     */
    public RestaurantLayoutDTO(Restaurant restaurant, List<SectionWithTablesDTO> sections) {
        this.restaurantId = restaurant.getRestaurantId();
        this.name = restaurant.getName();
        this.location = restaurant.getLocation();
        this.contactEmail = restaurant.getContactEmail();
        this.contactPhone = restaurant.getContactPhone();
        this.sections = sections != null ? new ArrayList<>(sections) : new ArrayList<>();
    }
    
    /**
     * Gets the total number of sections.
     * 
     * @return the number of sections
     */
    public int getSectionCount() {
        return sections.size();
    }
    
    /**
     * Gets the total number of tables across all sections.
     * 
     * @return the total number of tables
     */
    public int getTotalTableCount() {
        return sections.stream()
                .mapToInt(SectionWithTablesDTO::getTableCount)
                .sum();
    }
    
    /**
     * Gets the total number of active tables across all sections.
     * 
     * @return the total number of active tables
     */
    public long getTotalActiveTableCount() {
        return sections.stream()
                .mapToLong(SectionWithTablesDTO::getActiveTableCount)
                .sum();
    }
    
    /**
     * Gets the total seating capacity across all active tables.
     * 
     * @return the total capacity
     */
    public int getTotalCapacity() {
        return sections.stream()
                .mapToInt(SectionWithTablesDTO::getTotalCapacity)
                .sum();
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
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public List<SectionWithTablesDTO> getSections() {
        return sections;
    }
    
    public void setSections(List<SectionWithTablesDTO> sections) {
        this.sections = sections;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantLayoutDTO that = (RestaurantLayoutDTO) o;
        return Objects.equals(restaurantId, that.restaurantId) &&
               Objects.equals(name, that.name) &&
               Objects.equals(location, that.location) &&
               Objects.equals(contactEmail, that.contactEmail) &&
               Objects.equals(contactPhone, that.contactPhone) &&
               Objects.equals(sections, that.sections);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, name, location, contactEmail, contactPhone, sections);
    }
    
    @Override
    public String toString() {
        return "RestaurantLayoutDTO{" +
               "restaurantId=" + restaurantId +
               ", name='" + name + '\'' +
               ", location='" + location + '\'' +
               ", sectionCount=" + getSectionCount() +
               ", totalTables=" + getTotalTableCount() +
               ", activeTables=" + getTotalActiveTableCount() +
               ", totalCapacity=" + getTotalCapacity() +
               '}';
    }
}

