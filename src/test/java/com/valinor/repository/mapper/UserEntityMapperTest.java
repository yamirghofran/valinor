package com.valinor.repository.mapper;

import com.valinor.domain.model.User;
import com.valinor.domain.enums.UserRole;
import com.valinor.exception.EntityValidationException;
import com.valinor.exception.RepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserEntityMapper.
 * Tests entity-to-CSV and CSV-to-entity conversions.
 */
class UserEntityMapperTest {
    
    private UserEntityMapper mapper;
    private User validUser;
    
    @BeforeEach
    void setUp() {
        mapper = new UserEntityMapper();
        LocalDateTime now = LocalDateTime.now();
        validUser = new User(1L, "johndoe", "hashedPassword", "john@example.com", 
                                   "John", "Doe", "555-1234", UserRole.CUSTOMER, 
                                   100L, true, now, now);
    }
    
    @Test
    @DisplayName("Should convert entity to CSV record")
    void testToCsvRecord() throws RepositoryException {
        Map<String, String> record = mapper.toCsvRecord(validUser);
        
        assertNotNull(record);
        assertEquals("1", record.get("user_id"));
        assertEquals("johndoe", record.get("username"));
        assertEquals("hashedPassword", record.get("password_hash"));
        assertEquals("john@example.com", record.get("email"));
        assertEquals("John", record.get("first_name"));
        assertEquals("Doe", record.get("last_name"));
        assertEquals("555-1234", record.get("phone"));
        assertEquals("CUSTOMER", record.get("role"));
        assertEquals("100", record.get("restaurant_id"));
        assertEquals("true", record.get("is_active"));
        assertNotNull(record.get("created_at"));
        assertNotNull(record.get("last_login"));
    }
    
    @Test
    @DisplayName("Should throw exception when converting null entity")
    void testToCsvRecordWithNullEntity() {
        assertThrows(EntityValidationException.class, () -> mapper.toCsvRecord(null));
    }
    
    @Test
    @DisplayName("Should handle null fields in toCsvRecord")
    void testToCsvRecordWithNullFields() throws RepositoryException {
        User user = new User();
        // Set all fields to null to avoid validation errors
        user.setUsername("testuser");
        user.setPasswordHash("testhash");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhone("555-1234");
        user.setRole(UserRole.CUSTOMER);
        
        Map<String, String> record = mapper.toCsvRecord(user);
        
        assertEquals("", record.get("user_id"));
        assertEquals("testuser", record.get("username"));
        assertEquals("testhash", record.get("password_hash"));
        assertEquals("test@example.com", record.get("email"));
        assertEquals("Test", record.get("first_name"));
        assertEquals("User", record.get("last_name"));
        assertEquals("555-1234", record.get("phone"));
        assertEquals("CUSTOMER", record.get("role"));
        assertEquals("", record.get("restaurant_id"));
        assertEquals("true", record.get("is_active"));
        assertNotNull(record.get("created_at"));
        assertNotNull(record.get("last_login"));
    }
    
