package com.valinor.data.entity;

import java.util.Objects;

/**
 * Represents a restaurant using the reservation system.
 */
public class Restaurant {
    
    private Long restaurantId;
    private String name;
    private String location;
    private String contactEmail;
    private String contactPhone;
    
    /**
     * Default constructor.
     */
    public Restaurant() {
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param name the restaurant name
     * @param location the restaurant location
     * @param contactEmail the contact email
     * @param contactPhone the contact phone
     */
    public Restaurant(String name, String location, String contactEmail, String contactPhone) {
        this.name = name;
        this.location = location;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }
    
    /**
     * Full constructor.
     * 
     * @param restaurantId the restaurant ID
     * @param name the restaurant name
     * @param location the restaurant location
     * @param contactEmail the contact email
     * @param contactPhone the contact phone
     */
    public Restaurant(Long restaurantId, String name, String location, String contactEmail, String contactPhone) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.location = location;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }
    
    /**
     * Gets the restaurant ID.
     * 
     * @return the restaurant ID
     */
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Sets the restaurant ID.
     * 
     * @param restaurantId the restaurant ID to set
     */
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    /**
     * Gets the restaurant name.
     * 
     * @return the restaurant name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the restaurant name.
     * 
     * @param name the restaurant name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the restaurant location.
     * 
     * @return the restaurant location
     */
    public String getLocation() {
        return location;
    }
    
    /**
     * Sets the restaurant location.
     * 
     * @param location the restaurant location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }
    
    /**
     * Gets the contact email.
     * 
     * @return the contact email
     */
    public String getContactEmail() {
        return contactEmail;
    }
    
    /**
     * Sets the contact email.
     * 
     * @param contactEmail the contact email to set
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    /**
     * Gets the contact phone.
     * 
     * @return the contact phone
     */
    public String getContactPhone() {
        return contactPhone;
    }
    
    /**
     * Sets the contact phone.
     * 
     * @param contactPhone the contact phone to set
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(restaurantId, that.restaurantId) &&
               Objects.equals(name, that.name) &&
               Objects.equals(location, that.location) &&
               Objects.equals(contactEmail, that.contactEmail) &&
               Objects.equals(contactPhone, that.contactPhone);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, name, location, contactEmail, contactPhone);
    }
    
    @Override
    public String toString() {
        return "Restaurant{" +
               "restaurantId=" + restaurantId +
               ", name='" + name + '\'' +
               ", location='" + location + '\'' +
               ", contactEmail='" + contactEmail + '\'' +
               ", contactPhone='" + contactPhone + '\'' +
               '}';
    }
}