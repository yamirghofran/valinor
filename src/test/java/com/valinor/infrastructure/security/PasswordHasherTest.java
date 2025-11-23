package com.valinor.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordHasher.
 * Tests password hashing, verification, and validation.
 */
class PasswordHasherTest {
    
    @Test
    @DisplayName("Should not allow instantiation")
    void testCannotInstantiate() {
        assertThrows(UnsupportedOperationException.class, () -> {
            // Use reflection to try to instantiate
            try {
                var constructor = PasswordHasher.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw (UnsupportedOperationException) e.getCause();
            }
        });
    }
    
    @Test
    @DisplayName("Should hash password successfully")
    void testHashPassword() {
        String plainPassword = "MySecurePassword123!";
        String hashedPassword = PasswordHasher.hash(plainPassword);
        
        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$")); // BCrypt format
    }
    
    @Test
    @DisplayName("Should throw exception when hashing null password")
    void testHashNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash(null));
    }
    
    @Test
    @DisplayName("Should throw exception when hashing empty password")
    void testHashEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.hash(""));
    }
    
    @Test
    @DisplayName("Should generate different hashes for same password")
    void testDifferentHashesForSamePassword() {
        String plainPassword = "MySecurePassword123!";
        String hash1 = PasswordHasher.hash(plainPassword);
        String hash2 = PasswordHasher.hash(plainPassword);
        
        assertNotEquals(hash1, hash2); // BCrypt uses random salts
    }
    
    @Test
    @DisplayName("Should verify correct password")
    void testVerifyCorrectPassword() {
        String plainPassword = "MySecurePassword123!";
        String hashedPassword = PasswordHasher.hash(plainPassword);
        
        assertTrue(PasswordHasher.verify(plainPassword, hashedPassword));
    }
    
    @Test
    @DisplayName("Should reject incorrect password")
    void testVerifyIncorrectPassword() {
        String plainPassword = "MySecurePassword123!";
        String wrongPassword = "WrongPassword456!";
        String hashedPassword = PasswordHasher.hash(plainPassword);
        
        assertFalse(PasswordHasher.verify(wrongPassword, hashedPassword));
    }
    
    @Test
    @DisplayName("Should throw exception when verifying with null plain password")
    void testVerifyNullPlainPassword() {
        String hashedPassword = PasswordHasher.hash("password123!");
        assertThrows(IllegalArgumentException.class, 
                    () -> PasswordHasher.verify(null, hashedPassword));
    }
    
    @Test
    @DisplayName("Should throw exception when verifying with empty plain password")
    void testVerifyEmptyPlainPassword() {
        String hashedPassword = PasswordHasher.hash("password123!");
        assertThrows(IllegalArgumentException.class, 
                    () -> PasswordHasher.verify("", hashedPassword));
    }
    
    @Test
    @DisplayName("Should throw exception when verifying with null hashed password")
    void testVerifyNullHashedPassword() {
        assertThrows(IllegalArgumentException.class, 
                    () -> PasswordHasher.verify("password123!", null));
    }
    
    @Test
    @DisplayName("Should throw exception when verifying with empty hashed password")
    void testVerifyEmptyHashedPassword() {
        assertThrows(IllegalArgumentException.class, 
                    () -> PasswordHasher.verify("password123!", ""));
    }
    
    @Test
    @DisplayName("Should return false for invalid hash format")
    void testVerifyInvalidHashFormat() {
        assertFalse(PasswordHasher.verify("password123!", "invalid-hash"));
    }
    
    @Test
    @DisplayName("Should validate password with all requirements")
    void testValidPasswordWithAllRequirements() {
        // Assuming default requirements: min 8 chars, uppercase, lowercase, digit, special
        assertTrue(PasswordHasher.isValidPassword("SecurePass123!"));
    }
    
    @Test
    @DisplayName("Should reject null password")
    void testValidateNullPassword() {
        assertFalse(PasswordHasher.isValidPassword(null));
    }
    
    @Test
    @DisplayName("Should reject password that is too short")
    void testValidateTooShortPassword() {
        assertFalse(PasswordHasher.isValidPassword("Pass1!"));
    }
    
    @Test
    @DisplayName("Should reject password without uppercase")
    void testValidatePasswordWithoutUppercase() {
        // Assuming uppercase is required
        assertFalse(PasswordHasher.isValidPassword("password123!"));
    }
    
    @Test
    @DisplayName("Should reject password without lowercase")
    void testValidatePasswordWithoutLowercase() {
        // Assuming lowercase is required
        assertFalse(PasswordHasher.isValidPassword("PASSWORD123!"));
    }
    
    @Test
    @DisplayName("Should reject password without digit")
    void testValidatePasswordWithoutDigit() {
        // Assuming digit is required
        assertFalse(PasswordHasher.isValidPassword("SecurePassword!"));
    }
    
    @Test
    @DisplayName("Should accept password without special character")
    void testValidatePasswordWithoutSpecial() {
        // Special character is NOT required according to UserConfig
        assertTrue(PasswordHasher.isValidPassword("SecurePassword123"));
    }
    
    @Test
    @DisplayName("Should get password requirements string")
    void testGetPasswordRequirements() {
        String requirements = PasswordHasher.getPasswordRequirements();
        
        assertNotNull(requirements);
        assertFalse(requirements.isEmpty());
        assertTrue(requirements.contains("characters"));
    }
    
    @Test
    @DisplayName("Should handle password at minimum length")
    void testPasswordAtMinimumLength() {
        // Assuming min length is 8
        assertTrue(PasswordHasher.isValidPassword("Pass123!"));
    }
    
    @Test
    @DisplayName("Should handle long passwords")
    void testLongPassword() {
        String longPassword = "ThisIsAVeryLongSecurePassword123!WithManyCharacters";
        String hashedPassword = PasswordHasher.hash(longPassword);
        
        assertTrue(PasswordHasher.verify(longPassword, hashedPassword));
    }
    
    @Test
    @DisplayName("Should handle passwords with various special characters")
    void testPasswordsWithVariousSpecialCharacters() {
        String[] passwords = {
            "Pass123@test",
            "Pass123#test",
            "Pass123$test",
            "Pass123%test",
            "Pass123^test",
            "Pass123&test",
            "Pass123*test"
        };
        
        for (String password : passwords) {
            assertTrue(PasswordHasher.isValidPassword(password), 
                      "Password should be valid: " + password);
        }
    }
    
    @Test
    @DisplayName("Should be case sensitive when verifying")
    void testCaseSensitiveVerification() {
        String plainPassword = "MySecurePassword123!";
        String hashedPassword = PasswordHasher.hash(plainPassword);
        
        assertFalse(PasswordHasher.verify("mysecurepassword123!", hashedPassword));
        assertFalse(PasswordHasher.verify("MYSECUREPASSWORD123!", hashedPassword));
    }
}
