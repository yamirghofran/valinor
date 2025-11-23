package com.valinor.repository;

import com.valinor.exception.RepositoryException;
import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining basic CRUD operations.
 * This interface is decoupled from any specific storage mechanism.
 *
 * @param <T> the type of entity this repository manages
 * @param <ID> the type of the entity's identifier
 */
public interface Repository<T, ID> {
    
    /**
     * Saves an entity to the repository.
     * If the entity has a null ID, a new ID will be generated.
     * 
     * @param entity the entity to save
     * @return the saved entity (with generated ID if applicable)
     * @throws RepositoryException if an error occurs during save
     */
    T save(T entity) throws RepositoryException;
    
    /**
     * Finds an entity by its identifier.
     * 
     * @param id the identifier of the entity to find
     * @return an Optional containing the entity if found, otherwise empty
     * @throws RepositoryException if an error occurs during find
     */
    Optional<T> findById(ID id) throws RepositoryException;
    
    /**
     * Retrieves all entities from the repository.
     * 
     * @return a list of all entities
     * @throws RepositoryException if an error occurs during retrieval
     */
    List<T> findAll() throws RepositoryException;
    
    /**
     * Updates an existing entity.
     * The entity must have a valid ID.
     * 
     * @param entity the entity to update
     * @return the updated entity
     * @throws RepositoryException if an error occurs during update or entity not found
     */
    T update(T entity) throws RepositoryException;
    
    /**
     * Deletes an entity by its identifier.
     * 
     * @param id the identifier of the entity to delete
     * @return true if the entity was deleted, false if not found
     * @throws RepositoryException if an error occurs during deletion
     */
    boolean deleteById(ID id) throws RepositoryException;
    
    /**
     * Checks if an entity with the given identifier exists.
     * 
     * @param id the identifier to check
     * @return true if the entity exists, false otherwise
     * @throws RepositoryException if an error occurs during check
     */
    boolean existsById(ID id) throws RepositoryException;
    
    /**
     * Returns the number of entities in the repository.
     * 
     * @return the count of entities
     * @throws RepositoryException if an error occurs during count
     */
    long count() throws RepositoryException;
}