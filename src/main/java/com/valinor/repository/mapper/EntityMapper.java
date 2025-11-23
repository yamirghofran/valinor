package com.valinor.repository.mapper;

import com.valinor.exception.RepositoryException;
import java.util.List;
import java.util.Map;

/**
 * Interface for mapping between entities and CSV records.
 * This interface provides the contract for converting entities to/from
 * CSV format, handling data type conversion and validation.
 *
 * @param <T> the type of entity this mapper handles
 */
public interface EntityMapper<T> {
    
    /**
     * Converts an entity to a CSV record (map of column names to values).
     * 
     * @param entity the entity to convert
     * @return a map representing the CSV record
     * @throws RepositoryException if conversion fails
     */
    Map<String, String> toCsvRecord(T entity) throws RepositoryException;
    
    /**
     * Converts a CSV record (map of column names to values) to an entity.
     * 
     * @param record the CSV record to convert
     * @return the converted entity
     * @throws RepositoryException if conversion fails
     */
    T fromCsvRecord(Map<String, String> record) throws RepositoryException;
    
    /**
     * Returns the column names (header) for the CSV file.
     * The order of columns in the list determines the CSV file structure.
     * 
     * @return a list of column names
     */
    List<String> getColumnNames();
    
    /**
     * Returns the name of the primary key field.
     * 
     * @return the primary key field name
     */
    String getPrimaryKeyField();
    
    /**
     * Extracts the primary key value from an entity.
     * 
     * @param entity the entity
     * @return the primary key value
     * @throws RepositoryException if extraction fails
     */
    Object getPrimaryKey(T entity) throws RepositoryException;
    
    /**
     * Sets the primary key value on an entity.
     * This is used when generating new IDs for entities.
     * 
     * @param entity the entity to update
     * @param id the primary key value to set
     * @throws RepositoryException if setting fails
     */
    void setPrimaryKey(T entity, Object id) throws RepositoryException;
    
    /**
     * Validates that an entity has all required fields populated.
     * 
     * @param entity the entity to validate
     * @throws RepositoryException if validation fails
     */
    void validateEntity(T entity) throws RepositoryException;
    
    /**
     * Validates that a CSV record has all required columns and valid data.
     * 
     * @param record the CSV record to validate
     * @throws RepositoryException if validation fails
     */
    void validateCsvRecord(Map<String, String> record) throws RepositoryException;
}