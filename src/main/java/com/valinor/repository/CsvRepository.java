package com.valinor.repository;

import com.valinor.exception.RepositoryException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for CSV-based repositories extending the generic Repository interface.
 * This interface provides additional methods specific to CSV file operations.
 *
 * @param <T> the type of entity this repository manages
 * @param <ID> the type of the entity's identifier
 */
public interface CsvRepository<T, ID> extends Repository<T, ID> {
    
    /**
     * Finds entities by a custom field-value pair.
     * This is useful for searching by non-primary key fields.
     * 
     * @param fieldName the name of the field to search by
     * @param value the value to match
     * @return a list of entities matching the criteria
     * @throws RepositoryException if an error occurs during search
     */
    List<T> findByField(String fieldName, Object value) throws RepositoryException;
    
    /**
     * Finds entities by multiple field-value pairs (AND condition).
     * 
     * @param criteria a map of field names to values to match
     * @return a list of entities matching all criteria
     * @throws RepositoryException if an error occurs during search
     */
    List<T> findByFields(Map<String, Object> criteria) throws RepositoryException;
    
    /**
     * Finds a single entity by a custom field-value pair.
     * Returns the first match if multiple exist.
     * 
     * @param fieldName the name of the field to search by
     * @param value the value to match
     * @return an Optional containing the entity if found, otherwise empty
     * @throws RepositoryException if an error occurs during search
     */
    Optional<T> findOneByField(String fieldName, Object value) throws RepositoryException;
    
    /**
     * Returns all entities that match a custom predicate.
     * This provides maximum flexibility for complex queries.
     * 
     * @param predicate a function that takes an entity and returns true if it matches
     * @return a list of entities matching the predicate
     * @throws RepositoryException if an error occurs during search
     */
    List<T> findWhere(java.util.function.Predicate<T> predicate) throws RepositoryException;
    
    /**
     * Saves multiple entities in a single operation for better performance.
     * 
     * @param entities the entities to save
     * @return the list of saved entities
     * @throws RepositoryException if an error occurs during save
     */
    List<T> saveAll(List<T> entities) throws RepositoryException;
    
    /**
     * Deletes multiple entities by their identifiers.
     * 
     * @param ids the identifiers of entities to delete
     * @return the number of entities deleted
     * @throws RepositoryException if an error occurs during deletion
     */
    int deleteAllById(List<ID> ids) throws RepositoryException;
    
    /**
     * Deletes all entities that match the given criteria.
     * 
     * @param criteria a map of field names to values to match
     * @return the number of entities deleted
     * @throws RepositoryException if an error occurs during deletion
     */
    int deleteByFields(Map<String, Object> criteria) throws RepositoryException;
    
    /**
     * Returns the path to the CSV file used by this repository.
     * 
     * @return the file path
     */
    String getFilePath();
    
    /**
     * Forces a reload of data from the CSV file.
     * This is useful when the file might have been modified externally.
     * 
     * @throws RepositoryException if an error occurs during reload
     */
    void reload() throws RepositoryException;
}