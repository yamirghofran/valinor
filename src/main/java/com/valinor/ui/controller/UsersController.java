package com.valinor.ui.controller;

import com.valinor.data.entity.User;
import com.valinor.data.entity.UserRole;
import com.valinor.data.repository.UserRepository;
import com.valinor.ui.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for users management view
 */
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, ?> idColumn;

    @FXML
    private TableColumn<User, ?> usernameColumn;

    @FXML
    private TableColumn<User, ?> emailColumn;

    @FXML
    private TableColumn<User, ?> roleColumn;

    @FXML
    private TableColumn<User, ?> statusColumn;

    @FXML
    private TableColumn<User, ?> createdColumn;

    @FXML
    private TableColumn<User, Void> actionsColumn;

    @FXML
    private ComboBox<String> roleFilterCombo;

    @FXML
    private ComboBox<String> statusFilterCombo;

    private UserRepository userRepository;
    private ObservableList<User> users;

    @FXML
    public void initialize() {
        try {
            userRepository = new UserRepository(com.valinor.ui.util.DataPaths.USERS_CSV);
            users = FXCollections.observableArrayList();

            loadUsers();
            setupFilters();

            usersTable.setItems(users);

            logger.info("Users controller initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize users controller", e);
        }
    }

    private void loadUsers() {
        try {
            Long restaurantId = SessionManager.getInstance().getRestaurantId();
            if (restaurantId != null) {
                List<User> userList = userRepository.findAll().stream()
                        .filter(u -> restaurantId.equals(u.getRestaurantId()))
                        .collect(Collectors.toList());
                users.setAll(userList);
            }
        } catch (Exception e) {
            logger.error("Failed to load users", e);
        }
    }

    private void setupFilters() {
        roleFilterCombo.setItems(FXCollections.observableArrayList(
                "All Roles", "Restaurant Manager", "Front of House Staff"
        ));
        roleFilterCombo.setValue("All Roles");

        statusFilterCombo.setItems(FXCollections.observableArrayList(
                "All", "Active", "Inactive"
        ));
        statusFilterCombo.setValue("All");
    }

    @FXML
    private void handleAddUser() {
        showAlert("Add User", "User creation dialog will be implemented.");
    }

    @FXML
    private void handleClearFilters() {
        roleFilterCombo.setValue("All Roles");
        statusFilterCombo.setValue("All");
        loadUsers();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
