package com.valinor.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SessionTokenGenerator.
 * Tests token generation and validation.
 */
class SessionTokenGeneratorTest {
    
    @Test
    @DisplayName("Should not allow instantiation")
    void testCannotInstantiate() {
        assertThrows(UnsupportedOperationException.class, () -> {
            // Use reflection to try to instantiate
            try {
                var constructor = SessionTokenGenerator.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw (UnsupportedOperationException) e.getCause();
            }
        });
    }
    
    @Test
    @DisplayName("Should generate valid token")
    void testGenerateToken() {
        String token = SessionTokenGenerator.generateToken();
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(SessionTokenGenerator.isValidToken(token));
    }
    
    @Test
    @DisplayName("Should generate unique tokens")
    void testGenerateUniqueTokens() {
        String token1 = SessionTokenGenerator.generateToken();
        String token2 = SessionTokenGenerator.generateToken();
        String token3 = SessionTokenGenerator.generateToken();
        
        assertNotEquals(token1, token2);
        assertNotEquals(token2, token3);
        assertNotEquals(token1, token3);
    }
    
    @Test
    @DisplayName("Should generate tokens in UUID format")
    void testTokenFormat() {
        String token = SessionTokenGenerator.generateToken();
        
        // UUID format: 8-4-4-4-12 hex digits with hyphens
        assertTrue(token.matches("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$"));
    }
    
    @Test
    @DisplayName("Should validate correct token")
    void testValidateCorrectToken() {
        String token = SessionTokenGenerator.generateToken();
        
        assertTrue(SessionTokenGenerator.isValidToken(token));
    }
    
    @Test
    @DisplayName("Should reject null token")
    void testValidateNullToken() {
        assertFalse(SessionTokenGenerator.isValidToken(null));
    }
    
    @Test
    @DisplayName("Should reject empty token")
    void testValidateEmptyToken() {
        assertFalse(SessionTokenGenerator.isValidToken(""));
        assertFalse(SessionTokenGenerator.isValidToken("   "));
    }
    
    @Test
    @DisplayName("Should reject invalid token format")
    void testValidateInvalidToken() {
        assertFalse(SessionTokenGenerator.isValidToken("invalid-token"));
        assertFalse(SessionTokenGenerator.isValidToken("123-456-789"));
        assertFalse(SessionTokenGenerator.isValidToken("not-a-uuid"));
        assertFalse(SessionTokenGenerator.isValidToken("550e8400-e29b-41d4-a716-44665544zzzz")); // Invalid UUID
    }
    
    @Test
    @DisplayName("Should generate multiple tokens")
    void testGenerateMultipleTokens() {
        String[] tokens = SessionTokenGenerator.generateTokens(5);
        
        assertNotNull(tokens);
        assertEquals(5, tokens.length);
        
        // All tokens should be valid
        for (String token : tokens) {
            assertNotNull(token);
            assertTrue(SessionTokenGenerator.isValidToken(token));
        }
    }
    
    @Test
    @DisplayName("Should generate unique tokens in batch")
    void testGenerateUniqueTokensInBatch() {
        String[] tokens = SessionTokenGenerator.generateTokens(10);
        
        // Convert to set to check uniqueness
        Set<String> uniqueTokens = new HashSet<>(Arrays.asList(tokens));
        
        assertEquals(10, uniqueTokens.size());
    }
    
    @Test
    @DisplayName("Should throw exception for invalid count")
    void testGenerateTokensInvalidCount() {
        assertThrows(IllegalArgumentException.class, () -> SessionTokenGenerator.generateTokens(0));
        assertThrows(IllegalArgumentException.class, () -> SessionTokenGenerator.generateTokens(-1));
        assertThrows(IllegalArgumentException.class, () -> SessionTokenGenerator.generateTokens(-10));
    }
    
    @Test
    @DisplayName("Should generate single token when count is 1")
    void testGenerateSingleToken() {
        String[] tokens = SessionTokenGenerator.generateTokens(1);
        
        assertNotNull(tokens);
        assertEquals(1, tokens.length);
        assertTrue(SessionTokenGenerator.isValidToken(tokens[0]));
    }
    
    @Test
    @DisplayName("Should handle large token generation")
    void testGenerateLargeNumberOfTokens() {
        String[] tokens = SessionTokenGenerator.generateTokens(1000);
        
        assertEquals(1000, tokens.length);
        
        // Check uniqueness for a sample
        Set<String> uniqueTokens = new HashSet<>();
        for (int i = 0; i < 100; i++) { // Check first 100 for uniqueness
            uniqueTokens.add(tokens[i]);
        }
        assertEquals(100, uniqueTokens.size());
    }
    
    @Test
    @DisplayName("Should validate standard UUID examples")
    void testValidateStandardUUIDs() {
        String[] validUUIDs = {
            "550e8400-e29b-41d4-a716-446655440000",
            "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
            "6ba7b811-9dad-11d1-80b4-00c04fd430c8",
            "00000000-0000-0000-0000-000000000000"
        };
        
        for (String uuid : validUUIDs) {
            assertTrue(SessionTokenGenerator.isValidToken(uuid), "Should be valid: " + uuid);
        }
    }
    
    @Test
    @DisplayName("Should reject malformed UUIDs")
    void testValidateMalformedUUIDs() {
        String[] invalidUUIDs = {
            "550e8400-e29b-41d4-a716", // Too short
            "550e8400-e29b-41d4-a716-446655440000-extra", // Too long
            "550e8400-e29b-41d4-a716-44665544000g", // Invalid hex character
            "550e8400e29b41d4a716446655440000", // Missing hyphens
            "550e8400-e29b-41d4-a716-44665544-0000", // Wrong segment lengths
            "" // Empty
        };
        
        for (String uuid : invalidUUIDs) {
            assertFalse(SessionTokenGenerator.isValidToken(uuid), "Should be invalid: " + uuid);
        }
    }
    
    @Test
    @DisplayName("Should accept both case hex digits")
    void testValidateCaseInsensitiveHex() {
        // UUIDs should accept both lowercase and uppercase hex
        String token = SessionTokenGenerator.generateToken();
        
        // Generated tokens should be lowercase
        assertEquals(token.toLowerCase(), token);
        
        // Uppercase hex should be valid (UUID.fromString accepts both)
        assertTrue(SessionTokenGenerator.isValidToken("550E8400-E29B-41D4-A716-446655440000"));
    }
}
