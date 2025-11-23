package com.valinor.demo;

import com.valinor.service.dto.user.LoginRequest;
import com.valinor.service.dto.user.LoginResponse;
import com.valinor.service.dto.user.UserCreateRequest;
import com.valinor.service.dto.user.UserUpdateRequest;
import com.valinor.domain.enums.Permission;
import com.valinor.domain.model.User;
import com.valinor.domain.enums.UserRole;
import com.valinor.exception.AuthenticationException;
import com.valinor.exception.DuplicateUserException;
import com.valinor.exception.UserServiceException;
import com.valinor.repository.UserRepository;
import com.valinor.repository.UserSessionRepository;
import com.valinor.service.user.AuthenticationService;
import com.valinor.service.user.AuthorizationService;
import com.valinor.service.user.UserService;

import java.util.List;
import java.util.Optional;

/**
 * Demonstration application for the user management module.
 * Shows usage of all major features including user CRUD, authentication,
 * authorization, and session management.
 */
public class UserManagementDemo {
    
    private static final String USERS_CSV = "data/users.csv";
    private static final String SESSIONS_CSV = "data/user_sessions.csv";
    
    public static void main(String[] args) {
        System.out.println("=== Valinor User Management System Demo ===\n");
        
        try {
            // Initialize repositories
            UserRepository userRepo = new UserRepository(USERS_CSV);
            UserSessionRepository sessionRepo = new UserSessionRepository(SESSIONS_CSV);
            
            // Initialize services
            UserService userService = new UserService(userRepo);
            AuthenticationService authService = new AuthenticationService(userRepo, sessionRepo);
            AuthorizationService authzService = new AuthorizationService();
            
            // Run demo scenarios
            demonstrateUserCreation(userService);
            demonstrateAuthentication(authService, userService);
            demonstrateAuthorization(authzService, userService);
            demonstrateUserManagement(userService);
            demonstratePasswordManagement(userService);
            demonstrateSessionManagement(authService);
            
            System.out.println("\n=== Demo completed successfully! ===");
            
        } catch (Exception e) {
            System.err.println("Demo failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates user creation with different roles.
     */
    private static void demonstrateUserCreation(UserService userService) {
        System.out.println("--- 1. User Creation Demo ---");
        
        try {
            // Create a restaurant manager
            UserCreateRequest managerRequest = new UserCreateRequest(
                "john_manager",
                "SecurePass123",
                "john@restaurant.com",
                "John",
                "Manager",
                UserRole.RESTAURANT_MANAGER
            );
            managerRequest.setRestaurantId(1L);
            managerRequest.setPhone("+1-555-0101");
            
            User manager = userService.createUser(managerRequest);
            System.out.println("✓ Created manager: " + manager.getUsername() + " (ID: " + manager.getUserId() + ")");
            
            // Create front-of-house staff
            UserCreateRequest staffRequest = new UserCreateRequest(
                "sarah_staff",
                "StaffPass456",
                "sarah@restaurant.com",
                "Sarah",
                "Staff",
                UserRole.FRONT_OF_HOUSE_STAFF
            );
            staffRequest.setRestaurantId(1L);
            
            User staff = userService.createUser(staffRequest);
            System.out.println("✓ Created staff: " + staff.getUsername() + " (ID: " + staff.getUserId() + ")");
            
            // Create a customer
            UserCreateRequest customerRequest = new UserCreateRequest(
                "mike_customer",
                "CustomerPass789",
                "mike@email.com",
                "Mike",
                "Customer",
                UserRole.CUSTOMER
            );
            
            User customer = userService.createUser(customerRequest);
            System.out.println("✓ Created customer: " + customer.getUsername() + " (ID: " + customer.getUserId() + ")");
            
            // Attempt to create duplicate user
            try {
                userService.createUser(managerRequest);
                System.out.println("✗ Duplicate check failed!");
            } catch (DuplicateUserException e) {
                System.out.println("✓ Duplicate username correctly rejected");
            }
            
        } catch (UserServiceException e) {
            System.err.println("✗ User creation failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Demonstrates authentication and session creation.
     */
    private static void demonstrateAuthentication(AuthenticationService authService, UserService userService) {
        System.out.println("--- 2. Authentication Demo ---");
        
        try {
            // Successful login
            LoginRequest loginRequest = new LoginRequest("john_manager", "SecurePass123");
            loginRequest.setIpAddress("192.168.1.100");
            
            LoginResponse loginResponse = authService.login(loginRequest);
            System.out.println("✓ Login successful for: " + loginResponse.getUser().getUsername());
            System.out.println("  Session token: " + loginResponse.getSessionToken().substring(0, 8) + "...");
            System.out.println("  Expires at: " + loginResponse.getExpiresAt());
            System.out.println("  Permissions: " + loginResponse.getPermissions().size() + " granted");
            
            // Validate session
            Optional<User> validatedUser = authService.validateSession(loginResponse.getSessionToken());
            if (validatedUser.isPresent()) {
                System.out.println("✓ Session validated for: " + validatedUser.get().getUsername());
            }
            
            // Logout
            authService.logout(loginResponse.getSessionToken());
            System.out.println("✓ User logged out successfully");
            
            // Attempt login with wrong password
            try {
                LoginRequest badRequest = new LoginRequest("john_manager", "WrongPassword");
                authService.login(badRequest);
                System.out.println("✗ Authentication check failed!");
            } catch (AuthenticationException e) {
                System.out.println("✓ Invalid credentials correctly rejected");
            }
            
        } catch (AuthenticationException e) {
            System.err.println("✗ Authentication failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Demonstrates authorization and permission checking.
     */
    private static void demonstrateAuthorization(AuthorizationService authzService, UserService userService) {
        System.out.println("--- 3. Authorization Demo ---");
        
        try {
            Optional<User> managerOpt = userService.getUserByUsername("john_manager");
            Optional<User> staffOpt = userService.getUserByUsername("sarah_staff");
            Optional<User> customerOpt = userService.getUserByUsername("mike_customer");
            
            if (managerOpt.isPresent() && staffOpt.isPresent() && customerOpt.isPresent()) {
                User manager = managerOpt.get();
                User staff = staffOpt.get();
                User customer = customerOpt.get();
                
                // Check manager permissions
                System.out.println("Manager permissions:");
                System.out.println("  Can manage restaurant: " + 
                    authzService.hasPermission(manager, Permission.MANAGE_RESTAURANT_SETTINGS));
                System.out.println("  Can create users: " + 
                    authzService.hasPermission(manager, Permission.CREATE_USER));
                System.out.println("  Can create sections: " + 
                    authzService.hasPermission(manager, Permission.CREATE_SECTION));
                System.out.println("  Total permissions: " + authzService.getUserPermissions(manager).size());
                
                // Check staff permissions
                System.out.println("\nStaff permissions:");
                System.out.println("  Can manage restaurant: " + 
                    authzService.hasPermission(staff, Permission.MANAGE_RESTAURANT_SETTINGS));
                System.out.println("  Can create reservations: " + 
                    authzService.hasPermission(staff, Permission.CREATE_RESERVATION));
                System.out.println("  Can create customers: " + 
                    authzService.hasPermission(staff, Permission.CREATE_CUSTOMER));
                System.out.println("  Total permissions: " + authzService.getUserPermissions(staff).size());
                
                // Check customer permissions
                System.out.println("\nCustomer permissions:");
                System.out.println("  Can create reservations: " + 
                    authzService.hasPermission(customer, Permission.CREATE_RESERVATION));
                System.out.println("  Can manage restaurant: " + 
                    authzService.hasPermission(customer, Permission.MANAGE_RESTAURANT_SETTINGS));
                System.out.println("  Total permissions: " + authzService.getUserPermissions(customer).size());
                
                // Check restaurant access
                System.out.println("\nRestaurant access:");
                System.out.println("  Manager can access restaurant 1: " + 
                    authzService.canAccessRestaurant(manager, 1L));
                System.out.println("  Manager can access restaurant 2: " + 
                    authzService.canAccessRestaurant(manager, 2L));
                System.out.println("  Staff can access restaurant 1: " + 
                    authzService.canAccessRestaurant(staff, 1L));
                
                // Check user management capabilities
                System.out.println("\nUser management:");
                System.out.println("  Manager can manage staff: " + 
                    authzService.canManageUser(manager, staff));
                System.out.println("  Staff can manage customer: " + 
                    authzService.canManageUser(staff, customer));
                
                System.out.println("✓ Authorization checks completed");
            }
            
        } catch (UserServiceException e) {
            System.err.println("✗ Authorization demo failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Demonstrates user management operations.
     */
    private static void demonstrateUserManagement(UserService userService) {
        System.out.println("--- 4. User Management Demo ---");
        
        try {
            // List all users
            List<User> allUsers = userService.getAllUsers();
            System.out.println("Total users in system: " + allUsers.size());
            
            // List users by role
            List<User> managers = userService.getUsersByRole(UserRole.RESTAURANT_MANAGER);
            System.out.println("Restaurant managers: " + managers.size());
            
            List<User> staff = userService.getUsersByRole(UserRole.FRONT_OF_HOUSE_STAFF);
            System.out.println("Staff members: " + staff.size());
            
            // Update user
            Optional<User> userOpt = userService.getUserByUsername("sarah_staff");
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserUpdateRequest updateRequest = new UserUpdateRequest();
                updateRequest.setPhone("+1-555-0202");
                updateRequest.setEmail("sarah.updated@restaurant.com");
                
                userService.updateUser(user.getUserId(), updateRequest);
                System.out.println("✓ Updated user: " + user.getUsername());
            }
            
            // Deactivate and reactivate user
            Optional<User> customerOpt = userService.getUserByUsername("mike_customer");
            if (customerOpt.isPresent()) {
                Long customerId = customerOpt.get().getUserId();
                
                userService.deactivateUser(customerId);
                System.out.println("✓ Deactivated user");
                
                userService.activateUser(customerId);
                System.out.println("✓ Reactivated user");
            }
            
        } catch (UserServiceException e) {
            System.err.println("✗ User management failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Demonstrates password management.
     */
    private static void demonstratePasswordManagement(UserService userService) {
        System.out.println("--- 5. Password Management Demo ---");
        
        try {
            Optional<User> userOpt = userService.getUserByUsername("john_manager");
            if (userOpt.isPresent()) {
                Long userId = userOpt.get().getUserId();
                
                // Change password
                userService.changePassword(userId, "SecurePass123", "NewSecurePass456");
                System.out.println("✓ Password changed successfully");
                
                // Change it back for demo purposes
                userService.changePassword(userId, "NewSecurePass456", "SecurePass123");
                System.out.println("✓ Password changed back");
                
                // Attempt password change with wrong current password
                try {
                    userService.changePassword(userId, "WrongPassword", "AnotherPass");
                    System.out.println("✗ Password validation failed!");
                } catch (UserServiceException e) {
                    System.out.println("✓ Incorrect current password rejected");
                }
                
                // Admin password reset
                userService.resetPassword(userId, "AdminResetPass789");
                System.out.println("✓ Admin password reset successful");
                
                // Reset back
                userService.resetPassword(userId, "SecurePass123");
            }
            
        } catch (UserServiceException e) {
            System.err.println("✗ Password management failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Demonstrates session management.
     */
    private static void demonstrateSessionManagement(AuthenticationService authService) {
        System.out.println("--- 6. Session Management Demo ---");
        
        try {
            // Create multiple sessions
            LoginRequest request1 = new LoginRequest("john_manager", "SecurePass123");
            LoginResponse response1 = authService.login(request1);
            System.out.println("✓ Created session 1");
            
            LoginRequest request2 = new LoginRequest("john_manager", "SecurePass123");
            LoginResponse response2 = authService.login(request2);
            System.out.println("✓ Created session 2");
            
            // Validate both sessions
            boolean valid1 = authService.validateSession(response1.getSessionToken()).isPresent();
            boolean valid2 = authService.validateSession(response2.getSessionToken()).isPresent();
            System.out.println("  Session 1 valid: " + valid1);
            System.out.println("  Session 2 valid: " + valid2);
            
            // Refresh session
            authService.refreshSession(response1.getSessionToken());
            System.out.println("✓ Refreshed session 1");
            
            // Logout one session
            authService.logout(response1.getSessionToken());
            System.out.println("✓ Logged out session 1");
            
            // Verify session 1 is invalid, session 2 is still valid
            valid1 = authService.validateSession(response1.getSessionToken()).isPresent();
            valid2 = authService.validateSession(response2.getSessionToken()).isPresent();
            System.out.println("  Session 1 valid after logout: " + valid1);
            System.out.println("  Session 2 still valid: " + valid2);
            
            // Cleanup
            authService.logout(response2.getSessionToken());
            
            // Cleanup expired sessions
            int cleaned = authService.cleanupExpiredSessions();
            System.out.println("✓ Cleaned up " + cleaned + " expired sessions");
            
        } catch (AuthenticationException e) {
            System.err.println("✗ Session management failed: " + e.getMessage());
        }
        
        System.out.println();
    }
}
