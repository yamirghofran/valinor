package com.valinor.data.repository;

import com.valinor.data.entity.Section;
import com.valinor.data.exception.EntityValidationException;
import com.valinor.data.exception.RepositoryException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity mapper for Section entities.
 * Handles conversion between Section objects and CSV records.
 */
public class SectionEntityMapper implements EntityMapper<Section> {
    
    private static final String[] COLUMN_NAMES = {
        "section_id", "restaurant_id", "name", "num_tables", "notes"
    };
    
    private static final String PRIMARY_KEY_FIELD = "section_id";
    
    @Override
    public Map<String, String> toCsvRecord(Section entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Section entity cannot be null");
        }
        
        validateEntity(entity);
        
        Map<String, String> record = new LinkedHashMap<>();
        record.put("section_id", entity.getSectionId() != null ? entity.getSectionId().toString() : "");
        record.put("restaurant_id", entity.getRestaurantId() != null ? entity.getRestaurantId().toString() : "");
        record.put("name", entity.getName() != null ? entity.getName() : "");
        record.put("num_tables", entity.getNumTables() != null ? entity.getNumTables().toString() : "");
        record.put("notes", entity.getNotes() != null ? entity.getNotes() : "");
        
        return record;
    }
    
    @Override
    public Section fromCsvRecord(Map<String, String> record) throws RepositoryException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        validateCsvRecord(record);
        
        Section section = new Section();
        
        try {
            // Parse section_id
            String sectionIdStr = record.get("section_id");
            if (sectionIdStr != null && !sectionIdStr.trim().isEmpty()) {
                section.setSectionId(Long.parseLong(sectionIdStr.trim()));
            }
            
            // Parse restaurant_id (required)
            String restaurantIdStr = record.get("restaurant_id");
            if (restaurantIdStr != null && !restaurantIdStr.trim().isEmpty()) {
                section.setRestaurantId(Long.parseLong(restaurantIdStr.trim()));
            }
            
            // Set name (required)
            section.setName(record.get("name"));
            
            // Parse num_tables (optional)
            String numTablesStr = record.get("num_tables");
            if (numTablesStr != null && !numTablesStr.trim().isEmpty()) {
                section.setNumTables(Integer.parseInt(numTablesStr.trim()));
            }
            
            // Set notes (optional)
            String notes = record.get("notes");
            if (notes != null && !notes.trim().isEmpty()) {
                section.setNotes(notes.trim());
            }
            
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid number format in section record: " + e.getMessage(), e);
        }
        
        return section;
    }
    
    @Override
    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        columnNames.add("section_id");
        columnNames.add("restaurant_id");
        columnNames.add("name");
        columnNames.add("num_tables");
        columnNames.add("notes");
        return columnNames;
    }
    
    @Override
    public String getPrimaryKeyField() {
        return PRIMARY_KEY_FIELD;
    }
    
    @Override
    public Object getPrimaryKey(Section entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Section entity cannot be null");
        }
        return entity.getSectionId();
    }
    
    @Override
    public void setPrimaryKey(Section entity, Object id) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Section entity cannot be null");
        }
        
        if (id instanceof Long) {
            entity.setSectionId((Long) id);
        } else if (id instanceof String) {
            try {
                entity.setSectionId(Long.parseLong((String) id));
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid section ID format: " + id, e);
            }
        } else {
            throw new EntityValidationException("Section ID must be a Long or String");
        }
    }
    
    @Override
    public void validateEntity(Section entity) throws EntityValidationException {
        if (entity == null) {
            throw new EntityValidationException("Section entity cannot be null");
        }
        
        // Validate restaurant_id (required)
        if (entity.getRestaurantId() == null) {
            throw new EntityValidationException("Section restaurant_id is required");
        }
        
        // Validate name (required)
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new EntityValidationException("Section name is required");
        }
        
        // Validate num_tables if present (must be non-negative)
        if (entity.getNumTables() != null && entity.getNumTables() < 0) {
            throw new EntityValidationException("Section num_tables must be non-negative");
        }
    }
    
    @Override
    public void validateCsvRecord(Map<String, String> record) throws EntityValidationException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        // Validate restaurant_id (required)
        String restaurantId = record.get("restaurant_id");
        if (restaurantId == null || restaurantId.trim().isEmpty()) {
            throw new EntityValidationException("Section restaurant_id is required in CSV record");
        }
        
        // Validate restaurant_id format
        try {
            Long.parseLong(restaurantId.trim());
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid restaurant_id format in CSV record: " + restaurantId, e);
        }
        
        // Validate name (required)
        String name = record.get("name");
        if (name == null || name.trim().isEmpty()) {
            throw new EntityValidationException("Section name is required in CSV record");
        }
        
        // Validate section_id format if present
        String sectionId = record.get("section_id");
        if (sectionId != null && !sectionId.trim().isEmpty()) {
            try {
                Long.parseLong(sectionId.trim());
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid section_id format in CSV record: " + sectionId, e);
            }
        }
        
        // Validate num_tables format if present
        String numTables = record.get("num_tables");
        if (numTables != null && !numTables.trim().isEmpty()) {
            try {
                int num = Integer.parseInt(numTables.trim());
                if (num < 0) {
                    throw new EntityValidationException("Section num_tables must be non-negative: " + num);
                }
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid num_tables format in CSV record: " + numTables, e);
            }
        }
    }
}

