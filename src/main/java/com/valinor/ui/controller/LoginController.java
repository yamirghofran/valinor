package com.valinor.ui.controller;

import com.valinor.data.dto.LoginRequest;
import com.valinor.data.dto.LoginResponse;
import com.valinor.data.dto.UserResponse;
import com.valinor.data.entity.UserSession;
import com.valinor.data.exception.AuthenticationException;
import com.valinor.data.repository.UserRepository;
import com.valinor.data.repository.UserSessionRepository;
import com.valinor.data.service.AuthenticationService;
import com.valinor.ui.RestaurantApp;
import com.valinor.ui.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the login view
 */
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    private AuthenticationService authService;

    @FXML
    public void initialize() {
        try {
            // Initialize services
            UserRepository userRepository = new UserRepository(com.valinor.ui.util.DataPaths.USERS_CSV);
            UserSessionRepository sessionRepository = new UserSessionRepository(com.valinor.ui.util.DataPaths.SESSIONS_CSV);
            authService = new AuthenticationService(userRepository, sessionRepository);
        } catch (Exception e) {
            logger.error("Failed to initialize login controller", e);
            showError("System initialization error. Please contact support.");
            return;
        }

        // Add enter key handler for password field
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });

        // Add enter key handler for username field
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        logger.info("Login controller initialized");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        try {
            // Disable login button during authentication
            loginButton.setDisable(true);
            hideError();

            // Authenticate user
            LoginRequest request = new LoginRequest(username, password);
            LoginResponse response = authService.login(request);

            // Store session
            UserSession session = new UserSession();
            session.setSessionToken(response.getSessionToken());
            UserResponse user = response.getUser();

            SessionManager.getInstance().setSession(session, user);

            logger.info("User {} logged in successfully", username);

            // Navigate to dashboard
            RestaurantApp.switchScene("dashboard.fxml", "Dashboard", 1200, 800);

        } catch (AuthenticationException e) {
            logger.warn("Login failed for user {}: {}", username, e.getMessage());
            showError(e.getMessage());
            loginButton.setDisable(false);

        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            showError("An unexpected error occurred. Please try again.");
            loginButton.setDisable(false);
        }
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
}
