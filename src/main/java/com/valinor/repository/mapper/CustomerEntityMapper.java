package com.valinor.repository.mapper;

import com.valinor.domain.model.Customer;
import com.valinor.exception.EntityValidationException;
import com.valinor.exception.RepositoryException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Entity mapper for Customer entities.
 * Handles conversion between Customer objects and CSV records.
 */
public class CustomerEntityMapper implements EntityMapper<Customer> {
    
    private static final String[] COLUMN_NAMES = {
        "customer_id", "first_name", "last_name", "email", "phone", "allergies", "notes"
    };
    
    private static final String PRIMARY_KEY_FIELD = "customer_id";
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    @Override
    public Map<String, String> toCsvRecord(Customer entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Customer entity cannot be null");
        }
        
        validateEntity(entity);
        
        Map<String, String> record = new LinkedHashMap<>();
        record.put("customer_id", entity.getCustomerId() != null ? entity.getCustomerId().toString() : "");
        record.put("first_name", entity.getFirstName() != null ? entity.getFirstName() : "");
        record.put("last_name", entity.getLastName() != null ? entity.getLastName() : "");
        record.put("email", entity.getEmail() != null ? entity.getEmail() : "");
        record.put("phone", entity.getPhone() != null ? entity.getPhone() : "");
        record.put("allergies", entity.getAllergies() != null ? entity.getAllergies() : "");
        record.put("notes", entity.getNotes() != null ? entity.getNotes() : "");
        
        return record;
    }
    
    @Override
    public Customer fromCsvRecord(Map<String, String> record) throws RepositoryException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        validateCsvRecord(record);
        
        Customer customer = new Customer();
        
        try {
            // Parse customer_id
            String customerIdStr = record.get("customer_id");
            if (customerIdStr != null && !customerIdStr.trim().isEmpty()) {
                customer.setCustomerId(Long.parseLong(customerIdStr.trim()));
            }
            
            // Set required fields
            customer.setFirstName(record.get("first_name"));
            customer.setLastName(record.get("last_name"));
            customer.setEmail(record.get("email"));
            customer.setPhone(record.get("phone"));
            
            // Set optional fields
            String allergies = record.get("allergies");
            if (allergies != null && !allergies.trim().isEmpty()) {
                customer.setAllergies(allergies);
            }
            
            String notes = record.get("notes");
            if (notes != null && !notes.trim().isEmpty()) {
                customer.setNotes(notes);
            }
            
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid number format in customer record: " + e.getMessage(), e);
        }
        
        return customer;
    }
    
    @Override
    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        columnNames.add("customer_id");
        columnNames.add("first_name");
        columnNames.add("last_name");
        columnNames.add("email");
        columnNames.add("phone");
        columnNames.add("allergies");
        columnNames.add("notes");
        return columnNames;
    }
    
    @Override
    public String getPrimaryKeyField() {
        return PRIMARY_KEY_FIELD;
    }
    
    @Override
    public Object getPrimaryKey(Customer entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Customer entity cannot be null");
        }
        return entity.getCustomerId();
    }
    
    @Override
    public void setPrimaryKey(Customer entity, Object id) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Customer entity cannot be null");
        }
        
        if (id instanceof Long) {
            entity.setCustomerId((Long) id);
        } else if (id instanceof String) {
            try {
                entity.setCustomerId(Long.parseLong((String) id));
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid customer ID format: " + id, e);
            }
        } else {
            throw new EntityValidationException("Customer ID must be a Long or String");
        }
    }
    
    @Override
    public void validateEntity(Customer entity) throws EntityValidationException {
        if (entity == null) {
            throw new EntityValidationException("Customer entity cannot be null");
        }
        
        // Validate first_name (required)
        if (entity.getFirstName() == null || entity.getFirstName().trim().isEmpty()) {
            throw new EntityValidationException("Customer first_name is required");
        }
        
        // Validate last_name (required)
        if (entity.getLastName() == null || entity.getLastName().trim().isEmpty()) {
            throw new EntityValidationException("Customer last_name is required");
        }
        
        // Validate email (required and must be valid format)
        if (entity.getEmail() == null || entity.getEmail().trim().isEmpty()) {
            throw new EntityValidationException("Customer email is required");
        }
        
        if (!EMAIL_PATTERN.matcher(entity.getEmail()).matches()) {
            throw new EntityValidationException("Invalid email format: " + entity.getEmail());
        }
        
        // Validate phone (required)
        if (entity.getPhone() == null || entity.getPhone().trim().isEmpty()) {
            throw new EntityValidationException("Customer phone is required");
        }
    }
    
    @Override
    public void validateCsvRecord(Map<String, String> record) throws EntityValidationException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        // Validate first_name (required)
        String firstName = record.get("first_name");
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new EntityValidationException("Customer first_name is required in CSV record");
        }
        
        // Validate last_name (required)
        String lastName = record.get("last_name");
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new EntityValidationException("Customer last_name is required in CSV record");
        }
        
        // Validate email (required and must be valid format)
        String email = record.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new EntityValidationException("Customer email is required in CSV record");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new EntityValidationException("Invalid email format in CSV record: " + email);
        }
        
        // Validate phone (required)
        String phone = record.get("phone");
        if (phone == null || phone.trim().isEmpty()) {
            throw new EntityValidationException("Customer phone is required in CSV record");
        }
        
        // Validate customer_id format if present
        String customerId = record.get("customer_id");
        if (customerId != null && !customerId.trim().isEmpty()) {
            try {
                Long.parseLong(customerId.trim());
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid customer_id format in CSV record: " + customerId, e);
            }
        }
    }
}
