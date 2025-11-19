package com.valinor.data.util;

import com.valinor.data.config.UserConfig;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for hashing and verifying passwords using BCrypt.
 * BCrypt is a password hashing function designed to be computationally expensive
 * to resist brute-force attacks.
 */
public final class PasswordHasher {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordHasher.class);
    
    /**
     * Private constructor to prevent instantiation.
     */
    private PasswordHasher() {
        throw new UnsupportedOperationException("PasswordHasher is a utility class and cannot be instantiated");
    }
    
    /**
     * Hashes a plain text password using BCrypt.
     * 
     * @param plainPassword the plain text password to hash
     * @return the hashed password
     * @throws IllegalArgumentException if the password is null or empty
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            String salt = BCrypt.gensalt(UserConfig.BCRYPT_WORK_FACTOR);
            String hashedPassword = BCrypt.hashpw(plainPassword, salt);
            logger.debug("Password hashed successfully");
            return hashedPassword;
        } catch (Exception e) {
            logger.error("Failed to hash password", e);
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Verifies a plain text password against a hashed password.
     * 
     * @param plainPassword the plain text password to verify
     * @param hashedPassword the hashed password to compare against
     * @return true if the password matches, false otherwise
     * @throws IllegalArgumentException if either parameter is null or empty
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Plain password cannot be null or empty");
        }
        
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
        
        try {
            boolean matches = BCrypt.checkpw(plainPassword, hashedPassword);
            logger.debug("Password verification result: {}", matches);
            return matches;
        } catch (Exception e) {
            logger.error("Failed to verify password", e);
            return false;
        }
    }
    
    /**
     * Validates that a password meets the configured requirements.
     * 
     * @param password the password to validate
     * @return true if the password meets all requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        
        // Check length
        if (password.length() < UserConfig.PASSWORD_MIN_LENGTH || 
            password.length() > UserConfig.PASSWORD_MAX_LENGTH) {
            return false;
        }
        
        // Check uppercase requirement
        if (UserConfig.PASSWORD_REQUIRE_UPPERCASE && !password.matches(".*[A-Z].*")) {
            return false;
        }
        
        // Check lowercase requirement
        if (UserConfig.PASSWORD_REQUIRE_LOWERCASE && !password.matches(".*[a-z].*")) {
            return false;
        }
        
        // Check digit requirement
        if (UserConfig.PASSWORD_REQUIRE_DIGIT && !password.matches(".*\\d.*")) {
            return false;
        }
        
        // Check special character requirement
        if (UserConfig.PASSWORD_REQUIRE_SPECIAL && !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets a description of the password requirements.
     * 
     * @return a string describing the password requirements
     */
    public static String getPasswordRequirements() {
        StringBuilder requirements = new StringBuilder();
        requirements.append("Password must be between ")
                   .append(UserConfig.PASSWORD_MIN_LENGTH)
                   .append(" and ")
                   .append(UserConfig.PASSWORD_MAX_LENGTH)
                   .append(" characters");
        
        if (UserConfig.PASSWORD_REQUIRE_UPPERCASE) {
            requirements.append(", contain at least one uppercase letter");
        }
        
        if (UserConfig.PASSWORD_REQUIRE_LOWERCASE) {
            requirements.append(", contain at least one lowercase letter");
        }
        
        if (UserConfig.PASSWORD_REQUIRE_DIGIT) {
            requirements.append(", contain at least one digit");
        }
        
        if (UserConfig.PASSWORD_REQUIRE_SPECIAL) {
            requirements.append(", contain at least one special character");
        }
        
        requirements.append(".");
        return requirements.toString();
    }
}
