package com.valinor.repository.mapper;

import com.valinor.domain.model.Customer;
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
 * Unit tests for CustomerEntityMapper.
 * Tests entity-to-CSV and CSV-to-entity conversions.
 */
class CustomerEntityMapperTest {
    
    private CustomerEntityMapper mapper;
    private Customer validCustomer;
    
    @BeforeEach
    void setUp() {
        mapper = new CustomerEntityMapper();
        validCustomer = new Customer(1L, "John", "Doe", "john.doe@example.com", 
                                     "555-1234", "Peanuts", "VIP customer", 100L);
    }
    
    @Test
    @DisplayName("Should convert entity to CSV record")
    void testToCsvRecord() throws RepositoryException {
        Map<String, String> record = mapper.toCsvRecord(validCustomer);
        
        assertNotNull(record);
        assertEquals("1", record.get("customer_id"));
        assertEquals("John", record.get("first_name"));
        assertEquals("Doe", record.get("last_name"));
        assertEquals("john.doe@example.com", record.get("email"));
        assertEquals("555-1234", record.get("phone"));
        assertEquals("Peanuts", record.get("allergies"));
        assertEquals("VIP customer", record.get("notes"));
        assertEquals("100", record.get("restaurant_id"));
    }
    
    @Test
    @DisplayName("Should throw exception when converting null entity")
    void testToCsvRecordWithNullEntity() {
        assertThrows(EntityValidationException.class, () -> mapper.toCsvRecord(null));
    }
    
    @Test
    @DisplayName("Should handle null customer ID in toCsvRecord")
    void testToCsvRecordWithNullId() throws RepositoryException {
        Customer customer = new Customer("Jane", "Smith", "jane@example.com", "555-5678");
        Map<String, String> record = mapper.toCsvRecord(customer);
        
        assertEquals("", record.get("customer_id"));
    }
    
    @Test
    @DisplayName("Should handle null optional fields in toCsvRecord")
    void testToCsvRecordWithNullOptionalFields() throws RepositoryException {
        Customer customer = new Customer(1L, "John", "Doe", "john@example.com", 
                                        "555-1234", null, null, null);
        Map<String, String> record = mapper.toCsvRecord(customer);
        
        assertEquals("", record.get("allergies"));
        assertEquals("", record.get("notes"));
        assertEquals("", record.get("restaurant_id"));
    }
    
    @Test
    @DisplayName("Should convert CSV record to entity")
    void testFromCsvRecord() throws RepositoryException {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("customer_id", "1");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("email", "john.doe@example.com");
        record.put("phone", "555-1234");
        record.put("allergies", "Peanuts");
        record.put("notes", "VIP customer");
        record.put("restaurant_id", "100");
        
        Customer customer = mapper.fromCsvRecord(record);
        
        assertNotNull(customer);
        assertEquals(1L, customer.getCustomerId());
        assertEquals("John", customer.getFirstName());
        assertEquals("Doe", customer.getLastName());
        assertEquals("john.doe@example.com", customer.getEmail());
        assertEquals("555-1234", customer.getPhone());
        assertEquals("Peanuts", customer.getAllergies());
        assertEquals("VIP customer", customer.getNotes());
        assertEquals(100L, customer.getRestaurantId());
    }
    
    @Test
    @DisplayName("Should throw exception when converting null record")
    void testFromCsvRecordWithNull() {
        assertThrows(EntityValidationException.class, () -> mapper.fromCsvRecord(null));
    }
    
    @Test
    @DisplayName("Should handle empty customer ID in fromCsvRecord")
    void testFromCsvRecordWithEmptyId() throws RepositoryException {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("customer_id", "");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("email", "john@example.com");
        record.put("phone", "555-1234");
        record.put("allergies", "");
        record.put("notes", "");
        record.put("restaurant_id", "");
        
        Customer customer = mapper.fromCsvRecord(record);
        
        assertNull(customer.getCustomerId());
        assertNull(customer.getAllergies());
        assertNull(customer.getNotes());
        assertNull(customer.getRestaurantId());
    }
    
    @Test
    @DisplayName("Should throw exception for invalid customer ID format")
    void testFromCsvRecordWithInvalidId() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("customer_id", "invalid");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("email", "john@example.com");
        record.put("phone", "555-1234");
        
