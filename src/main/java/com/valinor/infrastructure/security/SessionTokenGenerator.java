package com.valinor.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Utility class for generating secure session tokens.
 * Uses UUID (Universally Unique Identifier) for token generation.
 */
public final class SessionTokenGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionTokenGenerator.class);
    
    /**
     * Private constructor to prevent instantiation.
     */
    private SessionTokenGenerator() {
        throw new UnsupportedOperationException("SessionTokenGenerator is a utility class and cannot be instantiated");
    }
    
    /**
     * Generates a new unique session token.
     * Uses UUID version 4 (random) for cryptographically strong tokens.
     * 
     * @return a new session token
     */
    public static String generateToken() {
        try {
            String token = UUID.randomUUID().toString();
            logger.debug("Generated new session token");
            return token;
        } catch (Exception e) {
            logger.error("Failed to generate session token", e);
            throw new RuntimeException("Failed to generate session token", e);
        }
    }
    
    /**
     * Validates that a token has the correct format (UUID).
     * 
     * @param token the token to validate
     * @return true if the token is a valid UUID format, false otherwise
     */
    public static boolean isValidToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        try {
            UUID.fromString(token);
            return true;
        } catch (IllegalArgumentException e) {
            logger.debug("Invalid token format: {}", token);
            return false;
        }
    }
    
    /**
     * Generates multiple unique session tokens.
     * 
     * @param count the number of tokens to generate
     * @return an array of unique session tokens
     * @throws IllegalArgumentException if count is less than 1
     */
    public static String[] generateTokens(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must be at least 1");
        }
        
        String[] tokens = new String[count];
        for (int i = 0; i < count; i++) {
            tokens[i] = generateToken();
        }
        
        logger.debug("Generated {} session tokens", count);
        return tokens;
    }
}
