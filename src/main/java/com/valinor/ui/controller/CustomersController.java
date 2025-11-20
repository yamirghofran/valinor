package com.valinor.ui.controller;

import com.valinor.data.entity.Customer;
import com.valinor.data.repository.CustomerRepository;
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
 * Controller for customers management view
 */
public class CustomersController {

    private static final Logger logger = LoggerFactory.getLogger(CustomersController.class);

    @FXML
    private TableView<Customer> customersTable;

    @FXML
    private TableColumn<Customer, ?> idColumn;

    @FXML
    private TableColumn<Customer, ?> nameColumn;

    @FXML
    private TableColumn<Customer, ?> emailColumn;

    @FXML
    private TableColumn<Customer, ?> phoneColumn;

    @FXML
    private TableColumn<Customer, ?> reservationCountColumn;

    @FXML
    private TableColumn<Customer, Void> actionsColumn;

    @FXML
    private TextField searchField;

    private CustomerRepository customerRepository;
    private ObservableList<Customer> customers;

    @FXML
    public void initialize() {
        try {
            customerRepository = new CustomerRepository(com.valinor.ui.util.DataPaths.CUSTOMERS_CSV);
            customers = FXCollections.observableArrayList();

            loadCustomers();

            customersTable.setItems(customers);

            // Setup search
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterCustomers(newVal));

            logger.info("Customers controller initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize customers controller", e);
        }
    }

    private void loadCustomers() {
        try {
            List<Customer> customerList = customerRepository.findAll();
            customers.setAll(customerList);
        } catch (Exception e) {
            logger.error("Failed to load customers", e);
        }
    }

    private void filterCustomers(String searchText) {
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                loadCustomers();
                return;
            }

            String lowerSearch = searchText.toLowerCase();
            List<Customer> filtered = customerRepository.findAll().stream()
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
        showAlert("Add Customer", "Customer creation dialog will be implemented.");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
