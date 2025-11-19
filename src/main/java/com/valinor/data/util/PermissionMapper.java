package com.valinor.data.util;

import com.valinor.data.entity.Permission;
import com.valinor.data.entity.UserRole;

import java.util.Collections;
import java.util.Set;

/**
 * Utility class for mapping user roles to their permissions.
 * Provides centralized permission management based on roles.
 */
public final class PermissionMapper {
    
    /**
     * Private constructor to prevent instantiation.
     */
    private PermissionMapper() {
        throw new UnsupportedOperationException("PermissionMapper is a utility class and cannot be instantiated");
    }
    
    /**
     * Gets all permissions for a given user role.
     * 
     * @param role the user role
     * @return an unmodifiable set of permissions for the role
     * @throws IllegalArgumentException if role is null
     */
    public static Set<Permission> getPermissionsForRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        
        return Collections.unmodifiableSet(role.getDefaultPermissions());
    }
    
    /**
     * Checks if a role has a specific permission.
     * 
     * @param role the user role
     * @param permission the permission to check
     * @return true if the role has the permission, false otherwise
     * @throws IllegalArgumentException if either parameter is null
     */
    public static boolean roleHasPermission(UserRole role, Permission permission) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        
        if (permission == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }
        
        return role.hasPermission(permission);
    }
    
    /**
     * Checks if a role has any of the specified permissions.
     * 
     * @param role the user role
     * @param permissions the permissions to check
     * @return true if the role has at least one of the permissions
     * @throws IllegalArgumentException if role is null or permissions is null/empty
     */
    public static boolean roleHasAnyPermission(UserRole role, Permission... permissions) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("Permissions cannot be null or empty");
        }
        
        return role.hasAnyPermission(permissions);
    }
    
    /**
     * Checks if a role has all of the specified permissions.
     * 
     * @param role the user role
     * @param permissions the permissions to check
     * @return true if the role has all of the permissions
     * @throws IllegalArgumentException if role is null or permissions is null/empty
     */
    public static boolean roleHasAllPermissions(UserRole role, Permission... permissions) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("Permissions cannot be null or empty");
        }
        
        return role.hasAllPermissions(permissions);
    }
    
    /**
     * Gets a count of how many permissions a role has.
     * 
     * @param role the user role
     * @return the number of permissions
     * @throws IllegalArgumentException if role is null
     */
    public static int getPermissionCount(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        
        return role.getDefaultPermissions().size();
    }
}
