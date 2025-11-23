package com.valinor.repository.mapper;

import com.valinor.domain.model.Restaurant;
import com.valinor.exception.EntityValidationException;
import com.valinor.exception.RepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RestaurantEntityMapper.
 * Tests entity-to-CSV and CSV-to-entity conversions.
 */
class RestaurantEntityMapperTest {
    
    private RestaurantEntityMapper mapper;
    private Restaurant validRestaurant;
    
    @BeforeEach
    void setUp() {
        mapper = new RestaurantEntityMapper();
        validRestaurant = new Restaurant(1L, "Test Restaurant", "New York", 
                                        "test@restaurant.com", "555-1234");
    }
    
    @Test
    @DisplayName("Should convert entity to CSV record")
    void testToCsvRecord() throws RepositoryException {
        Map<String, String> record = mapper.toCsvRecord(validRestaurant);
        
        assertNotNull(record);
        assertEquals("1", record.get("restaurant_id"));
        assertEquals("Test Restaurant", record.get("name"));
        assertEquals("New York", record.get("location"));
        assertEquals("test@restaurant.com", record.get("contact_email"));
        assertEquals("555-1234", record.get("contact_phone"));
    }
    
    @Test
    @DisplayName("Should throw exception when converting null entity")
    void testToCsvRecordWithNullEntity() {
        assertThrows(EntityValidationException.class, () -> mapper.toCsvRecord(null));
    }
    
    @Test
    @DisplayName("Should handle null restaurant ID in toCsvRecord")
    void testToCsvRecordWithNullId() throws RepositoryException {
        Restaurant restaurant = new Restaurant(null, "Test", "Location", 
                                              "test@example.com", "555-1234");
        Map<String, String> record = mapper.toCsvRecord(restaurant);
        
        assertEquals("", record.get("restaurant_id"));
    }
    
    @Test
    @DisplayName("Should convert CSV record to entity")
    void testFromCsvRecord() throws RepositoryException {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("restaurant_id", "1");
        record.put("name", "Test Restaurant");
        record.put("location", "New York");
        record.put("contact_email", "test@restaurant.com");
        record.put("contact_phone", "555-1234");
        
        Restaurant restaurant = mapper.fromCsvRecord(record);
        
        assertNotNull(restaurant);
        assertEquals(1L, restaurant.getRestaurantId());
        assertEquals("Test Restaurant", restaurant.getName());
        assertEquals("New York", restaurant.getLocation());
        assertEquals("test@restaurant.com", restaurant.getContactEmail());
        assertEquals("555-1234", restaurant.getContactPhone());
    }
    
    @Test
    @DisplayName("Should throw exception when converting null record")
    void testFromCsvRecordWithNull() {
        assertThrows(EntityValidationException.class, () -> mapper.fromCsvRecord(null));
    }
    
    @Test
    @DisplayName("Should handle empty restaurant ID in fromCsvRecord")
    void testFromCsvRecordWithEmptyId() throws RepositoryException {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("restaurant_id", "");
        record.put("name", "Test");
        record.put("location", "Location");
        record.put("contact_email", "test@example.com");
        record.put("contact_phone", "555-1234");
        
        Restaurant restaurant = mapper.fromCsvRecord(record);
        
        assertNull(restaurant.getRestaurantId());
    }
    
    @Test
    @DisplayName("Should throw exception for invalid restaurant ID format")
    void testFromCsvRecordWithInvalidId() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("restaurant_id", "invalid");
        record.put("name", "Test");
        record.put("location", "Location");
        record.put("contact_email", "test@example.com");
        record.put("contact_phone", "555-1234");
        
        assertThrows(EntityValidationException.class, () -> mapper.fromCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should return correct column names")
    void testGetColumnNames() {
        List<String> columnNames = mapper.getColumnNames();
        
        assertNotNull(columnNames);
        assertEquals(5, columnNames.size());
        assertTrue(columnNames.contains("restaurant_id"));
        assertTrue(columnNames.contains("name"));
        assertTrue(columnNames.contains("location"));
        assertTrue(columnNames.contains("contact_email"));
        assertTrue(columnNames.contains("contact_phone"));
    }
    
    @Test
    @DisplayName("Should return correct primary key field")
    void testGetPrimaryKeyField() {
        assertEquals("restaurant_id", mapper.getPrimaryKeyField());
    }
    
    @Test
    @DisplayName("Should get primary key from entity")
    void testGetPrimaryKey() throws RepositoryException {
        Object primaryKey = mapper.getPrimaryKey(validRestaurant);
        
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
        Restaurant restaurant = new Restaurant();
        mapper.setPrimaryKey(restaurant, 5L);
        
        assertEquals(5L, restaurant.getRestaurantId());
    }
    
    @Test
    @DisplayName("Should set primary key on entity with String")
    void testSetPrimaryKeyWithString() throws RepositoryException {
        Restaurant restaurant = new Restaurant();
        mapper.setPrimaryKey(restaurant, "10");
        
        assertEquals(10L, restaurant.getRestaurantId());
    }
    
    @Test
    @DisplayName("Should throw exception when setting invalid string ID")
    void testSetPrimaryKeyWithInvalidString() {
        Restaurant restaurant = new Restaurant();
        assertThrows(EntityValidationException.class, 
                    () -> mapper.setPrimaryKey(restaurant, "invalid"));
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
        assertDoesNotThrow(() -> mapper.validateEntity(validRestaurant));
    }
    
    @Test
    @DisplayName("Should throw exception for null entity")
    void testValidateNullEntity() {
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(null));
    }
    
