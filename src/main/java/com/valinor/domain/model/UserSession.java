package com.valinor.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an active user session in the restaurant reservation system.
 * Sessions are created upon successful login and used to authenticate subsequent requests.
 */
public class UserSession {
    
    private Long sessionId;
    private Long userId;
    private String sessionToken;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String ipAddress;
    private Boolean isActive;
    
    /**
     * Default constructor.
     */
    public UserSession() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param userId the user ID this session belongs to
     * @param sessionToken the unique session token
     * @param expiresAt when the session expires
     */
    public UserSession(Long userId, String sessionToken, LocalDateTime expiresAt) {
        this();
        this.userId = userId;
        this.sessionToken = sessionToken;
        this.expiresAt = expiresAt;
    }
    
    /**
     * Full constructor.
     * 
     * @param sessionId the session ID
     * @param userId the user ID this session belongs to
     * @param sessionToken the unique session token
     * @param createdAt when the session was created
     * @param expiresAt when the session expires
     * @param ipAddress the IP address of the client (optional)
     * @param isActive whether the session is active
     */
    public UserSession(Long sessionId, Long userId, String sessionToken, LocalDateTime createdAt,
                      LocalDateTime expiresAt, String ipAddress, Boolean isActive) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.sessionToken = sessionToken;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.ipAddress = ipAddress;
        this.isActive = isActive != null ? isActive : true;
    }
    
    /**
     * Gets the session ID.
     * 
     * @return the session ID
     */
    public Long getSessionId() {
        return sessionId;
    }
    
    /**
     * Sets the session ID.
     * 
     * @param sessionId the session ID to set
     */
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
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
     * Gets when the session was created.
     * 
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets when the session was created.
     * 
     * @param createdAt the creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets when the session expires.
     * 
     * @return the expiration timestamp
     */
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    /**
     * Sets when the session expires.
     * 
     * @param expiresAt the expiration timestamp to set
     */
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    /**
     * Gets the IP address associated with this session.
     * 
     * @return the IP address, or null if not recorded
     */
    public String getIpAddress() {
        return ipAddress;
    }
    
    /**
     * Sets the IP address associated with this session.
     * 
     * @param ipAddress the IP address to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    /**
     * Checks if the session is active.
     * 
     * @return true if the session is active, false otherwise
     */
    public Boolean getIsActive() {
        return isActive;
    }
    
    /**
     * Sets whether the session is active.
     * 
     * @param isActive true to activate the session, false to deactivate
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    /**
     * Checks if the session has expired based on the current time.
     * 
     * @return true if the session has expired, false otherwise
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * Checks if the session is valid (active and not expired).
     * 
     * @return true if the session is valid, false otherwise
     */
    public boolean isValid() {
        return isActive != null && isActive && !isExpired();
    }
    
    /**
     * Expires this session by setting it to inactive.
     */
    public void expire() {
        this.isActive = false;
    }
    
    /**
     * Gets the remaining time until the session expires.
     * 
     * @return the number of seconds until expiration, or 0 if already expired
     */
    public long getSecondsUntilExpiration() {
        if (expiresAt == null) {
            return 0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiresAt)) {
            return 0;
        }
        
        return java.time.Duration.between(now, expiresAt).getSeconds();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSession that = (UserSession) o;
        return Objects.equals(sessionId, that.sessionId) &&
               Objects.equals(sessionToken, that.sessionToken);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sessionId, sessionToken);
    }
    
    @Override
    public String toString() {
        return "UserSession{" +
               "sessionId=" + sessionId +
               ", userId=" + userId +
               ", sessionToken='" + (sessionToken != null ? sessionToken.substring(0, Math.min(8, sessionToken.length())) + "..." : "null") + '\'' +
               ", createdAt=" + createdAt +
               ", expiresAt=" + expiresAt +
               ", ipAddress='" + ipAddress + '\'' +
               ", isActive=" + isActive +
               ", isExpired=" + isExpired() +
               '}';
    }
}
