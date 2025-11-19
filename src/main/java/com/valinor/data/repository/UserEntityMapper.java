package com.valinor.data.repository;

import com.valinor.data.entity.User;
import com.valinor.data.entity.UserRole;
import com.valinor.data.exception.EntityValidationException;
import com.valinor.data.exception.RepositoryException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity mapper for User entities.
 * Handles conversion between User objects and CSV records.
 */
public class UserEntityMapper implements EntityMapper<User> {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private static final String[] COLUMN_NAMES = {
        "user_id", "username", "password_hash", "email", "first_name", "last_name",
        "phone", "role", "restaurant_id", "is_active", "created_at", "last_login"
    };
    
    private static final String PRIMARY_KEY_FIELD = "user_id";
    
    @Override
    public Map<String, String> toCsvRecord(User entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("User entity cannot be null");
        }
        
        validateEntity(entity);
        
        Map<String, String> record = new LinkedHashMap<>();
        record.put("user_id", entity.getUserId() != null ? entity.getUserId().toString() : "");
        record.put("username", entity.getUsername() != null ? entity.getUsername() : "");
        record.put("password_hash", entity.getPasswordHash() != null ? entity.getPasswordHash() : "");
        record.put("email", entity.getEmail() != null ? entity.getEmail() : "");
        record.put("first_name", entity.getFirstName() != null ? entity.getFirstName() : "");
        record.put("last_name", entity.getLastName() != null ? entity.getLastName() : "");
        record.put("phone", entity.getPhone() != null ? entity.getPhone() : "");
        record.put("role", entity.getRole() != null ? entity.getRole().name() : "");
        record.put("restaurant_id", entity.getRestaurantId() != null ? entity.getRestaurantId().toString() : "");
        record.put("is_active", entity.getIsActive() != null ? entity.getIsActive().toString() : "true");
        record.put("created_at", entity.getCreatedAt() != null ? entity.getCreatedAt().format(DATE_TIME_FORMATTER) : "");
        record.put("last_login", entity.getLastLogin() != null ? entity.getLastLogin().format(DATE_TIME_FORMATTER) : "");
        
