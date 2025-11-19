package com.valinor.data.repository;

import com.valinor.data.entity.UserSession;
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
 * Entity mapper for UserSession entities.
 * Handles conversion between UserSession objects and CSV records.
 */
public class UserSessionEntityMapper implements EntityMapper<UserSession> {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private static final String[] COLUMN_NAMES = {
        "session_id", "user_id", "session_token", "created_at", "expires_at", "ip_address", "is_active"
    };
    
    private static final String PRIMARY_KEY_FIELD = "session_id";
    
    @Override
    public Map<String, String> toCsvRecord(UserSession entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("UserSession entity cannot be null");
        }
        
        validateEntity(entity);
        
        Map<String, String> record = new LinkedHashMap<>();
        record.put("session_id", entity.getSessionId() != null ? entity.getSessionId().toString() : "");
        record.put("user_id", entity.getUserId() != null ? entity.getUserId().toString() : "");
        record.put("session_token", entity.getSessionToken() != null ? entity.getSessionToken() : "");
        record.put("created_at", entity.getCreatedAt() != null ? entity.getCreatedAt().format(DATE_TIME_FORMATTER) : "");
        record.put("expires_at", entity.getExpiresAt() != null ? entity.getExpiresAt().format(DATE_TIME_FORMATTER) : "");
        record.put("ip_address", entity.getIpAddress() != null ? entity.getIpAddress() : "");
        record.put("is_active", entity.getIsActive() != null ? entity.getIsActive().toString() : "true");
        
        return record;
    }
    
    @Override
    public UserSession fromCsvRecord(Map<String, String> record) throws RepositoryException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        validateCsvRecord(record);
        
        UserSession session = new UserSession();
        
        try {
            // Parse session ID
            String sessionIdStr = record.get("session_id");
            if (sessionIdStr != null && !sessionIdStr.trim().isEmpty()) {
                session.setSessionId(Long.parseLong(sessionIdStr.trim()));
            }
            
            // Parse user ID
            String userIdStr = record.get("user_id");
            if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                session.setUserId(Long.parseLong(userIdStr.trim()));
            }
            
            // Set session token
            session.setSessionToken(record.get("session_token"));
            
            // Parse created_at
            String createdAtStr = record.get("created_at");
            if (createdAtStr != null && !createdAtStr.trim().isEmpty()) {
                try {
                    session.setCreatedAt(LocalDateTime.parse(createdAtStr.trim(), DATE_TIME_FORMATTER));
                } catch (DateTimeParseException e) {
                    throw new EntityValidationException("Invalid created_at format: " + createdAtStr, e);
                }
            }
            
            // Parse expires_at
            String expiresAtStr = record.get("expires_at");
            if (expiresAtStr != null && !expiresAtStr.trim().isEmpty()) {
                try {
                    session.setExpiresAt(LocalDateTime.parse(expiresAtStr.trim(), DATE_TIME_FORMATTER));
                } catch (DateTimeParseException e) {
                    throw new EntityValidationException("Invalid expires_at format: " + expiresAtStr, e);
                }
            }
            
            // Set IP address
            session.setIpAddress(record.get("ip_address"));
            
            // Parse is_active
            String isActiveStr = record.get("is_active");
            if (isActiveStr != null && !isActiveStr.trim().isEmpty()) {
                session.setIsActive(Boolean.parseBoolean(isActiveStr.trim()));
            } else {
                session.setIsActive(true);
            }
            
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid number format in session record", e);
        }
        
        return session;
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
    public Object getPrimaryKey(UserSession entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("UserSession entity cannot be null");
        }
        return entity.getSessionId();
    }
    
    @Override
    public void setPrimaryKey(UserSession entity, Object id) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("UserSession entity cannot be null");
        }
        
        if (id instanceof Long) {
            entity.setSessionId((Long) id);
        } else if (id instanceof String) {
            try {
                entity.setSessionId(Long.parseLong((String) id));
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid session ID format: " + id, e);
            }
        } else {
            throw new EntityValidationException("Session ID must be a Long or String");
        }
    }
    
    @Override
    public void validateEntity(UserSession entity) throws EntityValidationException {
        if (entity == null) {
            throw new EntityValidationException("UserSession entity cannot be null");
        }
        
        // Validate user ID
        if (entity.getUserId() == null) {
            throw new EntityValidationException("User ID is required");
        }
        
        // Validate session token
        if (entity.getSessionToken() == null || entity.getSessionToken().trim().isEmpty()) {
            throw new EntityValidationException("Session token is required");
        }
        
        // Validate expires_at
        if (entity.getExpiresAt() == null) {
            throw new EntityValidationException("Expiration date is required");
        }
    }
    
    @Override
    public void validateCsvRecord(Map<String, String> record) throws EntityValidationException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        // Check required fields
        if (!record.containsKey("user_id") || record.get("user_id") == null || record.get("user_id").trim().isEmpty()) {
            throw new EntityValidationException("User ID is required in CSV record");
        }
        
        if (!record.containsKey("session_token") || record.get("session_token") == null || record.get("session_token").trim().isEmpty()) {
            throw new EntityValidationException("Session token is required in CSV record");
        }
        
        if (!record.containsKey("expires_at") || record.get("expires_at") == null || record.get("expires_at").trim().isEmpty()) {
            throw new EntityValidationException("Expiration date is required in CSV record");
        }
        
        // Validate session ID format if present
        String sessionId = record.get("session_id");
        if (sessionId != null && !sessionId.trim().isEmpty()) {
            try {
                Long.parseLong(sessionId.trim());
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid session ID format in CSV record: " + sessionId, e);
            }
        }
        
        // Validate user ID format
        String userId = record.get("user_id");
        try {
            Long.parseLong(userId.trim());
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid user ID format in CSV record: " + userId, e);
        }
    }
}
