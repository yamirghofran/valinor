package com.valinor.repository.mapper;

import com.valinor.domain.model.Restaurant;
import com.valinor.exception.EntityValidationException;
import com.valinor.exception.RepositoryException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity mapper for Restaurant entities.
 * Handles conversion between Restaurant objects and CSV records.
 */
public class RestaurantEntityMapper implements EntityMapper<Restaurant> {
    
    private static final String[] COLUMN_NAMES = {
        "restaurant_id", "name", "location", "contact_email", "contact_phone"
    };
    
    private static final String PRIMARY_KEY_FIELD = "restaurant_id";
    
    @Override
    public Map<String, String> toCsvRecord(Restaurant entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Restaurant entity cannot be null");
        }
        
        validateEntity(entity);
        
        Map<String, String> record = new LinkedHashMap<>();
        record.put("restaurant_id", entity.getRestaurantId() != null ? entity.getRestaurantId().toString() : "");
        record.put("name", entity.getName() != null ? entity.getName() : "");
        record.put("location", entity.getLocation() != null ? entity.getLocation() : "");
        record.put("contact_email", entity.getContactEmail() != null ? entity.getContactEmail() : "");
        record.put("contact_phone", entity.getContactPhone() != null ? entity.getContactPhone() : "");
        
        return record;
    }
    
    @Override
    public Restaurant fromCsvRecord(Map<String, String> record) throws RepositoryException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        validateCsvRecord(record);
        
        Restaurant restaurant = new Restaurant();
        
        try {
            String restaurantIdStr = record.get("restaurant_id");
            if (restaurantIdStr != null && !restaurantIdStr.trim().isEmpty()) {
                restaurant.setRestaurantId(Long.parseLong(restaurantIdStr.trim()));
            }
            
            restaurant.setName(record.get("name"));
            restaurant.setLocation(record.get("location"));
            restaurant.setContactEmail(record.get("contact_email"));
            restaurant.setContactPhone(record.get("contact_phone"));
            
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid restaurant ID format: " + record.get("restaurant_id"), e);
        }
        
        return restaurant;
    }
    
    @Override
    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        columnNames.add("restaurant_id");
        columnNames.add("name");
        columnNames.add("location");
        columnNames.add("contact_email");
        columnNames.add("contact_phone");
        return columnNames;
    }
    
    @Override
    public String getPrimaryKeyField() {
        return PRIMARY_KEY_FIELD;
    }
    
    @Override
    public Object getPrimaryKey(Restaurant entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Restaurant entity cannot be null");
        }
        return entity.getRestaurantId();
    }
    
    @Override
    public void setPrimaryKey(Restaurant entity, Object id) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Restaurant entity cannot be null");
        }
        
        if (id instanceof Long) {
            entity.setRestaurantId((Long) id);
        } else if (id instanceof String) {
            try {
                entity.setRestaurantId(Long.parseLong((String) id));
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid restaurant ID format: " + id, e);
            }
        } else {
            throw new EntityValidationException("Restaurant ID must be a Long or String");
        }
    }
    
    @Override
    public void validateEntity(Restaurant entity) throws EntityValidationException {
        if (entity == null) {
            throw new EntityValidationException("Restaurant entity cannot be null");
        }
        
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new EntityValidationException("Restaurant name is required");
        }
        
        if (entity.getLocation() == null || entity.getLocation().trim().isEmpty()) {
            throw new EntityValidationException("Restaurant location is required");
        }
        
        if (entity.getContactEmail() == null || entity.getContactEmail().trim().isEmpty()) {
            throw new EntityValidationException("Restaurant contact email is required");
        }
        
        if (entity.getContactPhone() == null || entity.getContactPhone().trim().isEmpty()) {
            throw new EntityValidationException("Restaurant contact phone is required");
        }
        
        // Basic email validation
        String email = entity.getContactEmail().trim();
        if (!email.contains("@") || !email.contains(".")) {
            throw new EntityValidationException("Invalid email format: " + email);
        }
    }
    
    @Override
    public void validateCsvRecord(Map<String, String> record) throws EntityValidationException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        // Check required fields
        if (!record.containsKey("name") || record.get("name") == null || record.get("name").trim().isEmpty()) {
            throw new EntityValidationException("Restaurant name is required in CSV record");
        }
        
        if (!record.containsKey("location") || record.get("location") == null || record.get("location").trim().isEmpty()) {
            throw new EntityValidationException("Restaurant location is required in CSV record");
        }
        
        if (!record.containsKey("contact_email") || record.get("contact_email") == null || record.get("contact_email").trim().isEmpty()) {
            throw new EntityValidationException("Restaurant contact email is required in CSV record");
        }
        
        if (!record.containsKey("contact_phone") || record.get("contact_phone") == null || record.get("contact_phone").trim().isEmpty()) {
            throw new EntityValidationException("Restaurant contact phone is required in CSV record");
        }
        
        // Validate restaurant ID format if present
        String restaurantId = record.get("restaurant_id");
        if (restaurantId != null && !restaurantId.trim().isEmpty()) {
            try {
                Long.parseLong(restaurantId.trim());
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid restaurant ID format in CSV record: " + restaurantId, e);
            }
        }
    }
}