        return record;
    }
    
    @Override
    public User fromCsvRecord(Map<String, String> record) throws RepositoryException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        validateCsvRecord(record);
        
        User user = new User();
        
        try {
            // Parse user ID
            String userIdStr = record.get("user_id");
            if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                user.setUserId(Long.parseLong(userIdStr.trim()));
            }
            
            // Set basic fields
            user.setUsername(record.get("username"));
            user.setPasswordHash(record.get("password_hash"));
            user.setEmail(record.get("email"));
            user.setFirstName(record.get("first_name"));
            user.setLastName(record.get("last_name"));
            user.setPhone(record.get("phone"));
            
            // Parse role
            String roleStr = record.get("role");
            if (roleStr != null && !roleStr.trim().isEmpty()) {
                try {
                    user.setRole(UserRole.valueOf(roleStr.trim()));
                } catch (IllegalArgumentException e) {
                    throw new EntityValidationException("Invalid user role: " + roleStr, e);
                }
            }
            
            // Parse restaurant ID
            String restaurantIdStr = record.get("restaurant_id");
            if (restaurantIdStr != null && !restaurantIdStr.trim().isEmpty()) {
                user.setRestaurantId(Long.parseLong(restaurantIdStr.trim()));
            }
            
            // Parse is_active
            String isActiveStr = record.get("is_active");
            if (isActiveStr != null && !isActiveStr.trim().isEmpty()) {
                user.setIsActive(Boolean.parseBoolean(isActiveStr.trim()));
            } else {
                user.setIsActive(true);
            }
            
            // Parse created_at
            String createdAtStr = record.get("created_at");
            if (createdAtStr != null && !createdAtStr.trim().isEmpty()) {
                try {
                    user.setCreatedAt(LocalDateTime.parse(createdAtStr.trim(), DATE_TIME_FORMATTER));
                } catch (DateTimeParseException e) {
                    throw new EntityValidationException("Invalid created_at format: " + createdAtStr, e);
                }
            }
            
            // Parse last_login
            String lastLoginStr = record.get("last_login");
            if (lastLoginStr != null && !lastLoginStr.trim().isEmpty()) {
                try {
                    user.setLastLogin(LocalDateTime.parse(lastLoginStr.trim(), DATE_TIME_FORMATTER));
                } catch (DateTimeParseException e) {
                    throw new EntityValidationException("Invalid last_login format: " + lastLoginStr, e);
                }
            }
            
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid number format in user record", e);
        }
        
        return user;
    }
    
    @Override
    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        for (String column : COLUMN_NAMES) {
            columnNames.add(column);
        }
        return columnNames;
    }
    
    @Override
    public String getPrimaryKeyField() {
        return PRIMARY_KEY_FIELD;
    }
    
    @Override
    public Object getPrimaryKey(User entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("User entity cannot be null");
        }
        return entity.getUserId();
    }
    
    @Override
    public void setPrimaryKey(User entity, Object id) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("User entity cannot be null");
        }
        
        if (id instanceof Long) {
            entity.setUserId((Long) id);
        } else if (id instanceof String) {
            try {
                entity.setUserId(Long.parseLong((String) id));
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid user ID format: " + id, e);
            }
        } else {
            throw new EntityValidationException("User ID must be a Long or String");
        }
    }
    
    @Override
    public void validateEntity(User entity) throws EntityValidationException {
        if (entity == null) {
            throw new EntityValidationException("User entity cannot be null");
        }
        
        // Validate username
        if (entity.getUsername() == null || entity.getUsername().trim().isEmpty()) {
            throw new EntityValidationException("Username is required");
        }
        
        // Validate password hash
        if (entity.getPasswordHash() == null || entity.getPasswordHash().trim().isEmpty()) {
            throw new EntityValidationException("Password hash is required");
        }
        
        // Validate email
        if (entity.getEmail() == null || entity.getEmail().trim().isEmpty()) {
            throw new EntityValidationException("Email is required");
        }
        
        // Basic email validation
        String email = entity.getEmail().trim();
        if (!email.contains("@") || !email.contains(".")) {
            throw new EntityValidationException("Invalid email format: " + email);
        }
        
        // Validate first name
        if (entity.getFirstName() == null || entity.getFirstName().trim().isEmpty()) {
            throw new EntityValidationException("First name is required");
        }
        
        // Validate last name
        if (entity.getLastName() == null || entity.getLastName().trim().isEmpty()) {
            throw new EntityValidationException("Last name is required");
        }
        
        // Validate role
        if (entity.getRole() == null) {
            throw new EntityValidationException("User role is required");
        }
    }
    
    @Override
    public void validateCsvRecord(Map<String, String> record) throws EntityValidationException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        // Check required fields
        if (!record.containsKey("username") || record.get("username") == null || record.get("username").trim().isEmpty()) {
            throw new EntityValidationException("Username is required in CSV record");
        }
        
        if (!record.containsKey("password_hash") || record.get("password_hash") == null || record.get("password_hash").trim().isEmpty()) {
            throw new EntityValidationException("Password hash is required in CSV record");
        }
        
        if (!record.containsKey("email") || record.get("email") == null || record.get("email").trim().isEmpty()) {
            throw new EntityValidationException("Email is required in CSV record");
        }
        
        if (!record.containsKey("first_name") || record.get("first_name") == null || record.get("first_name").trim().isEmpty()) {
            throw new EntityValidationException("First name is required in CSV record");
        }
        
        if (!record.containsKey("last_name") || record.get("last_name") == null || record.get("last_name").trim().isEmpty()) {
            throw new EntityValidationException("Last name is required in CSV record");
        }
        
        if (!record.containsKey("role") || record.get("role") == null || record.get("role").trim().isEmpty()) {
            throw new EntityValidationException("Role is required in CSV record");
        }
        
        // Validate user ID format if present
        String userId = record.get("user_id");
        if (userId != null && !userId.trim().isEmpty()) {
            try {
                Long.parseLong(userId.trim());
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid user ID format in CSV record: " + userId, e);
            }
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
