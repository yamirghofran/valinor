package com.valinor.data.dto;

import java.util.Objects;

/**
 * Data Transfer Object for login requests.
 * Contains credentials for user authentication.
 */
public class LoginRequest {
    
    private String username;
    private String password;
    private String ipAddress;
    
    /**
     * Default constructor.
     */
    public LoginRequest() {
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param username the username
     * @param password the plain text password
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * Full constructor.
     * 
     * @param username the username
     * @param password the plain text password
     * @param ipAddress the client IP address
     */
    public LoginRequest(String username, String password, String ipAddress) {
        this.username = username;
        this.password = password;
        this.ipAddress = ipAddress;
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
     * Gets the IP address.
     * 
     * @return the client IP address
     */
    public String getIpAddress() {
        return ipAddress;
    }
    
    /**
     * Sets the IP address.
     * 
     * @param ipAddress the client IP address to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(username, that.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
    
    @Override
    public String toString() {
        return "LoginRequest{" +
               "username='" + username + '\'' +
               ", ipAddress='" + ipAddress + '\'' +
               '}';
    }
}
