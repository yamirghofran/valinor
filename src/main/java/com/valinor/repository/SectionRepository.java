package com.valinor.repository;

import com.valinor.domain.model.Section;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.mapper.SectionEntityMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * CSV-based repository for Section entities.
 * Provides CRUD operations for section data stored in CSV files.
 */
public class SectionRepository extends AbstractCsvRepository<Section, Long> {
    
    /**
     * Constructs a new SectionRepository.
     * 
     * @param filePath the path to the sections CSV file
     * @throws RepositoryException if initialization fails
     */
    public SectionRepository(String filePath) throws RepositoryException {
        super(filePath, new SectionEntityMapper());
    }
    
    /**
     * Finds sections by restaurant ID.
     * Critical for retrieving the layout of a specific restaurant.
     * 
     * @param restaurantId the restaurant ID to search for
     * @return list of sections belonging to the restaurant
     * @throws RepositoryException if search fails
     */
    public List<Section> findByRestaurantId(Long restaurantId) throws RepositoryException {
        return findByField("restaurant_id", restaurantId);
    }
    
    /**
     * Finds sections by name.
     * 
     * @param name the section name to search for
     * @return list of sections matching the name
     * @throws RepositoryException if search fails
     */
    public List<Section> findByName(String name) throws RepositoryException {
        return findByField("name", name);
    }
    
    /**
     * Finds a section by restaurant ID and name.
     * Useful for finding a specific section within a restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @param name the section name
     * @return list of sections matching both criteria
     * @throws RepositoryException if search fails
     */
    public List<Section> findByRestaurantIdAndName(Long restaurantId, String name) throws RepositoryException {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("restaurant_id", restaurantId);
        criteria.put("name", name);
        return findByFields(criteria);
    }
    
    /**
     * Finds a single section by restaurant ID and name.
     * 
     * @param restaurantId the restaurant ID
     * @param name the section name
     * @return optional containing the section if found
     * @throws RepositoryException if search fails
     */
    public Optional<Section> findOneByRestaurantIdAndName(Long restaurantId, String name) throws RepositoryException {
        List<Section> results = findByRestaurantIdAndName(restaurantId, name);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    /**
     * Counts the number of sections in a restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @return the count of sections
     * @throws RepositoryException if counting fails
     */
    public long countByRestaurantId(Long restaurantId) throws RepositoryException {
        return findByRestaurantId(restaurantId).size();
    }
    
    /**
     * Deletes all sections belonging to a restaurant.
     * Useful for cascade deletes when removing a restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @return the number of sections deleted
     * @throws RepositoryException if deletion fails
     */
    public int deleteByRestaurantId(Long restaurantId) throws RepositoryException {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("restaurant_id", restaurantId);
        return deleteByFields(criteria);
    }
    
    @Override
    protected boolean fieldValueMatches(Section entity, String fieldName, Object expectedValue) {
        if (entity == null || expectedValue == null) {
            return false;
        }
        
        try {
            switch (fieldName.toLowerCase()) {
                case "section_id":
                    return expectedValue.equals(entity.getSectionId());
                case "restaurant_id":
                    return expectedValue.equals(entity.getRestaurantId());
                case "name":
                    return expectedValue.equals(entity.getName());
                case "num_tables":
                    return expectedValue.equals(entity.getNumTables());
                case "notes":
                    return expectedValue.equals(entity.getNotes());
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
