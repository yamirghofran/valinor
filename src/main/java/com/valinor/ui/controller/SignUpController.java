package com.valinor.ui.controller;

import com.valinor.domain.enums.UserRole;
import com.valinor.domain.model.User;
import com.valinor.exception.DuplicateUserException;
import com.valinor.exception.UserServiceException;
import com.valinor.infrastructure.config.UserConfig;
import com.valinor.infrastructure.security.PasswordHasher;
import com.valinor.repository.UserRepository;
import com.valinor.service.dto.user.UserCreateRequest;
import com.valinor.service.user.UserService;
import com.valinor.ui.util.DataPaths;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Controller for the sign up (user registration) dialog.
 */
public class SignUpController {

    private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<UserRole> roleComboBox;

    @FXML
    private TextField restaurantIdField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button createAccountButton;

    private UserService userService;
    private Consumer<String> onSignUpSuccess;

    @FXML
    public void initialize() {
        try {
            UserRepository userRepository = new UserRepository(DataPaths.USERS_CSV);
            userService = new UserService(userRepository);
        } catch (Exception e) {
            logger.error("Failed to initialize sign up controller", e);
            showError("Unable to load sign up form. Please contact support.");
            disableForm();
            return;
        }

        configureRoleComboBox();
        hideError();
    }

    private void configureRoleComboBox() {
        roleComboBox.getItems().setAll(UserRole.values());
        roleComboBox.setCellFactory(listView -> new ListCell<UserRole>() {
            @Override
            protected void updateItem(UserRole role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                } else {
                    setText(role.getDisplayName());
                }
            }
        });
        roleComboBox.setButtonCell(new ListCell<UserRole>() {
            @Override
            protected void updateItem(UserRole role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                } else {
                    setText(role.getDisplayName());
                }
            }
        });
        roleComboBox.getSelectionModel().select(UserRole.FRONT_OF_HOUSE_STAFF);
    }

    private void disableForm() {
        if (createAccountButton != null) {
            createAccountButton.setDisable(true);
        }
    }

    @FXML
    private void handleSignUp() {
        hideError();

        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        UserRole role = roleComboBox.getValue();
        Long restaurantId = parseRestaurantId();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() ||
            firstName.isEmpty() || lastName.isEmpty()) {
            showError("Please fill in all required fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        if (role == null) {
            showError("Please select a role.");
            return;
        }
        
        if (restaurantId == null && !restaurantIdField.getText().trim().isEmpty()) {
            // parseRestaurantId already showed error
            return;
        }
        
        String criteriaError = validateFieldCriteria(username, password, email);
        if (criteriaError != null) {
            showError(criteriaError);
            return;
        }

        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPhone(phone.isEmpty() ? null : phone);
        request.setRole(role);
        request.setRestaurantId(restaurantId);

        try {
            createAccountButton.setDisable(true);
            User createdUser = userService.createUser(request);
            logger.info("Created user {} via sign up dialog", createdUser.getUsername());

            if (onSignUpSuccess != null) {
                onSignUpSuccess.accept(createdUser.getUsername());
            }
            closeDialog();

        } catch (DuplicateUserException e) {
            showError(e.getMessage());
        } catch (UserServiceException e) {
            showError(resolveUserServiceMessage(e));
        } catch (Exception e) {
            logger.error("Unexpected error while signing up", e);
            showError("An unexpected error occurred. Please try again.");
        } finally {
            createAccountButton.setDisable(false);
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private Long parseRestaurantId() {
        String value = restaurantIdField.getText().trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            showError("Restaurant ID must be a number.");
            return null;
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) createAccountButton.getScene().getWindow();
        stage.close();
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

    /**
     * Sets the callback invoked after successful sign up.
     * @param callback consumer receiving the created username
     */
    public void setOnSignUpSuccess(Consumer<String> callback) {
        this.onSignUpSuccess = callback;
    }
    
    private String validateFieldCriteria(String username, String password, String email) {
        if (username.length() < UserConfig.USERNAME_MIN_LENGTH) {
            return String.format("Username must be at least %d characters.", UserConfig.USERNAME_MIN_LENGTH);
        }
        
        if (username.length() > UserConfig.USERNAME_MAX_LENGTH) {
            return String.format("Username must not exceed %d characters.", UserConfig.USERNAME_MAX_LENGTH);
        }
        
        if (!username.matches(UserConfig.USERNAME_PATTERN)) {
            return "Username can only contain letters, numbers, and underscores.";
        }
        
        if (!email.matches(UserConfig.EMAIL_PATTERN)) {
            return "Invalid email format.";
        }
        
        if (!PasswordHasher.isValidPassword(password)) {
            return PasswordHasher.getPasswordRequirements();
        }
        
        return null;
    }
    
    private String resolveUserServiceMessage(UserServiceException exception) {
        String message = exception.getMessage();
        if ((message == null || message.trim().isEmpty() || "Failed to create user".equalsIgnoreCase(message))
                && exception.getCause() != null && exception.getCause().getMessage() != null) {
            message = exception.getCause().getMessage();
        }
        
        if (message == null || message.trim().isEmpty()) {
            message = "Failed to create user. Please verify your inputs.";
        }
        
        return message;
    }
}
