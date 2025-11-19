package com.valinor.data.config;

/**
 * Configuration constants for the user management module.
 * These values control password requirements, session management, and security settings.
 */
public final class UserConfig {
    
    // Session Configuration
    /** Default session expiry time in hours */
    public static final int SESSION_EXPIRY_HOURS = 24;
    
    /** Maximum number of active sessions per user (0 = unlimited) */
    public static final int MAX_SESSIONS_PER_USER = 5;
    
    // Password Requirements
    /** Minimum password length */
    public static final int PASSWORD_MIN_LENGTH = 8;
    
    /** Maximum password length */
    public static final int PASSWORD_MAX_LENGTH = 128;
    
    /** Require at least one uppercase letter */
    public static final boolean PASSWORD_REQUIRE_UPPERCASE = true;
    
    /** Require at least one lowercase letter */
    public static final boolean PASSWORD_REQUIRE_LOWERCASE = true;
    
    /** Require at least one digit */
    public static final boolean PASSWORD_REQUIRE_DIGIT = true;
    
    /** Require at least one special character */
    public static final boolean PASSWORD_REQUIRE_SPECIAL = false;
    
    // BCrypt Configuration
    /** BCrypt work factor (higher = more secure but slower) */
    public static final int BCRYPT_WORK_FACTOR = 12;
    
    // Account Lockout Configuration
    /** Maximum failed login attempts before account lockout */
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    
    /** Account lockout duration in minutes */
    public static final int LOCKOUT_DURATION_MINUTES = 30;
    
    // Username Requirements
    /** Minimum username length */
    public static final int USERNAME_MIN_LENGTH = 3;
    
    /** Maximum username length */
    public static final int USERNAME_MAX_LENGTH = 50;
    
    /** Username pattern (alphanumeric and underscore only) */
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]+$";
    
    // Email Configuration
    /** Basic email validation pattern */
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    // Session Token Configuration
    /** Session token length (UUID format) */
    public static final int SESSION_TOKEN_LENGTH = 36;
    
    /**
     * Private constructor to prevent instantiation.
     */
    private UserConfig() {
        throw new UnsupportedOperationException("UserConfig is a utility class and cannot be instantiated");
    }
}
