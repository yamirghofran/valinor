package com.valinor.repository;

import com.valinor.domain.enums.UserRole;
import com.valinor.domain.model.User;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.mapper.UserEntityMapper;

import java.util.List;
import java.util.Optional;

/**
 * CSV-based repository for User entities.
 * Provides CRUD operations for user data stored in CSV files.
 */
public class UserRepository extends AbstractCsvRepository<User, Long> {
    
    /**
     * Constructs a new UserRepository.
     * 
     * @param filePath the path to the users CSV file
     * @throws RepositoryException if initialization fails
     */
    public UserRepository(String filePath) throws RepositoryException {
        super(filePath, new UserEntityMapper());
    }
    
    /**
     * Finds a user by username.
     * 
     * @param username the username to search for
     * @return optional containing the user if found
     * @throws RepositoryException if search fails
     */
    public Optional<User> findByUsername(String username) throws RepositoryException {
        return findOneByField("username", username);
    }
    
    /**
     * Finds a user by email address.
     * 
     * @param email the email to search for
     * @return optional containing the user if found
     * @throws RepositoryException if search fails
     */
    public Optional<User> findByEmail(String email) throws RepositoryException {
        return findOneByField("email", email);
    }
    
    /**
     * Finds all users with a specific role.
     * 
     * @param role the user role to search for
     * @return list of users with the specified role
     * @throws RepositoryException if search fails
     */
    public List<User> findByRole(UserRole role) throws RepositoryException {
        return findByField("role", role);
    }
    
    /**
     * Finds all users associated with a specific restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @return list of users associated with the restaurant
     * @throws RepositoryException if search fails
     */
    public List<User> findByRestaurantId(Long restaurantId) throws RepositoryException {
        return findByField("restaurant_id", restaurantId);
    }
    
    /**
     * Finds all active users.
     * 
     * @return list of active users
     * @throws RepositoryException if search fails
     */
    public List<User> findActiveUsers() throws RepositoryException {
        return findByField("is_active", true);
    }
    
    /**
     * Finds users by restaurant and role.
     * 
     * @param restaurantId the restaurant ID
     * @param role the user role
     * @return list of users matching both criteria
     * @throws RepositoryException if search fails
     */
    public List<User> findByRestaurantIdAndRole(Long restaurantId, UserRole role) throws RepositoryException {
        return findWhere(user -> 
            user.getRestaurantId() != null && 
            user.getRestaurantId().equals(restaurantId) && 
            user.getRole() == role
        );
    }
    
    /**
     * Checks if a username already exists.
     * 
     * @param username the username to check
     * @return true if the username exists, false otherwise
     * @throws RepositoryException if check fails
     */
    public boolean existsByUsername(String username) throws RepositoryException {
        return findByUsername(username).isPresent();
    }
    
    /**
     * Checks if an email already exists.
     * 
     * @param email the email to check
     * @return true if the email exists, false otherwise
     * @throws RepositoryException if check fails
     */
    public boolean existsByEmail(String email) throws RepositoryException {
        return findByEmail(email).isPresent();
    }
    
    /**
     * Finds all inactive users.
     * 
     * @return list of inactive users
     * @throws RepositoryException if search fails
     */
    public List<User> findInactiveUsers() throws RepositoryException {
        return findByField("is_active", false);
    }
    
    /**
     * Finds all users without a restaurant assignment (system-level users).
     * 
     * @return list of users without restaurant assignment
     * @throws RepositoryException if search fails
     */
    public List<User> findUnassignedUsers() throws RepositoryException {
        return findWhere(user -> user.getRestaurantId() == null);
    }
    
    /**
     * Counts users by role.
     * 
     * @param role the user role
     * @return the number of users with the specified role
     * @throws RepositoryException if count fails
     */
    public long countByRole(UserRole role) throws RepositoryException {
        return findByRole(role).size();
    }
    
    /**
     * Counts users by restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @return the number of users associated with the restaurant
     * @throws RepositoryException if count fails
     */
    public long countByRestaurantId(Long restaurantId) throws RepositoryException {
        return findByRestaurantId(restaurantId).size();
    }
    
    @Override
    protected boolean fieldValueMatches(User entity, String fieldName, Object expectedValue) {
        if (entity == null || expectedValue == null) {
            return false;
        }
        
        try {
            switch (fieldName.toLowerCase()) {
                case "user_id":
                    return expectedValue.equals(entity.getUserId());
                case "username":
                    return expectedValue.equals(entity.getUsername());
                case "email":
                    return expectedValue.equals(entity.getEmail());
                case "first_name":
                    return expectedValue.equals(entity.getFirstName());
                case "last_name":
                    return expectedValue.equals(entity.getLastName());
                case "phone":
                    return expectedValue.equals(entity.getPhone());
                case "role":
                    if (expectedValue instanceof UserRole) {
                        return expectedValue == entity.getRole();
                    } else if (expectedValue instanceof String) {
                        return expectedValue.equals(entity.getRole().name());
                    }
                    return false;
                case "restaurant_id":
                    return expectedValue.equals(entity.getRestaurantId());
                case "is_active":
                    if (expectedValue instanceof Boolean) {
                        return expectedValue.equals(entity.getIsActive());
                    } else if (expectedValue instanceof String) {
                        return Boolean.parseBoolean((String) expectedValue) == entity.getIsActive();
                    }
                    return false;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
