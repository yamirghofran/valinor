package com.valinor.ui.controller;

import com.valinor.domain.enums.UserRole;
import com.valinor.domain.model.Restaurant;
import com.valinor.domain.model.User;
import com.valinor.exception.RepositoryException;
import com.valinor.infrastructure.config.UserConfig;
import com.valinor.infrastructure.security.PasswordHasher;
import com.valinor.repository.RestaurantRepository;
import com.valinor.repository.UserRepository;
import com.valinor.service.restaurant.RestaurantService;
import com.valinor.service.user.UserService;
import com.valinor.service.dto.user.UserCreateRequest;
import com.valinor.ui.RestaurantApp;
import com.valinor.ui.util.DataPaths;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for creating a new restaurant with its admin user.
 */
public class CreateRestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(CreateRestaurantController.class);

    // Restaurant fields
    @FXML
    private TextField restaurantNameField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField contactEmailField;

    @FXML
    private TextField contactPhoneField;

    // Admin user fields
    @FXML
    private TextField adminUsernameField;

    @FXML
    private PasswordField adminPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField adminEmailField;

    @FXML
    private TextField adminFirstNameField;

    @FXML
    private TextField adminLastNameField;

    @FXML
    private TextField adminPhoneField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button createButton;

    private RestaurantService restaurantService;
    private UserService userService;

    @FXML
    public void initialize() {
        try {
            RestaurantRepository restaurantRepository = new RestaurantRepository(DataPaths.RESTAURANTS_CSV);
            UserRepository userRepository = new UserRepository(DataPaths.USERS_CSV);
            
            restaurantService = new RestaurantService(restaurantRepository);
            userService = new UserService(userRepository);
            
            hideError();
            logger.info("Create restaurant controller initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize create restaurant controller", e);
            showError("Unable to load form. Please contact support.");
            disableForm();
        }
    }

    @FXML
    private void handleCreateRestaurant() {
        hideError();

        // Get all field values
        String restaurantName = restaurantNameField.getText().trim();
        String location = locationField.getText().trim();
        String contactEmail = contactEmailField.getText().trim();
        String contactPhone = contactPhoneField.getText().trim();

        String adminUsername = adminUsernameField.getText().trim();
        String adminPassword = adminPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String adminEmail = adminEmailField.getText().trim();
        String adminFirstName = adminFirstNameField.getText().trim();
        String adminLastName = adminLastNameField.getText().trim();
        String adminPhone = adminPhoneField.getText().trim();

        // Validate restaurant fields
        if (restaurantName.isEmpty() || location.isEmpty() || contactEmail.isEmpty() || contactPhone.isEmpty()) {
            showError("Please fill in all restaurant fields.");
            return;
        }

        // Validate admin user fields
        if (adminUsername.isEmpty() || adminPassword.isEmpty() || adminEmail.isEmpty() ||
            adminFirstName.isEmpty() || adminLastName.isEmpty()) {
            showError("Please fill in all required admin user fields.");
            return;
        }

        // Validate passwords match
        if (!adminPassword.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        // Validate field criteria
        String criteriaError = validateFieldCriteria(adminUsername, adminPassword, adminEmail, contactEmail);
        if (criteriaError != null) {
            showError(criteriaError);
            return;
        }

        try {
            createButton.setDisable(true);

            // Step 1: Create the restaurant
            logger.info("Creating restaurant: {}", restaurantName);
            Restaurant restaurant = restaurantService.createRestaurant(
                restaurantName, location, contactEmail, contactPhone
            );
            logger.info("Restaurant created with ID: {}", restaurant.getRestaurantId());

            // Step 2: Create the admin user
            logger.info("Creating admin user: {}", adminUsername);
            UserCreateRequest userRequest = new UserCreateRequest();
            userRequest.setUsername(adminUsername);
            userRequest.setPassword(adminPassword);
            userRequest.setEmail(adminEmail);
            userRequest.setFirstName(adminFirstName);
            userRequest.setLastName(adminLastName);
            userRequest.setPhone(adminPhone.isEmpty() ? null : adminPhone);
            userRequest.setRole(UserRole.RESTAURANT_MANAGER);
            userRequest.setRestaurantId(restaurant.getRestaurantId());

            User adminUser = userService.createUser(userRequest);
            logger.info("Admin user created with ID: {}", adminUser.getUserId());

            // Success! Navigate back to login
            logger.info("Restaurant and admin user created successfully");
            RestaurantApp.switchScene("login.fxml", "Login", 400, 500);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error during restaurant creation", e);
            showError(e.getMessage());
            createButton.setDisable(false);
        } catch (RepositoryException e) {
            logger.error("Repository error during restaurant creation", e);
            showError("Failed to create restaurant: " + e.getMessage());
            createButton.setDisable(false);
        } catch (Exception e) {
            logger.error("Unexpected error during restaurant creation", e);
            showError("An unexpected error occurred. Please try again.");
            createButton.setDisable(false);
        }
    }

    @FXML
    private void handleCancel() {
        logger.info("Restaurant creation cancelled");
        RestaurantApp.switchScene("login.fxml", "Login", 400, 500);
    }

    private String validateFieldCriteria(String username, String password, String adminEmail, String contactEmail) {
        // Validate username
        if (username.length() < UserConfig.USERNAME_MIN_LENGTH) {
            return String.format("Username must be at least %d characters.", UserConfig.USERNAME_MIN_LENGTH);
        }

        if (username.length() > UserConfig.USERNAME_MAX_LENGTH) {
            return String.format("Username must not exceed %d characters.", UserConfig.USERNAME_MAX_LENGTH);
        }

        if (!username.matches(UserConfig.USERNAME_PATTERN)) {
            return "Username can only contain letters, numbers, and underscores.";
        }

        // Validate admin email
        if (!adminEmail.matches(UserConfig.EMAIL_PATTERN)) {
            return "Invalid admin email format.";
        }

        // Validate contact email
        if (!contactEmail.matches(UserConfig.EMAIL_PATTERN)) {
            return "Invalid contact email format.";
        }

        // Validate password
        if (!PasswordHasher.isValidPassword(password)) {
            return PasswordHasher.getPasswordRequirements();
        }

        return null;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void disableForm() {
        if (createButton != null) {
            createButton.setDisable(true);
        }
    }
}
