package com.valinor.ui.controller;

import com.valinor.service.dto.user.LoginRequest;
import com.valinor.service.dto.user.LoginResponse;
import com.valinor.service.dto.user.UserResponse;
import com.valinor.domain.model.UserSession;
import com.valinor.exception.AuthenticationException;
import com.valinor.repository.UserRepository;
import com.valinor.repository.UserSessionRepository;
import com.valinor.service.user.AuthenticationService;
import com.valinor.ui.RestaurantApp;
import com.valinor.ui.util.DataPaths;
import com.valinor.ui.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    
    @FXML
    private Label statusLabel;

    private AuthenticationService authService;

    @FXML
    public void initialize() {
        if (!initializeAuthService("System initialization error. Please contact support.")) {
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
        hideStatus();
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

    @FXML
    private void handleShowSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup.fxml"));
            Parent root = loader.load();

            SignUpController controller = loader.getController();
            controller.setOnSignUpSuccess(username -> {
                usernameField.setText(username);
                passwordField.clear();
                passwordField.requestFocus();
                if (initializeAuthService("Account created but failed to refresh users. Please restart the application.")) {
                    showStatus("Account created successfully. Please sign in.");
                }
            });

            Stage dialog = new Stage();
            dialog.initOwner(RestaurantApp.getPrimaryStage());
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setTitle("Create Account");
            Scene scene = new Scene(root, 420, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();

        } catch (Exception e) {
            logger.error("Failed to open sign up dialog", e);
            showError("Unable to open sign up form. Please try again.");
        }
    }

    private void showError(String message) {
        hideStatus();
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void showStatus(String message) {
        hideError();
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void hideStatus() {
        if (statusLabel != null) {
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);
        }
    }
    
    private boolean initializeAuthService(String failureMessage) {
        try {
            UserRepository userRepository = new UserRepository(DataPaths.USERS_CSV);
            UserSessionRepository sessionRepository = new UserSessionRepository(DataPaths.SESSIONS_CSV);
            authService = new AuthenticationService(userRepository, sessionRepository);
            if (loginButton != null) {
                loginButton.setDisable(false);
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to initialize authentication service", e);
            if (failureMessage != null && !failureMessage.isEmpty()) {
                showError(failureMessage);
            }
            if (loginButton != null) {
                loginButton.setDisable(true);
            }
            return false;
        }
    }
}
