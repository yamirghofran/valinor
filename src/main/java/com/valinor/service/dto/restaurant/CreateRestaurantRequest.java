package com.valinor.service.dto.restaurant;

/**
 * Data Transfer Object for creating a new restaurant with its admin user.
 */
public class CreateRestaurantRequest {
    
    // Restaurant fields
    private String restaurantName;
    private String location;
    private String contactEmail;
    private String contactPhone;
    
    // Admin user fields
    private String adminUsername;
    private String adminPassword;
    private String adminEmail;
    private String adminFirstName;
    private String adminLastName;
    private String adminPhone;
    
    /**
     * Default constructor.
     */
    public CreateRestaurantRequest() {
    }
    
    // Restaurant getters and setters
    
    public String getRestaurantName() {
        return restaurantName;
    }
    
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
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
    
    // Admin user getters and setters
    
    public String getAdminUsername() {
        return adminUsername;
    }
    
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }
    
    public String getAdminPassword() {
        return adminPassword;
    }
    
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
    
    public String getAdminEmail() {
        return adminEmail;
    }
    
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
    
    public String getAdminFirstName() {
        return adminFirstName;
    }
    
    public void setAdminFirstName(String adminFirstName) {
        this.adminFirstName = adminFirstName;
    }
    
    public String getAdminLastName() {
        return adminLastName;
    }
    
    public void setAdminLastName(String adminLastName) {
        this.adminLastName = adminLastName;
    }
    
    public String getAdminPhone() {
        return adminPhone;
    }
    
    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }
}
