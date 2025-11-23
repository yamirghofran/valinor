package com.valinor.ui.controller;

import com.valinor.domain.model.Customer;
import com.valinor.repository.CustomerRepository;
import com.valinor.repository.ReservationRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for customers management view
 */
public class CustomersController {

    private static final Logger logger = LoggerFactory.getLogger(CustomersController.class);

    @FXML
    private TableView<Customer> customersTable;

    @FXML
    private TableColumn<Customer, Long> idColumn;

    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    private TableColumn<Customer, String> emailColumn;

    @FXML
    private TableColumn<Customer, String> phoneColumn;

    @FXML
    private TableColumn<Customer, Integer> reservationCountColumn;

    @FXML
    private TableColumn<Customer, Void> actionsColumn;

    @FXML
    private TextField searchField;

    private CustomerRepository customerRepository;
    private ReservationRepository reservationRepository;
    private ObservableList<Customer> customers;

    @FXML
    public void initialize() {
        try {
            customerRepository = new CustomerRepository(com.valinor.ui.util.DataPaths.CUSTOMERS_CSV);
            reservationRepository = new ReservationRepository(com.valinor.ui.util.DataPaths.RESERVATIONS_CSV);
            customers = FXCollections.observableArrayList();

            setupTableColumns();
            loadCustomers();

            customersTable.setItems(customers);

            // Setup search
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterCustomers(newVal));

            logger.info("Customers controller initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize customers controller", e);
        }
    }

    private void setupTableColumns() {
        // Setup basic columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Setup reservation count column
        reservationCountColumn.setCellValueFactory(cellData -> {
            try {
                long count = reservationRepository.findAll().stream()
                    .filter(r -> r.getCustomerId().equals(cellData.getValue().getCustomerId()))
                    .count();
                return new javafx.beans.property.SimpleObjectProperty<>((int) count);
            } catch (Exception e) {
                logger.error("Failed to count reservations", e);
                return new javafx.beans.property.SimpleObjectProperty<>(0);
            }
        });

        // Setup actions column with edit and delete buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏ Edit");
            private final Button deleteButton = new Button("🗑 Delete");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                buttons.setAlignment(Pos.CENTER);
                editButton.getStyleClass().add("button-edit");
                deleteButton.getStyleClass().add("button-delete");

                // Set minimum widths to prevent text cutoff
                editButton.setMinWidth(70);
                deleteButton.setMinWidth(80);

                // Add tooltips
                editButton.setTooltip(new Tooltip("Edit customer details"));
                deleteButton.setTooltip(new Tooltip("Delete customer"));

                editButton.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    handleEditCustomer(customer);
                });

                deleteButton.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    handleDeleteCustomer(customer);
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

    private void loadCustomers() {
        try {
            Long restaurantId = SessionManager.getInstance().getRestaurantId();
            if (restaurantId != null) {
                List<Customer> customerList = customerRepository.findAll().stream()
                    .filter(c -> restaurantId.equals(c.getRestaurantId()))
                    .collect(Collectors.toList());
                customers.setAll(customerList);
            }
        } catch (Exception e) {
            logger.error("Failed to load customers", e);
        }
    }

    private void filterCustomers(String searchText) {
        try {
            Long restaurantId = SessionManager.getInstance().getRestaurantId();
            if (restaurantId == null) {
                return;
            }
            
            if (searchText == null || searchText.trim().isEmpty()) {
                loadCustomers();
                return;
            }

            String lowerSearch = searchText.toLowerCase();
            List<Customer> filtered = customerRepository.findAll().stream()
                    .filter(c -> restaurantId.equals(c.getRestaurantId()))
                    .filter(c -> {
                        String fullName = (c.getFirstName() + " " + c.getLastName()).toLowerCase();
                        return fullName.contains(lowerSearch) ||
                                (c.getEmail() != null && c.getEmail().toLowerCase().contains(lowerSearch)) ||
                                (c.getPhone() != null && c.getPhone().contains(searchText));
                    })
                    .collect(Collectors.toList());
            customers.setAll(filtered);
        } catch (Exception e) {
            logger.error("Failed to filter customers", e);
        }
    }

    @FXML
    private void handleAddCustomer() {
        Dialog<Customer> dialog = createCustomerDialog(null);
        Optional<Customer> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                Customer newCustomer = result.get();
                newCustomer.setRestaurantId(SessionManager.getInstance().getRestaurantId());
                customerRepository.save(newCustomer);
                loadCustomers();
                logger.info("Customer created: {}", newCustomer.getEmail());
            } catch (Exception e) {
                logger.error("Failed to create customer", e);
                showAlert("Error", "Failed to create customer: " + e.getMessage());
            }
        }
    }

    private void handleEditCustomer(Customer customer) {
        Dialog<Customer> dialog = createCustomerDialog(customer);
        Optional<Customer> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                Customer updatedCustomer = result.get();
                customerRepository.update(updatedCustomer);
                loadCustomers();
                logger.info("Customer updated: {}", updatedCustomer.getEmail());
            } catch (Exception e) {
                logger.error("Failed to update customer", e);
                showAlert("Error", "Failed to update customer: " + e.getMessage());
            }
        }
    }

    private Dialog<Customer> createCustomerDialog(Customer existingCustomer) {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle(existingCustomer == null ? "Add Customer" : "Edit Customer");
        dialog.setHeaderText(existingCustomer == null ? "Create New Customer" : "Edit Customer");

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form fields
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        if (existingCustomer != null) {
            firstNameField.setText(existingCustomer.getFirstName());
        }

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        if (existingCustomer != null) {
            lastNameField.setText(existingCustomer.getLastName());
        }

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        if (existingCustomer != null) {
            emailField.setText(existingCustomer.getEmail());
        }

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        if (existingCustomer != null) {
            phoneField.setText(existingCustomer.getPhone());
        }

        TextField allergiesField = new TextField();
        allergiesField.setPromptText("Allergies (optional)");
        if (existingCustomer != null && existingCustomer.getAllergies() != null) {
            allergiesField.setText(existingCustomer.getAllergies());
        }

        TextArea notesField = new TextArea();
        notesField.setPromptText("Notes (optional)");
        notesField.setPrefRowCount(3);
        if (existingCustomer != null && existingCustomer.getNotes() != null) {
            notesField.setText(existingCustomer.getNotes());
        }

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Allergies:"), 0, 4);
        grid.add(allergiesField, 1, 4);
        grid.add(new Label("Notes:"), 0, 5);
        grid.add(notesField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Enable/disable save button based on validation
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Validation - require first name, last name, email, and phone
        Runnable validateForm = () -> {
            boolean isValid = !firstNameField.getText().trim().isEmpty()
                    && !lastNameField.getText().trim().isEmpty()
                    && !emailField.getText().trim().isEmpty()
                    && !phoneField.getText().trim().isEmpty();
            saveButton.setDisable(!isValid);
        };

        firstNameField.textProperty().addListener((obs, old, newVal) -> validateForm.run());
        lastNameField.textProperty().addListener((obs, old, newVal) -> validateForm.run());
        emailField.textProperty().addListener((obs, old, newVal) -> validateForm.run());
        phoneField.textProperty().addListener((obs, old, newVal) -> validateForm.run());

        // Convert result to Customer when save is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Customer customer = existingCustomer != null ? existingCustomer : new Customer();
                customer.setFirstName(firstNameField.getText().trim());
                customer.setLastName(lastNameField.getText().trim());
                customer.setEmail(emailField.getText().trim());
                customer.setPhone(phoneField.getText().trim());
                customer.setAllergies(allergiesField.getText().trim().isEmpty() ? null : allergiesField.getText().trim());
                customer.setNotes(notesField.getText().trim().isEmpty() ? null : notesField.getText().trim());
                return customer;
            }
            return null;
        });

        return dialog;
    }

    private void handleDeleteCustomer(Customer customer) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Customer");
        confirm.setHeaderText("Are you sure you want to delete this customer?");
        confirm.setContentText(customer.getFullName() + " (" + customer.getEmail() + ")");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                customerRepository.deleteById(customer.getCustomerId());
                loadCustomers();
                logger.info("Deleted customer: {}", customer.getCustomerId());
            } catch (Exception e) {
                logger.error("Failed to delete customer", e);
                showAlert("Error", "Failed to delete customer: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
