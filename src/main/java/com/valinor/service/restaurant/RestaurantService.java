package com.valinor.service.restaurant;

import com.valinor.domain.model.Restaurant;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing restaurants.
 * Provides business logic for restaurant CRUD operations.
 */
public class RestaurantService {
    
    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);
    private final RestaurantRepository restaurantRepository;
    
    /**
     * Constructs a new RestaurantService.
     * 
     * @param restaurantRepository the restaurant repository
     */
    public RestaurantService(RestaurantRepository restaurantRepository) {
        if (restaurantRepository == null) {
            throw new IllegalArgumentException("RestaurantRepository cannot be null");
        }
        this.restaurantRepository = restaurantRepository;
    }
    
    /**
     * Creates a new restaurant.
     * 
     * @param name the restaurant name
     * @param location the restaurant location
     * @param contactEmail the contact email
     * @param contactPhone the contact phone
     * @return the created restaurant
     * @throws RepositoryException if creation fails
     */
    public Restaurant createRestaurant(String name, String location, String contactEmail, String contactPhone) 
            throws RepositoryException {
        
        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant name is required");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant location is required");
        }
        if (contactEmail == null || contactEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact email is required");
        }
        if (contactPhone == null || contactPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact phone is required");
        }
        
        // Check for duplicate email
        Optional<Restaurant> existing = restaurantRepository.findOneByContactEmail(contactEmail);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A restaurant with this contact email already exists");
        }
        
        Restaurant restaurant = new Restaurant(name, location, contactEmail, contactPhone);
        restaurant = restaurantRepository.save(restaurant);
        
        logger.info("Created new restaurant: {} (ID: {})", restaurant.getName(), restaurant.getRestaurantId());
        return restaurant;
    }
    
    /**
     * Gets a restaurant by ID.
     * 
     * @param restaurantId the restaurant ID
     * @return optional containing the restaurant if found
     * @throws RepositoryException if retrieval fails
     */
    public Optional<Restaurant> getRestaurantById(Long restaurantId) throws RepositoryException {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID cannot be null");
        }
        return restaurantRepository.findById(restaurantId);
    }
    
    /**
     * Gets all restaurants.
     * 
     * @return list of all restaurants
     * @throws RepositoryException if retrieval fails
     */
    public List<Restaurant> getAllRestaurants() throws RepositoryException {
        return restaurantRepository.findAll();
    }
    
    /**
     * Updates a restaurant.
     * 
     * @param restaurant the restaurant to update
     * @return the updated restaurant
     * @throws RepositoryException if update fails
     */
    public Restaurant updateRestaurant(Restaurant restaurant) throws RepositoryException {
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null");
        }
        if (restaurant.getRestaurantId() == null) {
            throw new IllegalArgumentException("Restaurant ID cannot be null");
        }
        
        return restaurantRepository.update(restaurant);
    }
    
    /**
     * Deletes a restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @return true if deleted, false if not found
     * @throws RepositoryException if deletion fails
     */
    public boolean deleteRestaurant(Long restaurantId) throws RepositoryException {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID cannot be null");
        }
        
        boolean deleted = restaurantRepository.deleteById(restaurantId);
        if (deleted) {
            logger.info("Deleted restaurant with ID: {}", restaurantId);
        }
        return deleted;
    }
}
