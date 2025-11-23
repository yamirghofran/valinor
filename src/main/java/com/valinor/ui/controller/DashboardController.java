package com.valinor.ui.controller;

import com.valinor.service.dto.user.UserResponse;
import com.valinor.ui.RestaurantApp;
import com.valinor.ui.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller for the main dashboard view
 */
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML
    private StackPane contentArea;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Button homeButton;

    @FXML
    private Button layoutButton;

    @FXML
    private Button reservationsButton;

    @FXML
    private Button customersButton;

    @FXML
    private Button usersButton;

    private Button activeButton;

    @FXML
    public void initialize() {
        // Load user info
        UserResponse currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            roleLabel.setText(currentUser.getRole().toString());
        }

        // Show home view by default
        showHome();

        logger.info("Dashboard controller initialized");
    }

    @FXML
    private void showHome() {
        loadView("/fxml/views/home.fxml", homeButton);
    }

    @FXML
    private void showRestaurantLayout() {
        loadView("/fxml/views/restaurant_layout.fxml", layoutButton);
    }

    @FXML
    private void showReservations() {
        loadView("/fxml/views/reservations.fxml", reservationsButton);
    }

    @FXML
    private void showCustomers() {
        loadView("/fxml/views/customers.fxml", customersButton);
    }

    @FXML
    private void showUsers() {
        loadView("/fxml/views/users.fxml", usersButton);
    }

    @FXML
    private void handleLogout() {
        logger.info("User {} logging out", SessionManager.getInstance().getCurrentUser().getUsername());

        // Clear session
        SessionManager.getInstance().clearSession();

        // Return to login screen
        RestaurantApp.switchScene("login.fxml", "Login", 400, 500);
    }

    /**
     * Loads a view into the content area
     * @param fxmlPath path to FXML file
     * @param button button that was clicked
     */
    private void loadView(String fxmlPath, Button button) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            // Update active button styling
            if (activeButton != null) {
                activeButton.getStyleClass().remove("menu-button-active");
            }
            button.getStyleClass().add("menu-button-active");
            activeButton = button;

            logger.debug("Loaded view: {}", fxmlPath);

        } catch (IOException e) {
            logger.error("Failed to load view: " + fxmlPath, e);
        }
    }
}
