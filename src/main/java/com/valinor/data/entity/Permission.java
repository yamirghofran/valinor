package com.valinor.data.entity;

/**
 * Enum representing permissions in the restaurant reservation system.
 * Permissions are granular capabilities that can be assigned to user roles.
 */
public enum Permission {
    
    // Restaurant Management Permissions
    MANAGE_RESTAURANT_SETTINGS("Manage Restaurant Settings", "Full control over restaurant configuration and settings"),
    VIEW_RESTAURANT_SETTINGS("View Restaurant Settings", "View restaurant configuration and settings"),
    
    // Section Management Permissions
    CREATE_SECTION("Create Section", "Create new sections in the restaurant layout"),
    UPDATE_SECTION("Update Section", "Modify existing sections"),
    DELETE_SECTION("Delete Section", "Remove sections from the restaurant layout"),
    VIEW_SECTIONS("View Sections", "View restaurant sections"),
    
    // Table Management Permissions
    CREATE_TABLE("Create Table", "Add new tables to sections"),
    UPDATE_TABLE("Update Table", "Modify existing table configurations"),
    DELETE_TABLE("Delete Table", "Remove tables from sections"),
    VIEW_TABLES("View Tables", "View table information"),
    
    // Reservation Management Permissions
    CREATE_RESERVATION("Create Reservation", "Create new reservations"),
    UPDATE_RESERVATION("Update Reservation", "Modify existing reservations"),
    CANCEL_RESERVATION("Cancel Reservation", "Cancel reservations"),
    VIEW_RESERVATIONS("View Reservations", "View reservation information"),
    VIEW_ALL_RESERVATIONS("View All Reservations", "View all reservations across the system"),
    ASSIGN_TABLE("Assign Table", "Assign tables to reservations"),
    UPDATE_RESERVATION_STATUS("Update Reservation Status", "Change reservation status (confirmed, completed, no-show, cancelled)"),
    
    // Customer Management Permissions
    CREATE_CUSTOMER("Create Customer", "Add new customer profiles"),
    UPDATE_CUSTOMER("Update Customer", "Modify customer information"),
    DELETE_CUSTOMER("Delete Customer", "Remove customer profiles"),
    VIEW_CUSTOMERS("View Customers", "View customer information"),
    VIEW_CUSTOMER_HISTORY("View Customer History", "View customer reservation history and notes"),
    
    // User Management Permissions
    CREATE_USER("Create User", "Create new user accounts"),
    UPDATE_USER("Update User", "Modify user account information"),
    DELETE_USER("Delete User", "Remove user accounts"),
    VIEW_USERS("View Users", "View user account information"),
    ASSIGN_ROLES("Assign Roles", "Assign and modify user roles");
    
    private final String displayName;
    private final String description;
    
    /**
     * Constructs a Permission enum value.
     * 
     * @param displayName the human-readable name of the permission
     * @param description a description of what the permission allows
     */
    Permission(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Gets the display name of the permission.
     * 
     * @return the human-readable permission name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of the permission.
     * 
     * @return the permission description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Returns a string representation of the permission.
     * 
     * @return the permission name and display name
     */
    @Override
    public String toString() {
        return name() + " (" + displayName + ")";
    }
}
