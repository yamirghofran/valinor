package com.valinor.ui.util;

import com.valinor.service.dto.user.UserCreateRequest;
import com.valinor.domain.enums.UserRole;
import com.valinor.repository.UserRepository;
import com.valinor.service.user.UserService;

/**
 * Utility to create a test user
 */
public class CreateTestUser {
    public static void main(String[] args) {
        try {
            UserRepository userRepository = new UserRepository("data/users.csv");
            UserService userService = new UserService(userRepository);

            // Create test user
            UserCreateRequest request = new UserCreateRequest(
                "test_user",
                "Password123",  // This is the password
                "test@valinor.com",
                "Test",
                "User",
                UserRole.RESTAURANT_MANAGER
            );
            request.setRestaurantId(1L);
            request.setPhone("+1-555-9999");

            var user = userService.createUser(request);

            System.out.println("✓ Test user created successfully!");
            System.out.println("Username: test_user");
            System.out.println("Password: Password123");
            System.out.println("Role: RESTAURANT_MANAGER");
            System.out.println("Restaurant ID: 1");

        } catch (Exception e) {
            System.err.println("Failed to create test user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
