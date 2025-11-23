package com.valinor.service.user;

import com.valinor.domain.enums.Permission;
import com.valinor.domain.model.User;
import com.valinor.domain.enums.UserRole;
import com.valinor.exception.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Service for handling authorization and permission checks.
 * Provides role-based access control (RBAC) functionality.
 */
public class AuthorizationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
    
    /**
     * Checks if a user has a specific permission.
     * 
     * @param user the user to check
     * @param permission the permission to check for
     * @return true if the user has the permission, false otherwise
     * @throws IllegalArgumentException if user or permission is null
     */
    public boolean hasPermission(User user, Permission permission) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (permission == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }
        
        if (!user.isUsable()) {
            logger.debug("User {} is not usable (inactive or no role)", user.getUsername());
            return false;
        }
        
        boolean hasPermission = user.hasPermission(permission);
        logger.debug("User {} {} permission {}", user.getUsername(), 
                    hasPermission ? "has" : "does not have", permission);
        
        return hasPermission;
    }
    
    /**
     * Checks if a user has any of the specified permissions.
     * 
     * @param user the user to check
     * @param permissions the permissions to check for
     * @return true if the user has at least one of the permissions
     * @throws IllegalArgumentException if user is null or permissions is empty
     */
    public boolean hasAnyPermission(User user, Permission... permissions) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("At least one permission must be specified");
        }
        
        if (!user.isUsable()) {
            return false;
        }
        
        for (Permission permission : permissions) {
            if (user.hasPermission(permission)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if a user has all of the specified permissions.
     * 
     * @param user the user to check
     * @param permissions the permissions to check for
     * @return true if the user has all of the permissions
     * @throws IllegalArgumentException if user is null or permissions is empty
     */
    public boolean hasAllPermissions(User user, Permission... permissions) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("At least one permission must be specified");
        }
        
        if (!user.isUsable()) {
            return false;
        }
        
        for (Permission permission : permissions) {
            if (!user.hasPermission(permission)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets all permissions for a user based on their role.
     * 
     * @param user the user
     * @return a set of permissions
     * @throws IllegalArgumentException if user is null
     */
    public Set<Permission> getUserPermissions(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        return user.getPermissions();
    }
    
    /**
     * Checks if a user has a permission and throws an exception if not.
     * 
     * @param user the user to check
     * @param permission the required permission
     * @throws AuthorizationException if the user does not have the permission
     * @throws IllegalArgumentException if user or permission is null
     */
    public void checkPermission(User user, Permission permission) throws AuthorizationException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (permission == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }
        
        if (!user.isUsable()) {
            throw new AuthorizationException("User account is not active or has no role assigned");
        }
        
        if (!hasPermission(user, permission)) {
            logger.warn("Authorization failed: User {} does not have permission {}", 
                       user.getUsername(), permission);
            throw new AuthorizationException(
                String.format("User does not have required permission: %s", permission.getDisplayName())
            );
        }
    }
    
    /**
     * Checks if a user can access a specific restaurant's data.
     * System admins can access all restaurants.
     * Other users can only access their assigned restaurant.
     * 
     * @param user the user to check
     * @param restaurantId the restaurant ID to access
     * @return true if the user can access the restaurant
     * @throws IllegalArgumentException if user or restaurantId is null
     */
    public boolean canAccessRestaurant(User user, Long restaurantId) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID cannot be null");
        }
        
        if (!user.isUsable()) {
            return false;
        }
        
        // System admins can access all restaurants
        if (user.getRole() == UserRole.SYSTEM_ADMIN) {
            return true;
        }
        
        // Other users can only access their assigned restaurant
        boolean canAccess = user.getRestaurantId() != null && 
                           user.getRestaurantId().equals(restaurantId);
        
        logger.debug("User {} {} access restaurant {}", 
                    user.getUsername(), 
                    canAccess ? "can" : "cannot", 
                    restaurantId);
        
        return canAccess;
    }
    
    /**
     * Checks if a manager can manage another user.
     * Managers can only manage users within their own restaurant
     * and cannot manage users with equal or higher privileges.
     * 
     * @param manager the managing user
     * @param targetUser the user to be managed
     * @return true if the manager can manage the target user
     * @throws IllegalArgumentException if either parameter is null
     */
    public boolean canManageUser(User manager, User targetUser) {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null");
        }
        
        if (targetUser == null) {
            throw new IllegalArgumentException("Target user cannot be null");
        }
        
        if (!manager.isUsable()) {
            return false;
        }
        
        // Must have user management permissions
        if (!hasPermission(manager, Permission.UPDATE_USER)) {
            return false;
        }
        
        // System admins can manage all users
        if (manager.getRole() == UserRole.SYSTEM_ADMIN) {
            return true;
        }
        
        // Cannot manage users from other restaurants
        if (manager.getRestaurantId() == null || 
            !manager.getRestaurantId().equals(targetUser.getRestaurantId())) {
            return false;
        }
        
        // Cannot manage system admins
        if (targetUser.getRole() == UserRole.SYSTEM_ADMIN) {
            return false;
        }
        
        // Restaurant managers can manage staff and customers
        if (manager.getRole() == UserRole.RESTAURANT_MANAGER) {
            return targetUser.getRole() == UserRole.FRONT_OF_HOUSE_STAFF || 
                   targetUser.getRole() == UserRole.CUSTOMER;
        }
        
        return false;
    }
    
    /**
     * Checks restaurant access and throws an exception if denied.
     * 
     * @param user the user to check
     * @param restaurantId the restaurant ID to access
     * @throws AuthorizationException if access is denied
     * @throws IllegalArgumentException if user or restaurantId is null
     */
    public void checkRestaurantAccess(User user, Long restaurantId) throws AuthorizationException {
        if (!canAccessRestaurant(user, restaurantId)) {
            logger.warn("Authorization failed: User {} cannot access restaurant {}", 
                       user.getUsername(), restaurantId);
            throw new AuthorizationException(
                String.format("User does not have access to restaurant ID: %d", restaurantId)
            );
        }
    }
}
