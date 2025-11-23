package com.valinor.domain.enums;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Enum representing user roles in the restaurant reservation system.
 * Each role has a predefined set of permissions that determine what actions
 * users with that role can perform.
 */
public enum UserRole {
    
    /**
     * System administrator with full access to all system features.
     * Reserved for future use when multi-restaurant management is needed.
     */
    SYSTEM_ADMIN(
        "System Administrator",
        "Full system access across all restaurants",
        EnumSet.allOf(Permission.class)
    ),
    
    /**
     * Restaurant manager with full control over their restaurant.
     * Can manage layout, reservations, customers, and staff users.
     */
    RESTAURANT_MANAGER(
        "Restaurant Manager",
        "Full control over restaurant operations, layout, and staff",
        EnumSet.of(
            // Restaurant Management
            Permission.MANAGE_RESTAURANT_SETTINGS,
            Permission.VIEW_RESTAURANT_SETTINGS,
            
            // Section Management
            Permission.CREATE_SECTION,
            Permission.UPDATE_SECTION,
            Permission.DELETE_SECTION,
            Permission.VIEW_SECTIONS,
            
            // Table Management
            Permission.CREATE_TABLE,
            Permission.UPDATE_TABLE,
            Permission.DELETE_TABLE,
            Permission.VIEW_TABLES,
            
            // Reservation Management
            Permission.CREATE_RESERVATION,
            Permission.UPDATE_RESERVATION,
            Permission.CANCEL_RESERVATION,
            Permission.VIEW_RESERVATIONS,
            Permission.VIEW_ALL_RESERVATIONS,
            Permission.ASSIGN_TABLE,
            Permission.UPDATE_RESERVATION_STATUS,
            
            // Customer Management
            Permission.CREATE_CUSTOMER,
            Permission.UPDATE_CUSTOMER,
            Permission.DELETE_CUSTOMER,
            Permission.VIEW_CUSTOMERS,
            Permission.VIEW_CUSTOMER_HISTORY,
            
            // User Management (within their restaurant)
            Permission.CREATE_USER,
            Permission.UPDATE_USER,
            Permission.DELETE_USER,
            Permission.VIEW_USERS,
            Permission.ASSIGN_ROLES
        )
    ),
    
    /**
     * Front-of-house staff with access to daily operations.
     * Can manage reservations and customers but cannot modify layout or users.
     */
    FRONT_OF_HOUSE_STAFF(
        "Front of House Staff",
        "Manage daily reservations and customer interactions",
        EnumSet.of(
            // Restaurant Management (read-only)
            Permission.VIEW_RESTAURANT_SETTINGS,
            
            // Section Management (read-only)
            Permission.VIEW_SECTIONS,
            
            // Table Management (read-only)
            Permission.VIEW_TABLES,
            
            // Reservation Management
            Permission.CREATE_RESERVATION,
            Permission.UPDATE_RESERVATION,
            Permission.CANCEL_RESERVATION,
            Permission.VIEW_RESERVATIONS,
            Permission.ASSIGN_TABLE,
            Permission.UPDATE_RESERVATION_STATUS,
            
            // Customer Management
            Permission.CREATE_CUSTOMER,
            Permission.UPDATE_CUSTOMER,
            Permission.VIEW_CUSTOMERS,
            Permission.VIEW_CUSTOMER_HISTORY
        )
    ),
    
    /**
     * Customer role for external users making reservations.
     * Limited to viewing and managing their own reservations.
     * This role is primarily for future integration with customer self-service.
     */
    CUSTOMER(
        "Customer",
        "Make and manage personal reservations",
        EnumSet.of(
            // Limited reservation access (own reservations only)
            Permission.CREATE_RESERVATION,
            Permission.VIEW_RESERVATIONS,
            
            // Limited customer access (own profile only)
            Permission.UPDATE_CUSTOMER
        )
    );
    
    private final String displayName;
    private final String description;
    private final Set<Permission> defaultPermissions;
    
    /**
     * Constructs a UserRole enum value.
     * 
     * @param displayName the human-readable name of the role
     * @param description a description of the role's purpose
     * @param defaultPermissions the set of permissions granted to this role
     */
    UserRole(String displayName, String description, Set<Permission> defaultPermissions) {
        this.displayName = displayName;
        this.description = description;
        this.defaultPermissions = Collections.unmodifiableSet(defaultPermissions);
    }
    
    /**
     * Gets the display name of the role.
     * 
     * @return the human-readable role name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of the role.
     * 
     * @return the role description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the default permissions for this role.
     * 
     * @return an unmodifiable set of permissions
     */
    public Set<Permission> getDefaultPermissions() {
        return defaultPermissions;
    }
    
    /**
     * Checks if this role has a specific permission.
     * 
     * @param permission the permission to check
     * @return true if the role has the permission, false otherwise
     */
    public boolean hasPermission(Permission permission) {
        return defaultPermissions.contains(permission);
    }
    
    /**
     * Checks if this role has any of the specified permissions.
     * 
     * @param permissions the permissions to check
     * @return true if the role has at least one of the permissions
     */
    public boolean hasAnyPermission(Permission... permissions) {
        for (Permission permission : permissions) {
            if (defaultPermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if this role has all of the specified permissions.
     * 
     * @param permissions the permissions to check
     * @return true if the role has all of the permissions
     */
    public boolean hasAllPermissions(Permission... permissions) {
        for (Permission permission : permissions) {
            if (!defaultPermissions.contains(permission)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns a string representation of the role.
     * 
     * @return the role name and display name
     */
    @Override
    public String toString() {
        return name() + " (" + displayName + ")";
    }
}
