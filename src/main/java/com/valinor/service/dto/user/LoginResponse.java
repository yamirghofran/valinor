package com.valinor.service.dto.user;

import com.valinor.domain.enums.Permission;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Data Transfer Object for login responses.
 * Contains session information and user details after successful authentication.
 */
public class LoginResponse {
    
    private String sessionToken;
    private UserResponse user;
    private LocalDateTime expiresAt;
    private Set<Permission> permissions;
    
    /**
     * Default constructor.
     */
    public LoginResponse() {
    }
    
    /**
     * Full constructor.
     * 
     * @param sessionToken the session token
     * @param user the user information
     * @param expiresAt when the session expires
     * @param permissions the user's permissions
     */
    public LoginResponse(String sessionToken, UserResponse user, LocalDateTime expiresAt, Set<Permission> permissions) {
        this.sessionToken = sessionToken;
        this.user = user;
        this.expiresAt = expiresAt;
        this.permissions = permissions;
    }
    
    /**
     * Gets the session token.
     * 
     * @return the session token
     */
    public String getSessionToken() {
        return sessionToken;
    }
    
    /**
     * Sets the session token.
     * 
     * @param sessionToken the session token to set
     */
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
    
    /**
     * Gets the user information.
     * 
     * @return the user response DTO
     */
    public UserResponse getUser() {
        return user;
    }
    
    /**
     * Sets the user information.
     * 
     * @param user the user response DTO to set
     */
    public void setUser(UserResponse user) {
        this.user = user;
    }
    
    /**
     * Gets the session expiration time.
     * 
     * @return when the session expires
     */
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    /**
     * Sets the session expiration time.
     * 
     * @param expiresAt when the session expires
     */
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    /**
     * Gets the user's permissions.
     * 
     * @return the set of permissions
     */
    public Set<Permission> getPermissions() {
        return permissions;
    }
    
    /**
     * Sets the user's permissions.
     * 
     * @param permissions the set of permissions to set
     */
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginResponse that = (LoginResponse) o;
        return Objects.equals(sessionToken, that.sessionToken) &&
               Objects.equals(user, that.user);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sessionToken, user);
    }
    
    @Override
    public String toString() {
        return "LoginResponse{" +
               "sessionToken='" + (sessionToken != null ? sessionToken.substring(0, Math.min(8, sessionToken.length())) + "..." : "null") + '\'' +
               ", user=" + user +
               ", expiresAt=" + expiresAt +
               ", permissionCount=" + (permissions != null ? permissions.size() : 0) +
               '}';
    }
}
