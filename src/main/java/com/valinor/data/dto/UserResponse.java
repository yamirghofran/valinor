package com.valinor.data.dto;

import com.valinor.data.entity.User;
import com.valinor.data.entity.UserRole;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object for user responses.
 * Excludes sensitive information like password hash.
 */
public class UserResponse {
    
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
    private Long restaurantId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    /**
     * Default constructor.
     */
    public UserResponse() {
    }
    
    /**
     * Creates a UserResponse from a User entity.
     * 
     * @param user the user entity
     * @return a UserResponse DTO
     */
    public static UserResponse fromUser(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setRestaurantId(user.getRestaurantId());
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setLastLogin(user.getLastLogin());
        
        return response;
    }
    
    /**
     * Gets the user ID.
     * 
     * @return the user ID
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * Sets the user ID.
     * 
     * @param userId the user ID to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
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
     * Gets the full name.
     * 
     * @return the full name (first + last)
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return "";
        }
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
     * @return true if active, false if inactive
     */
    public Boolean getIsActive() {
        return isActive;
    }
    
    /**
     * Sets the active status.
     * 
     * @param isActive the active status to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    /**
     * Gets the creation timestamp.
     * 
     * @return when the account was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp.
     * 
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the last login timestamp.
     * 
     * @return when the user last logged in
     */
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    /**
     * Sets the last login timestamp.
     * 
     * @param lastLogin the last login timestamp to set
     */
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponse that = (UserResponse) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(username, that.username) &&
               Objects.equals(email, that.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, username, email);
    }
    
    @Override
    public String toString() {
        return "UserResponse{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", phone='" + phone + '\'' +
               ", role=" + role +
               ", restaurantId=" + restaurantId +
               ", isActive=" + isActive +
               ", createdAt=" + createdAt +
               ", lastLogin=" + lastLogin +
               '}';
    }
}
