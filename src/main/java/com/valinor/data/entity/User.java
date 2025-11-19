package com.valinor.data.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a user account in the restaurant reservation system.
 * Users have roles that determine their permissions and access levels.
 */
public class User {
    
    private Long userId;
    private String username;
    private String passwordHash;
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
    public User() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param username the unique username
     * @param passwordHash the hashed password
     * @param email the user's email address
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param role the user's role
     */
    public User(String username, String passwordHash, String email, String firstName, String lastName, UserRole role) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
    
    /**
     * Full constructor.
     * 
     * @param userId the user ID
     * @param username the unique username
     * @param passwordHash the hashed password
     * @param email the user's email address
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param phone the user's phone number
     * @param role the user's role
     * @param restaurantId the associated restaurant ID
     * @param isActive whether the user account is active
     * @param createdAt when the account was created
     * @param lastLogin when the user last logged in
     */
    public User(Long userId, String username, String passwordHash, String email, String firstName, 
                String lastName, String phone, UserRole role, Long restaurantId, Boolean isActive, 
                LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
        this.restaurantId = restaurantId;
        this.isActive = isActive != null ? isActive : true;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.lastLogin = lastLogin;
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
     * Gets the password hash.
     * 
     * @return the password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * Sets the password hash.
     * 
     * @param passwordHash the password hash to set
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
     * Gets the user's full name (first + last name).
     * 
     * @return the full name
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
     * @return the phone number, or null if not set
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
     * Gets the user's role.
     * 
     * @return the user role
     */
    public UserRole getRole() {
        return role;
    }
    
    /**
     * Sets the user's role.
     * 
     * @param role the role to set
     */
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    /**
     * Gets the associated restaurant ID.
     * 
     * @return the restaurant ID, or null if not associated with a restaurant
     */
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Sets the associated restaurant ID.
     * 
     * @param restaurantId the restaurant ID to set
     */
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    /**
     * Checks if the user account is active.
     * 
     * @return true if the account is active, false otherwise
     */
    public Boolean getIsActive() {
        return isActive;
    }
    
    /**
     * Sets whether the user account is active.
     * 
     * @param isActive true to activate the account, false to deactivate
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    /**
     * Gets when the account was created.
     * 
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets when the account was created.
     * 
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets when the user last logged in.
     * 
     * @return the last login timestamp, or null if never logged in
     */
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    /**
     * Sets when the user last logged in.
     * 
     * @param lastLogin the last login timestamp to set
     */
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    /**
     * Gets the permissions for this user based on their role.
     * 
     * @return a set of permissions, or empty set if role is null
     */
    public Set<Permission> getPermissions() {
        return role != null ? role.getDefaultPermissions() : Set.of();
    }
    
    /**
     * Checks if this user has a specific permission.
     * 
     * @param permission the permission to check
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(Permission permission) {
        return role != null && role.hasPermission(permission);
    }
    
    /**
     * Checks if the user account is currently usable.
     * An account is usable if it is active and has a valid role.
     * 
     * @return true if the account is usable, false otherwise
     */
    public boolean isUsable() {
        return isActive != null && isActive && role != null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
               Objects.equals(username, user.username) &&
               Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, username, email);
    }
    
    @Override
    public String toString() {
        return "User{" +
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
    
    /**
     * Returns a string representation without sensitive information.
     * Excludes the password hash for security.
     * 
     * @return a safe string representation
     */
    public String toSafeString() {
        return "User{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", fullName='" + getFullName() + '\'' +
               ", role=" + role +
               ", isActive=" + isActive +
               '}';
    }
}
