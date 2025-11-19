package com.valinor.data.dto;

import com.valinor.data.entity.UserRole;

import java.util.Objects;

/**
 * Data Transfer Object for user creation requests.
 * Contains all information needed to create a new user account.
 */
public class UserCreateRequest {
    
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
    private Long restaurantId;
    
    /**
     * Default constructor.
     */
    public UserCreateRequest() {
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param username the username
     * @param password the plain text password
     * @param email the email address
     * @param firstName the first name
     * @param lastName the last name
     * @param role the user role
     */
    public UserCreateRequest(String username, String password, String email, String firstName, String lastName, UserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
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
     * Gets the password.
     * 
     * @return the plain text password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password.
     * 
     * @param password the plain text password to set
     */
    public void setPassword(String password) {
        this.password = password;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCreateRequest that = (UserCreateRequest) o;
        return Objects.equals(username, that.username) &&
               Objects.equals(email, that.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }
    
    @Override
    public String toString() {
        return "UserCreateRequest{" +
               "username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", phone='" + phone + '\'' +
               ", role=" + role +
               ", restaurantId=" + restaurantId +
               '}';
    }
}
