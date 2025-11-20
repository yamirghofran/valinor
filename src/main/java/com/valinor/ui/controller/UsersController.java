package com.valinor.ui.controller;

import com.valinor.data.entity.User;
import com.valinor.data.entity.UserRole;
import com.valinor.data.repository.UserRepository;
import com.valinor.ui.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for users management view
 */
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Long> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> statusColumn;

    @FXML
    private TableColumn<User, String> createdColumn;

    @FXML
    private TableColumn<User, Void> actionsColumn;

    @FXML
    private ComboBox<String> roleFilterCombo;

    @FXML
    private ComboBox<String> statusFilterCombo;

    private UserRepository userRepository;
    private ObservableList<User> users;
    private List<User> allUsers;

    @FXML
    public void initialize() {
        try {
            userRepository = new UserRepository(com.valinor.ui.util.DataPaths.USERS_CSV);
            users = FXCollections.observableArrayList();

            setupTableColumns();
            loadUsers();
            setupFilters();

            usersTable.setItems(users);

            logger.info("Users controller initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize users controller", e);
        }
    }

    private void setupTableColumns() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        // Setup basic columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Role column - format the enum nicely
        roleColumn.setCellValueFactory(cellData -> {
            UserRole role = cellData.getValue().getRole();
            if (role == null) return new javafx.beans.property.SimpleStringProperty("None");

            String roleName;
            switch (role) {
                case SYSTEM_ADMIN:
                    roleName = "System Admin";
                    break;
                case RESTAURANT_MANAGER:
                    roleName = "Restaurant Manager";
                    break;
                case FRONT_OF_HOUSE_STAFF:
                    roleName = "Front of House Staff";
                    break;
                case CUSTOMER:
                    roleName = "Customer";
                    break;
                default:
                    roleName = "Unknown";
                    break;
            }
            return new javafx.beans.property.SimpleStringProperty(roleName);
        });

        // Status column
        statusColumn.setCellValueFactory(cellData -> {
            Boolean isActive = cellData.getValue().getIsActive();
            String status = (isActive != null && isActive) ? "Active" : "Inactive";
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        // Created date column
        createdColumn.setCellValueFactory(cellData -> {
            LocalDateTime created = cellData.getValue().getCreatedAt();
            String formattedDate = created != null ? created.format(dateFormatter) : "";
            return new javafx.beans.property.SimpleStringProperty(formattedDate);
        });

        // Setup actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏ Edit");
            private final Button toggleButton = new Button("⚡ Toggle");
            private final Button deleteButton = new Button("🗑 Delete");
            private final HBox buttons = new HBox(5, editButton, toggleButton, deleteButton);

            {
                buttons.setAlignment(Pos.CENTER);
                editButton.getStyleClass().add("button-edit");
                toggleButton.getStyleClass().add("button-action");
                deleteButton.getStyleClass().add("button-delete");

                // Set minimum widths to prevent text cutoff
                editButton.setMinWidth(70);
                toggleButton.setMinWidth(85);
                deleteButton.setMinWidth(80);

                // Add tooltips
                editButton.setTooltip(new Tooltip("Edit user details"));
                toggleButton.setTooltip(new Tooltip("Toggle active/inactive status"));
                deleteButton.setTooltip(new Tooltip("Delete user"));

                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleEditUser(user);
                });

                toggleButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleToggleUser(user);
                });

                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadUsers() {
        try {
            Long restaurantId = SessionManager.getInstance().getRestaurantId();
            if (restaurantId != null) {
                allUsers = userRepository.findAll().stream()
                        .filter(u -> restaurantId.equals(u.getRestaurantId()))
                        .collect(Collectors.toList());
                applyFilters();
            }
        } catch (Exception e) {
            logger.error("Failed to load users", e);
        }
    }

    private void setupFilters() {
        roleFilterCombo.setItems(FXCollections.observableArrayList(
                "All Roles", "Restaurant Manager", "Front of House Staff", "Customer", "System Admin"
        ));
        roleFilterCombo.setValue("All Roles");

        statusFilterCombo.setItems(FXCollections.observableArrayList(
                "All", "Active", "Inactive"
        ));
        statusFilterCombo.setValue("All");

        // Connect filter change events
        roleFilterCombo.setOnAction(event -> applyFilters());
        statusFilterCombo.setOnAction(event -> applyFilters());
    }

    private void applyFilters() {
        if (allUsers == null) {
            return;
        }

        List<User> filtered = allUsers.stream()
                .filter(u -> {
                    // Role filter
                    String roleFilter = roleFilterCombo.getValue();
                    if (roleFilter != null && !"All Roles".equals(roleFilter)) {
                        UserRole userRole = u.getRole();
                        if (userRole == null) return false;

                        String roleString;
                        switch (userRole) {
                            case SYSTEM_ADMIN:
                                roleString = "System Admin";
                                break;
                            case RESTAURANT_MANAGER:
                                roleString = "Restaurant Manager";
                                break;
                            case FRONT_OF_HOUSE_STAFF:
                                roleString = "Front of House Staff";
                                break;
                            case CUSTOMER:
                                roleString = "Customer";
                                break;
                            default:
                                roleString = "Unknown";
                                break;
                        }

                        if (!roleString.equals(roleFilter)) {
                            return false;
                        }
                    }

                    // Status filter
                    String statusFilter = statusFilterCombo.getValue();
                    if (statusFilter != null && !"All".equals(statusFilter)) {
                        Boolean isActive = u.getIsActive();
                        if ("Active".equals(statusFilter) && (isActive == null || !isActive)) {
                            return false;
                        }
                        if ("Inactive".equals(statusFilter) && (isActive != null && isActive)) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        users.setAll(filtered);
    }

    @FXML
    private void handleAddUser() {
        Dialog<User> dialog = createUserDialog(null);
        Optional<User> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                User newUser = result.get();
                // Note: Password would need to be hashed with BCrypt in production
                // For now, we'll use a placeholder password hash
                newUser.setPasswordHash("$2a$12$placeholder"); // Placeholder
                userRepository.save(newUser);
                loadUsers();
                logger.info("User created: {}", newUser.getUsername());
            } catch (Exception e) {
                logger.error("Failed to create user", e);
                showAlert("Error", "Failed to create user: " + e.getMessage());
            }
        }
    }

    private void handleEditUser(User user) {
        Dialog<User> dialog = createUserDialog(user);
        Optional<User> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                User updatedUser = result.get();
                userRepository.update(updatedUser);
                loadUsers();
                logger.info("User updated: {}", updatedUser.getUsername());
            } catch (Exception e) {
                logger.error("Failed to update user", e);
                showAlert("Error", "Failed to update user: " + e.getMessage());
            }
        }
    }

    private Dialog<User> createUserDialog(User existingUser) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(existingUser == null ? "Add User" : "Edit User");
        dialog.setHeaderText(existingUser == null ? "Create New User" : "Edit User");

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form fields
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setDisable(existingUser != null); // Can't change username
        if (existingUser != null) {
            usernameField.setText(existingUser.getUsername());
        }

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        if (existingUser != null) {
            emailField.setText(existingUser.getEmail());
        }

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        if (existingUser != null) {
            firstNameField.setText(existingUser.getFirstName());
        }

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        if (existingUser != null) {
            lastNameField.setText(existingUser.getLastName());
        }

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone (optional)");
        if (existingUser != null && existingUser.getPhone() != null) {
            phoneField.setText(existingUser.getPhone());
        }

        ComboBox<UserRole> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(
                UserRole.RESTAURANT_MANAGER,
                UserRole.FRONT_OF_HOUSE_STAFF,
                UserRole.CUSTOMER
        ));
        roleCombo.setConverter(new javafx.util.StringConverter<UserRole>() {
            @Override
            public String toString(UserRole role) {
                if (role == null) return "";
                switch (role) {
                    case SYSTEM_ADMIN:
                        return "System Admin";
                    case RESTAURANT_MANAGER:
                        return "Restaurant Manager";
                    case FRONT_OF_HOUSE_STAFF:
                        return "Front of House Staff";
                    case CUSTOMER:
                        return "Customer";
                    default:
                        return role.name();
                }
            }

            @Override
            public UserRole fromString(String string) {
                return null;
            }
        });
        if (existingUser != null) {
            roleCombo.setValue(existingUser.getRole());
        } else {
            roleCombo.setValue(UserRole.FRONT_OF_HOUSE_STAFF);
        }

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("First Name:"), 0, 2);
        grid.add(firstNameField, 1, 2);
        grid.add(new Label("Last Name:"), 0, 3);
        grid.add(lastNameField, 1, 3);
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Role:"), 0, 5);
        grid.add(roleCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Enable/disable save button based on validation
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Validation
        Runnable validateForm = () -> {
            boolean isValid = !usernameField.getText().trim().isEmpty()
                    && !emailField.getText().trim().isEmpty()
                    && !firstNameField.getText().trim().isEmpty()
                    && !lastNameField.getText().trim().isEmpty()
                    && roleCombo.getValue() != null;
            saveButton.setDisable(!isValid);
        };

        usernameField.textProperty().addListener((obs, old, newVal) -> validateForm.run());
        emailField.textProperty().addListener((obs, old, newVal) -> validateForm.run());
        firstNameField.textProperty().addListener((obs, old, newVal) -> validateForm.run());
        lastNameField.textProperty().addListener((obs, old, newVal) -> validateForm.run());
        roleCombo.valueProperty().addListener((obs, old, newVal) -> validateForm.run());

        // Convert result to User when save is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                User user = existingUser != null ? existingUser : new User();
                user.setUsername(usernameField.getText().trim());
                user.setEmail(emailField.getText().trim());
                user.setFirstName(firstNameField.getText().trim());
                user.setLastName(lastNameField.getText().trim());
                user.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
                user.setRole(roleCombo.getValue());

                // If new user, set restaurant ID from session and default active status
                if (existingUser == null) {
                    user.setRestaurantId(SessionManager.getInstance().getRestaurantId());
                    user.setIsActive(true);
                }

                return user;
            }
            return null;
        });

        return dialog;
    }

    private void handleToggleUser(User user) {
        try {
            Boolean currentStatus = user.getIsActive();
            boolean newStatus = (currentStatus == null || !currentStatus);
            user.setIsActive(newStatus);
            userRepository.update(user);
            loadUsers();
            String action = newStatus ? "activated" : "deactivated";
            logger.info("{} user: {}", action, user.getUsername());
        } catch (Exception e) {
            logger.error("Failed to toggle user status", e);
            showAlert("Error", "Failed to update user status: " + e.getMessage());
        }
    }

    private void handleDeleteUser(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Are you sure you want to delete this user?");
        confirm.setContentText(user.getUsername() + " (" + user.getEmail() + ")");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userRepository.deleteById(user.getUserId());
                loadUsers();
                logger.info("Deleted user: {}", user.getUsername());
            } catch (Exception e) {
                logger.error("Failed to delete user", e);
                showAlert("Error", "Failed to delete user: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClearFilters() {
        roleFilterCombo.setValue("All Roles");
        statusFilterCombo.setValue("All");
        applyFilters();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
