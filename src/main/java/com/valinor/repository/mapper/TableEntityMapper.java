package com.valinor.repository.mapper;

import com.valinor.domain.model.Table;
import com.valinor.exception.EntityValidationException;
import com.valinor.exception.RepositoryException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity mapper for Table entities.
 * Handles conversion between Table objects and CSV records.
 */
public class TableEntityMapper implements EntityMapper<Table> {
    
    private static final String[] COLUMN_NAMES = {
        "table_id", "section_id", "table_number", "capacity", "is_active"
    };
    
    private static final String PRIMARY_KEY_FIELD = "table_id";
    
    @Override
    public Map<String, String> toCsvRecord(Table entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Table entity cannot be null");
        }
        
        validateEntity(entity);
        
        Map<String, String> record = new LinkedHashMap<>();
        record.put("table_id", entity.getTableId() != null ? entity.getTableId().toString() : "");
        record.put("section_id", entity.getSectionId() != null ? entity.getSectionId().toString() : "");
        record.put("table_number", entity.getTableNumber() != null ? entity.getTableNumber() : "");
        record.put("capacity", entity.getCapacity() != null ? entity.getCapacity().toString() : "");
        record.put("is_active", entity.getIsActive() != null ? entity.getIsActive().toString() : "true");
        
        return record;
    }
    
    @Override
    public Table fromCsvRecord(Map<String, String> record) throws RepositoryException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        validateCsvRecord(record);
        
        Table table = new Table();
        
        try {
            // Parse table_id
            String tableIdStr = record.get("table_id");
            if (tableIdStr != null && !tableIdStr.trim().isEmpty()) {
                table.setTableId(Long.parseLong(tableIdStr.trim()));
            }
            
            // Parse section_id (required)
            String sectionIdStr = record.get("section_id");
            if (sectionIdStr != null && !sectionIdStr.trim().isEmpty()) {
                table.setSectionId(Long.parseLong(sectionIdStr.trim()));
            }
            
            // Set table_number (required)
            table.setTableNumber(record.get("table_number"));
            
            // Parse capacity (required)
            String capacityStr = record.get("capacity");
            if (capacityStr != null && !capacityStr.trim().isEmpty()) {
                table.setCapacity(Integer.parseInt(capacityStr.trim()));
            }
            
            // Parse is_active (default to true if not specified)
            String isActiveStr = record.get("is_active");
            if (isActiveStr != null && !isActiveStr.trim().isEmpty()) {
                table.setIsActive(Boolean.parseBoolean(isActiveStr.trim()));
            } else {
                table.setIsActive(true); // Default to active
            }
            
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid number format in table record: " + e.getMessage(), e);
        }
        
        return table;
    }
    
    @Override
    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        columnNames.add("table_id");
        columnNames.add("section_id");
        columnNames.add("table_number");
        columnNames.add("capacity");
        columnNames.add("is_active");
        return columnNames;
    }
    
    @Override
    public String getPrimaryKeyField() {
        return PRIMARY_KEY_FIELD;
    }
    
    @Override
    public Object getPrimaryKey(Table entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Table entity cannot be null");
        }
        return entity.getTableId();
    }
    
    @Override
    public void setPrimaryKey(Table entity, Object id) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Table entity cannot be null");
        }
        
        if (id instanceof Long) {
            entity.setTableId((Long) id);
        } else if (id instanceof String) {
            try {
                entity.setTableId(Long.parseLong((String) id));
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid table ID format: " + id, e);
            }
        } else {
            throw new EntityValidationException("Table ID must be a Long or String");
        }
    }
    
    @Override
    public void validateEntity(Table entity) throws EntityValidationException {
        if (entity == null) {
            throw new EntityValidationException("Table entity cannot be null");
        }
        
        // Validate section_id (required)
        if (entity.getSectionId() == null) {
            throw new EntityValidationException("Table section_id is required");
        }
        
        // Validate table_number (required)
        if (entity.getTableNumber() == null || entity.getTableNumber().trim().isEmpty()) {
            throw new EntityValidationException("Table table_number is required");
        }
        
        // Validate capacity (required and must be > 0)
        if (entity.getCapacity() == null) {
            throw new EntityValidationException("Table capacity is required");
        }
        
        if (entity.getCapacity() <= 0) {
            throw new EntityValidationException("Table capacity must be greater than 0");
        }
    }
    
    @Override
    public void validateCsvRecord(Map<String, String> record) throws EntityValidationException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        // Validate section_id (required)
        String sectionId = record.get("section_id");
        if (sectionId == null || sectionId.trim().isEmpty()) {
            throw new EntityValidationException("Table section_id is required in CSV record");
        }
        
        // Validate section_id format
        try {
            Long.parseLong(sectionId.trim());
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid section_id format in CSV record: " + sectionId, e);
        }
        
        // Validate table_number (required)
        String tableNumber = record.get("table_number");
        if (tableNumber == null || tableNumber.trim().isEmpty()) {
            throw new EntityValidationException("Table table_number is required in CSV record");
        }
        
        // Validate capacity (required and must be > 0)
        String capacity = record.get("capacity");
        if (capacity == null || capacity.trim().isEmpty()) {
            throw new EntityValidationException("Table capacity is required in CSV record");
        }
        
        try {
            int cap = Integer.parseInt(capacity.trim());
            if (cap <= 0) {
                throw new EntityValidationException("Table capacity must be greater than 0: " + cap);
            }
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid capacity format in CSV record: " + capacity, e);
        }
        
        // Validate table_id format if present
        String tableId = record.get("table_id");
        if (tableId != null && !tableId.trim().isEmpty()) {
            try {
                Long.parseLong(tableId.trim());
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid table_id format in CSV record: " + tableId, e);
            }
        }
        
        // Validate is_active format if present (should be true/false)
        String isActive = record.get("is_active");
        if (isActive != null && !isActive.trim().isEmpty()) {
            String normalized = isActive.trim().toLowerCase();
            if (!normalized.equals("true") && !normalized.equals("false")) {
                throw new EntityValidationException("Invalid is_active format in CSV record (must be true/false): " + isActive);
            }
        }
    }
}