    @Test
    @DisplayName("Should throw exception for missing name")
    void testValidateMissingName() {
        Restaurant restaurant = new Restaurant(1L, null, "Location", 
                                              "test@example.com", "555-1234");
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(restaurant));
    }
    
    @Test
    @DisplayName("Should throw exception for empty name")
    void testValidateEmptyName() {
        Restaurant restaurant = new Restaurant(1L, "  ", "Location", 
                                              "test@example.com", "555-1234");
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(restaurant));
    }
    
    @Test
    @DisplayName("Should throw exception for missing location")
    void testValidateMissingLocation() {
        Restaurant restaurant = new Restaurant(1L, "Test", null, 
                                              "test@example.com", "555-1234");
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(restaurant));
    }
    
    @Test
    @DisplayName("Should throw exception for missing email")
    void testValidateMissingEmail() {
        Restaurant restaurant = new Restaurant(1L, "Test", "Location", 
                                              null, "555-1234");
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(restaurant));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid email format")
    void testValidateInvalidEmail() {
        Restaurant restaurant = new Restaurant(1L, "Test", "Location", 
                                              "invalid-email", "555-1234");
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(restaurant));
    }
    
    @Test
    @DisplayName("Should throw exception for missing phone")
    void testValidateMissingPhone() {
        Restaurant restaurant = new Restaurant(1L, "Test", "Location", 
                                              "test@example.com", null);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(restaurant));
    }
    
    @Test
    @DisplayName("Should validate valid CSV record")
    void testValidateValidCsvRecord() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("restaurant_id", "1");
        record.put("name", "Test");
        record.put("location", "Location");
        record.put("contact_email", "test@example.com");
        record.put("contact_phone", "555-1234");
        
        assertDoesNotThrow(() -> mapper.validateCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should throw exception for null CSV record")
    void testValidateNullCsvRecord() {
        assertThrows(EntityValidationException.class, () -> mapper.validateCsvRecord(null));
    }
    
    @Test
    @DisplayName("Should throw exception for CSV record missing name")
    void testValidateCsvRecordMissingName() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("location", "Location");
        record.put("contact_email", "test@example.com");
        record.put("contact_phone", "555-1234");
        
        assertThrows(EntityValidationException.class, () -> mapper.validateCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should throw exception for CSV record with invalid ID format")
    void testValidateCsvRecordInvalidId() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("restaurant_id", "not-a-number");
        record.put("name", "Test");
        record.put("location", "Location");
        record.put("contact_email", "test@example.com");
        record.put("contact_phone", "555-1234");
        
        assertThrows(EntityValidationException.class, () -> mapper.validateCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should handle whitespace in record values")
    void testFromCsvRecordWithWhitespace() throws RepositoryException {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("restaurant_id", " 1 ");
        record.put("name", " Test Restaurant ");
        record.put("location", " New York ");
        record.put("contact_email", " test@example.com ");
        record.put("contact_phone", " 555-1234 ");
        
        Restaurant restaurant = mapper.fromCsvRecord(record);
        
        assertEquals(1L, restaurant.getRestaurantId());
    }
    
    @Test
    @DisplayName("Should round-trip entity through CSV")
    void testRoundTrip() throws RepositoryException {
        Map<String, String> record = mapper.toCsvRecord(validRestaurant);
        Restaurant reconstructed = mapper.fromCsvRecord(record);
        
        assertEquals(validRestaurant.getRestaurantId(), reconstructed.getRestaurantId());
        assertEquals(validRestaurant.getName(), reconstructed.getName());
        assertEquals(validRestaurant.getLocation(), reconstructed.getLocation());
        assertEquals(validRestaurant.getContactEmail(), reconstructed.getContactEmail());
        assertEquals(validRestaurant.getContactPhone(), reconstructed.getContactPhone());
    }
}