    @Test
    @DisplayName("Should convert CSV record to entity")
    void testFromCsvRecord() throws RepositoryException {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("user_id", "1");
        record.put("username", "johndoe");
        record.put("password_hash", "hashedPassword");
        record.put("email", "john@example.com");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("phone", "555-1234");
        record.put("role", "CUSTOMER");
        record.put("restaurant_id", "100");
        record.put("is_active", "true");
        record.put("created_at", "2023-01-01T10:00:00");
        record.put("last_login", "2023-01-01T11:00:00");
        
        User user = mapper.fromCsvRecord(record);
        
        assertNotNull(user);
        assertEquals(1L, user.getUserId());
        assertEquals("johndoe", user.getUsername());
        assertEquals("hashedPassword", user.getPasswordHash());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("555-1234", user.getPhone());
        assertEquals(UserRole.CUSTOMER, user.getRole());
        assertEquals(100L, user.getRestaurantId());
        assertTrue(user.getIsActive());
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0, 0), user.getCreatedAt());
        assertEquals(LocalDateTime.of(2023, 1, 1, 11, 0, 0), user.getLastLogin());
    }
    
    @Test
    @DisplayName("Should throw exception when converting null record")
    void testFromCsvRecordWithNull() {
        assertThrows(EntityValidationException.class, () -> mapper.fromCsvRecord(null));
    }
    
    @Test
    @DisplayName("Should handle empty optional fields in fromCsvRecord")
    void testFromCsvRecordWithEmptyOptionalFields() throws RepositoryException {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("username", "johndoe");
        record.put("password_hash", "hashedPassword");
        record.put("email", "john@example.com");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("phone", "555-1234");
        record.put("role", "CUSTOMER");
        record.put("user_id", "");
        record.put("restaurant_id", "");
        record.put("created_at", "");
        record.put("last_login", "");
        
        User user = mapper.fromCsvRecord(record);
        
        assertNull(user.getUserId());
        assertNull(user.getRestaurantId());
        // Note: createdAt is set to current time by User constructor, lastLogin defaults to null
        assertNotNull(user.getCreatedAt());
        assertNull(user.getLastLogin());
    }
    
    @Test
    @DisplayName("Should handle missing is_active field as true")
    void testFromCsvRecordMissingIsActive() throws RepositoryException {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("username", "johndoe");
        record.put("password_hash", "hashedPassword");
        record.put("email", "john@example.com");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("phone", "555-1234");
        record.put("role", "CUSTOMER");
        
        User user = mapper.fromCsvRecord(record);
        
        assertTrue(user.getIsActive());
    }
    
    @Test
    @DisplayName("Should throw exception for invalid user ID format")
    void testFromCsvRecordWithInvalidUserId() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("user_id", "invalid");
        record.put("username", "johndoe");
        record.put("password_hash", "hashedPassword");
        record.put("email", "john@example.com");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("phone", "555-1234");
        record.put("role", "CUSTOMER");
        
        assertThrows(EntityValidationException.class, () -> mapper.fromCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid role")
    void testFromCsvRecordWithInvalidRole() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("username", "johndoe");
        record.put("password_hash", "hashedPassword");
        record.put("email", "john@example.com");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("phone", "555-1234");
        record.put("role", "INVALID_ROLE");
        
        assertThrows(EntityValidationException.class, () -> mapper.fromCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid datetime format")
    void testFromCsvRecordWithInvalidDateTime() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("username", "johndoe");
        record.put("password_hash", "hashedPassword");
        record.put("email", "john@example.com");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("phone", "555-1234");
        record.put("role", "CUSTOMER");
        record.put("created_at", "invalid-date");
        
        assertThrows(EntityValidationException.class, () -> mapper.fromCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should return correct column names")
    void testGetColumnNames() {
        List<String> columnNames = mapper.getColumnNames();
        
        assertNotNull(columnNames);
        assertEquals(12, columnNames.size());
        assertTrue(columnNames.contains("user_id"));
        assertTrue(columnNames.contains("username"));
        assertTrue(columnNames.contains("password_hash"));
        assertTrue(columnNames.contains("email"));
        assertTrue(columnNames.contains("first_name"));
        assertTrue(columnNames.contains("last_name"));
        assertTrue(columnNames.contains("phone"));
        assertTrue(columnNames.contains("role"));
        assertTrue(columnNames.contains("restaurant_id"));
        assertTrue(columnNames.contains("is_active"));
        assertTrue(columnNames.contains("created_at"));
        assertTrue(columnNames.contains("last_login"));
    }
    
    @Test
    @DisplayName("Should return correct primary key field")
    void testGetPrimaryKeyField() {
        assertEquals("user_id", mapper.getPrimaryKeyField());
    }
    
    @Test
    @DisplayName("Should get primary key from entity")
    void testGetPrimaryKey() throws RepositoryException {
        Object primaryKey = mapper.getPrimaryKey(validUser);
        
        assertNotNull(primaryKey);
        assertEquals(1L, primaryKey);
    }
    
    @Test
    @DisplayName("Should throw exception when getting primary key from null entity")
    void testGetPrimaryKeyFromNull() {
        assertThrows(EntityValidationException.class, () -> mapper.getPrimaryKey(null));
    }
    
    @Test
    @DisplayName("Should set primary key on entity with Long")
    void testSetPrimaryKeyWithLong() throws RepositoryException {
        User user = new User();
        mapper.setPrimaryKey(user, 5L);
        
        assertEquals(5L, user.getUserId());
    }
    
    @Test
    @DisplayName("Should set primary key on entity with String")
    void testSetPrimaryKeyWithString() throws RepositoryException {
        User user = new User();
        mapper.setPrimaryKey(user, "10");
        
        assertEquals(10L, user.getUserId());
    }
    
    @Test
    @DisplayName("Should throw exception when setting invalid string ID")
    void testSetPrimaryKeyWithInvalidString() {
        User user = new User();
        assertThrows(EntityValidationException.class, 
                    () -> mapper.setPrimaryKey(user, "invalid"));
    }
    
    @Test
    @DisplayName("Should throw exception when setting primary key on null entity")
    void testSetPrimaryKeyOnNull() {
        assertThrows(EntityValidationException.class, 
                    () -> mapper.setPrimaryKey(null, 1L));
    }
    
    @Test
    @DisplayName("Should validate valid entity")
    void testValidateValidEntity() {
        assertDoesNotThrow(() -> mapper.validateEntity(validUser));
    }
    
    @Test
    @DisplayName("Should throw exception for null entity")
    void testValidateNullEntity() {
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(null));
    }
    
    @Test
    @DisplayName("Should throw exception for missing username")
    void testValidateMissingUsername() {
        User user = new User();
        user.setUsername(null);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(user));
    }
    
    @Test
    @DisplayName("Should throw exception for empty username")
    void testValidateEmptyUsername() {
        User user = new User();
        user.setUsername("  ");
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(user));
    }
    
    @Test
    @DisplayName("Should throw exception for missing password hash")
    void testValidateMissingPasswordHash() {
        User user = new User();
        user.setPasswordHash(null);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(user));
    }
    
    @Test
    @DisplayName("Should throw exception for missing email")
    void testValidateMissingEmail() {
        User user = new User();
        user.setEmail(null);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(user));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid email format")
    void testValidateInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(user));
    }
    
    @Test
    @DisplayName("Should throw exception for missing first name")
    void testValidateMissingFirstName() {
        User user = new User();
        user.setFirstName(null);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(user));
    }
    
    @Test
    @DisplayName("Should throw exception for missing last name")
    void testValidateMissingLastName() {
        User user = new User();
        user.setLastName(null);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(user));
    }
    
    @Test
    @DisplayName("Should throw exception for missing role")
    void testValidateMissingRole() {
        User user = new User();
        user.setRole(null);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(user));
    }
    
    @Test
    @DisplayName("Should validate valid CSV record")
    void testValidateValidCsvRecord() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("username", "johndoe");
        record.put("password_hash", "hashedPassword");
        record.put("email", "john@example.com");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("phone", "555-1234");
        record.put("role", "CUSTOMER");
        
        assertDoesNotThrow(() -> mapper.validateCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should throw exception for null CSV record")
    void testValidateNullCsvRecord() {
        assertThrows(EntityValidationException.class, () -> mapper.validateCsvRecord(null));
    }
    
    @Test
    @DisplayName("Should throw exception for CSV record missing username")
    void testValidateCsvRecordMissingUsername() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("password_hash", "hashedPassword");
        record.put("email", "john@example.com");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("phone", "555-1234");
        record.put("role", "CUSTOMER");
        
        assertThrows(EntityValidationException.class, () -> mapper.validateCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should throw exception for CSV record missing email")
    void testValidateCsvRecordMissingEmail() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("username", "johndoe");
        record.put("password_hash", "hashedPassword");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("phone", "555-1234");
        record.put("role", "CUSTOMER");
        
        assertThrows(EntityValidationException.class, () -> mapper.validateCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should round-trip entity through CSV")
    void testRoundTrip() throws RepositoryException {
        Map<String, String> record = mapper.toCsvRecord(validUser);
        User reconstructed = mapper.fromCsvRecord(record);
        
        assertEquals(validUser.getUserId(), reconstructed.getUserId());
        assertEquals(validUser.getUsername(), reconstructed.getUsername());
        assertEquals(validUser.getPasswordHash(), reconstructed.getPasswordHash());
        assertEquals(validUser.getEmail(), reconstructed.getEmail());
        assertEquals(validUser.getFirstName(), reconstructed.getFirstName());
        assertEquals(validUser.getLastName(), reconstructed.getLastName());
        assertEquals(validUser.getPhone(), reconstructed.getPhone());
        assertEquals(validUser.getRole(), reconstructed.getRole());
        assertEquals(validUser.getRestaurantId(), reconstructed.getRestaurantId());
        assertEquals(validUser.getIsActive(), reconstructed.getIsActive());
    }
}