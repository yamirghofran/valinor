package com.valinor.ui.util;

import com.valinor.domain.model.UserSession;
import com.valinor.service.dto.user.UserResponse;

/**
 * Manages user session state across the JavaFX application
 */
public class SessionManager {

    private static SessionManager instance;
    private UserSession currentSession;
    private UserResponse currentUser;

    private SessionManager() {
    }

    /**
     * Gets the singleton instance
     * @return SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Sets the current session
     * @param session user session
     * @param user user details
     */
    public void setSession(UserSession session, UserResponse user) {
        this.currentSession = session;
        this.currentUser = user;
    }

    /**
     * Gets the current session
     * @return current session or null if not logged in
     */
    public UserSession getCurrentSession() {
        return currentSession;
    }

    /**
     * Gets the current user
     * @return current user or null if not logged in
     */
    public UserResponse getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is logged in
     * @return true if logged in
     */
    public boolean isLoggedIn() {
        return currentSession != null && currentUser != null;
    }

    /**
     * Clears the session (logout)
     */
    public void clearSession() {
        this.currentSession = null;
        this.currentUser = null;
    }

    /**
     * Gets the session token
     * @return session token or null
     */
    public String getSessionToken() {
        return currentSession != null ? currentSession.getSessionToken() : null;
    }

    /**
     * Gets the current user ID
     * @return user ID or null
     */
    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }

    /**
     * Gets the current restaurant ID
     * @return restaurant ID or null
     */
    public Long getRestaurantId() {
        return currentUser != null ? currentUser.getRestaurantId() : null;
    }
}