        assertThrows(EntityValidationException.class, () -> mapper.fromCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should return correct column names")
    void testGetColumnNames() {
        List<String> columnNames = mapper.getColumnNames();
        
        assertNotNull(columnNames);
        assertEquals(8, columnNames.size());
        assertTrue(columnNames.contains("customer_id"));
        assertTrue(columnNames.contains("first_name"));
        assertTrue(columnNames.contains("last_name"));
        assertTrue(columnNames.contains("email"));
        assertTrue(columnNames.contains("phone"));
        assertTrue(columnNames.contains("allergies"));
        assertTrue(columnNames.contains("notes"));
        assertTrue(columnNames.contains("restaurant_id"));
    }
    
    @Test
    @DisplayName("Should return correct primary key field")
    void testGetPrimaryKeyField() {
        assertEquals("customer_id", mapper.getPrimaryKeyField());
    }
    
    @Test
    @DisplayName("Should get primary key from entity")
    void testGetPrimaryKey() throws RepositoryException {
        Object primaryKey = mapper.getPrimaryKey(validCustomer);
        
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
        Customer customer = new Customer();
        mapper.setPrimaryKey(customer, 5L);
        
        assertEquals(5L, customer.getCustomerId());
    }
    
    @Test
    @DisplayName("Should set primary key on entity with String")
    void testSetPrimaryKeyWithString() throws RepositoryException {
        Customer customer = new Customer();
        mapper.setPrimaryKey(customer, "10");
        
        assertEquals(10L, customer.getCustomerId());
    }
    
    @Test
    @DisplayName("Should throw exception when setting invalid string ID")
    void testSetPrimaryKeyWithInvalidString() {
        Customer customer = new Customer();
        assertThrows(EntityValidationException.class, 
                    () -> mapper.setPrimaryKey(customer, "invalid"));
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
        assertDoesNotThrow(() -> mapper.validateEntity(validCustomer));
    }
    
    @Test
    @DisplayName("Should throw exception for null entity")
    void testValidateNullEntity() {
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(null));
    }
    
    @Test
    @DisplayName("Should throw exception for missing first name")
    void testValidateMissingFirstName() {
        Customer customer = new Customer(1L, null, "Doe", "john@example.com", 
                                        "555-1234", null, null, 100L);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(customer));
    }
    
    @Test
    @DisplayName("Should throw exception for empty first name")
    void testValidateEmptyFirstName() {
        Customer customer = new Customer(1L, "  ", "Doe", "john@example.com", 
                                        "555-1234", null, null, 100L);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(customer));
    }
    
    @Test
    @DisplayName("Should throw exception for missing last name")
    void testValidateMissingLastName() {
        Customer customer = new Customer(1L, "John", null, "john@example.com", 
                                        "555-1234", null, null, 100L);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(customer));
    }
    
    @Test
    @DisplayName("Should throw exception for missing email")
    void testValidateMissingEmail() {
        Customer customer = new Customer(1L, "John", "Doe", null, 
                                        "555-1234", null, null, 100L);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(customer));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid email format")
    void testValidateInvalidEmail() {
        Customer customer = new Customer(1L, "John", "Doe", "invalid-email", 
                                        "555-1234", null, null, 100L);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(customer));
    }
    
    @Test
    @DisplayName("Should throw exception for missing phone")
    void testValidateMissingPhone() {
        Customer customer = new Customer(1L, "John", "Doe", "john@example.com", 
                                        null, null, null, 100L);
        assertThrows(EntityValidationException.class, () -> mapper.validateEntity(customer));
    }
    
    @Test
    @DisplayName("Should validate valid CSV record")
    void testValidateValidCsvRecord() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("customer_id", "1");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("email", "john@example.com");
        record.put("phone", "555-1234");
        
        assertDoesNotThrow(() -> mapper.validateCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should throw exception for null CSV record")
    void testValidateNullCsvRecord() {
        assertThrows(EntityValidationException.class, () -> mapper.validateCsvRecord(null));
    }
    
    @Test
    @DisplayName("Should throw exception for CSV record missing first name")
    void testValidateCsvRecordMissingFirstName() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("last_name", "Doe");
        record.put("email", "john@example.com");
        record.put("phone", "555-1234");
        
        assertThrows(EntityValidationException.class, () -> mapper.validateCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should throw exception for CSV record with invalid ID format")
    void testValidateCsvRecordInvalidId() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("customer_id", "not-a-number");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("email", "john@example.com");
        record.put("phone", "555-1234");
        
        assertThrows(EntityValidationException.class, () -> mapper.validateCsvRecord(record));
    }
    
    @Test
    @DisplayName("Should handle whitespace in record values")
    void testFromCsvRecordWithWhitespace() throws RepositoryException {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("customer_id", " 1 ");
        record.put("first_name", "John");
        record.put("last_name", "Doe");
        record.put("email", "john@example.com");
        record.put("phone", "555-1234");
        record.put("restaurant_id", " 100 ");
        
        Customer customer = mapper.fromCsvRecord(record);
        
        assertEquals(1L, customer.getCustomerId());
        assertEquals(100L, customer.getRestaurantId());
    }
    
    @Test
    @DisplayName("Should round-trip entity through CSV")
    void testRoundTrip() throws RepositoryException {
        Map<String, String> record = mapper.toCsvRecord(validCustomer);
        Customer reconstructed = mapper.fromCsvRecord(record);
        
        assertEquals(validCustomer.getCustomerId(), reconstructed.getCustomerId());
        assertEquals(validCustomer.getFirstName(), reconstructed.getFirstName());
        assertEquals(validCustomer.getLastName(), reconstructed.getLastName());
        assertEquals(validCustomer.getEmail(), reconstructed.getEmail());
        assertEquals(validCustomer.getPhone(), reconstructed.getPhone());
        assertEquals(validCustomer.getAllergies(), reconstructed.getAllergies());
        assertEquals(validCustomer.getNotes(), reconstructed.getNotes());
        assertEquals(validCustomer.getRestaurantId(), reconstructed.getRestaurantId());
    }
}
