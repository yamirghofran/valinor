package com.valinor.repository;

import com.valinor.domain.model.Restaurant;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.mapper.RestaurantEntityMapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * CSV-based repository for Restaurant entities.
 * Provides CRUD operations for restaurant data stored in CSV files.
 */
public class RestaurantRepository extends AbstractCsvRepository<Restaurant, Long> {
    
    /**
     * Constructs a new RestaurantRepository.
     * 
     * @param filePath the path to the restaurants CSV file
     * @throws RepositoryException if initialization fails
     */
    public RestaurantRepository(String filePath) throws RepositoryException {
        super(filePath, new RestaurantEntityMapper());
    }
    
    /**
     * Finds restaurants by name.
     * 
     * @param name the restaurant name to search for
     * @return list of restaurants matching the name
     * @throws RepositoryException if search fails
     */
    public List<Restaurant> findByName(String name) throws RepositoryException {
        return findByField("name", name);
    }
    
    /**
     * Finds restaurants by location.
     * 
     * @param location the location to search for
     * @return list of restaurants matching the location
     * @throws RepositoryException if search fails
     */
    public List<Restaurant> findByLocation(String location) throws RepositoryException {
        return findByField("location", location);
    }
    
    /**
     * Finds restaurants by contact email.
     * 
     * @param email the contact email to search for
     * @return list of restaurants matching the email
     * @throws RepositoryException if search fails
     */
    public List<Restaurant> findByContactEmail(String email) throws RepositoryException {
        return findByField("contact_email", email);
    }
    
    /**
     * Finds a single restaurant by contact email.
     * 
     * @param email the contact email to search for
     * @return optional containing the restaurant if found
     * @throws RepositoryException if search fails
     */
    public Optional<Restaurant> findOneByContactEmail(String email) throws RepositoryException {
        return findOneByField("contact_email", email);
    }
    
    /**
     * Finds restaurants by name and location.
     * 
     * @param name the restaurant name to search for
     * @param location the location to search for
     * @return list of restaurants matching both criteria
     * @throws RepositoryException if search fails
     */
    public List<Restaurant> findByNameAndLocation(String name, String location) throws RepositoryException {
        return findByField("name", name);
    }
    
    @Override
    protected boolean fieldValueMatches(Restaurant entity, String fieldName, Object expectedValue) {
        if (entity == null || expectedValue == null) {
            return false;
        }
        
        try {
            switch (fieldName.toLowerCase()) {
                case "restaurant_id":
                    return expectedValue.equals(entity.getRestaurantId());
                case "name":
                    return expectedValue.equals(entity.getName());
                case "location":
                    return expectedValue.equals(entity.getLocation());
                case "contact_email":
                    return expectedValue.equals(entity.getContactEmail());
                case "contact_phone":
                    return expectedValue.equals(entity.getContactPhone());
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
