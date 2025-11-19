package com.valinor.data.dto;

import com.valinor.data.entity.UserRole;

import java.util.Objects;

/**
 * Data Transfer Object for user update requests.
 * All fields are optional - only provided fields will be updated.
 * Username cannot be changed.
 */
public class UserUpdateRequest {
    
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
    private Long restaurantId;
    private Boolean isActive;
    
    /**
     * Default constructor.
     */
    public UserUpdateRequest() {
    }
    
    /**
     * Gets the email address.
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email address.
     * 
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the first name.
     * 
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Sets the first name.
     * 
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Gets the last name.
     * 
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Sets the last name.
     * 
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Gets the phone number.
     * 
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Sets the phone number.
     * 
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * Gets the user role.
     * 
     * @return the user role
     */
    public UserRole getRole() {
        return role;
    }
    
    /**
     * Sets the user role.
     * 
     * @param role the user role to set
     */
    public void setRole(UserRole role) {
        this.role = role;
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
     * Gets the active status.
     * 
     * @return true if active, false if inactive, null if not set
     */
    public Boolean getIsActive() {
        return isActive;
    }
    
    /**
     * Sets the active status.
     * 
     * @param isActive true to activate, false to deactivate
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    /**
     * Checks if any fields are set for update.
     * 
     * @return true if at least one field is set
     */
    public boolean hasUpdates() {
        return email != null || firstName != null || lastName != null || 
               phone != null || role != null || restaurantId != null || isActive != null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserUpdateRequest that = (UserUpdateRequest) o;
        return Objects.equals(email, that.email) &&
               Objects.equals(firstName, that.firstName) &&
               Objects.equals(lastName, that.lastName) &&
               Objects.equals(phone, that.phone) &&
               role == that.role &&
               Objects.equals(restaurantId, that.restaurantId) &&
               Objects.equals(isActive, that.isActive);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email, firstName, lastName, phone, role, restaurantId, isActive);
    }
    
    @Override
    public String toString() {
        return "UserUpdateRequest{" +
               "email='" + email + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", phone='" + phone + '\'' +
               ", role=" + role +
               ", restaurantId=" + restaurantId +
               ", isActive=" + isActive +
               '}';
    }
}
