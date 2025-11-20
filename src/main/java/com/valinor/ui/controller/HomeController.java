package com.valinor.ui.controller;

import com.valinor.data.entity.Table;
import com.valinor.data.entity.Customer;
import com.valinor.data.entity.Reservation;
import com.valinor.data.entity.User;
import com.valinor.data.repository.TableRepository;
import com.valinor.data.repository.CustomerRepository;
import com.valinor.data.repository.ReservationRepository;
import com.valinor.data.repository.UserRepository;
import com.valinor.ui.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the home dashboard view
 */
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @FXML
    private Label totalTablesLabel;

    @FXML
    private Label activeReservationsLabel;

    @FXML
    private Label totalCustomersLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private TableView<?> recentActivityTable;

    @FXML
    private TableColumn<?, ?> timeColumn;

    @FXML
    private TableColumn<?, ?> activityColumn;

    @FXML
    private TableColumn<?, ?> userColumn;

    @FXML
    private TableColumn<?, ?> statusColumn;

    private TableRepository tableRepository;
    private CustomerRepository customerRepository;
    private ReservationRepository reservationRepository;
    private UserRepository userRepository;

    @FXML
    public void initialize() {
        try {
            // Initialize repositories
            tableRepository = new TableRepository(com.valinor.ui.util.DataPaths.TABLES_CSV);
            customerRepository = new CustomerRepository(com.valinor.ui.util.DataPaths.CUSTOMERS_CSV);
            reservationRepository = new ReservationRepository(com.valinor.ui.util.DataPaths.RESERVATIONS_CSV);
            userRepository = new UserRepository(com.valinor.ui.util.DataPaths.USERS_CSV);

            // Load statistics
            loadStatistics();

            logger.info("Home controller initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize home controller", e);
        }
    }

    private void loadStatistics() {
        try {
            Long restaurantId = SessionManager.getInstance().getRestaurantId();

            if (restaurantId != null) {
                // Count tables for this restaurant
                long tableCount = tableRepository.findAll().stream()
                        .filter(table -> table.getSectionId() != null)
                        .count();
                totalTablesLabel.setText(String.valueOf(tableCount));

                // Count active reservations
                long reservationCount = reservationRepository.findAll().stream()
                        .filter(reservation -> restaurantId.equals(reservation.getRestaurantId()))
                        .count();
                activeReservationsLabel.setText(String.valueOf(reservationCount));

                // Count customers
                long customerCount = customerRepository.findAll().size();
                totalCustomersLabel.setText(String.valueOf(customerCount));

                // Count users
                long userCount = userRepository.findAll().stream()
                        .filter(user -> restaurantId.equals(user.getRestaurantId()))
                        .count();
                totalUsersLabel.setText(String.valueOf(userCount));

                logger.debug("Statistics loaded: tables={}, reservations={}, customers={}, users={}",
                        tableCount, reservationCount, customerCount, userCount);
            }
        } catch (Exception e) {
            logger.error("Failed to load statistics", e);
        }
    }

    @FXML
    private void handleNewReservation() {
        // Navigate to reservations view
        logger.info("Navigate to new reservation");
        navigateTo("reservations");
    }

    @FXML
    private void handleAddCustomer() {
        // Navigate to customers view
        logger.info("Navigate to add customer");
        navigateTo("customers");
    }

    @FXML
    private void handleManageTables() {
        // Navigate to restaurant layout view
        logger.info("Navigate to manage tables");
        navigateTo("layout");
    }

    private void navigateTo(String view) {
        try {
            // Get the DashboardController through the scene
            javafx.scene.Node node = totalTablesLabel; // Any FXML-injected node will work
            while (node.getParent() != null) {
                node = node.getParent();
                if (node instanceof javafx.scene.layout.StackPane) {
                    // This is likely the contentArea in dashboard
                    javafx.scene.Parent root = node.getParent();
                    if (root != null) {
                        // Trigger navigation by simulating button clicks or finding buttons
                        findAndClickButton(root, view);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to navigate to view: " + view, e);
        }
    }

    private void findAndClickButton(javafx.scene.Parent parent, String view) {
        // Find the navigation buttons in the dashboard
        for (javafx.scene.Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof javafx.scene.control.Button) {
                javafx.scene.control.Button button = (javafx.scene.control.Button) node;
                String buttonId = button.getId();
                if (buttonId != null) {
                    if ((view.equals("reservations") && buttonId.equals("reservationsButton")) ||
                        (view.equals("customers") && buttonId.equals("customersButton")) ||
                        (view.equals("layout") && buttonId.equals("layoutButton"))) {
                        button.fire();
                        return;
                    }
                }
            }
            if (node instanceof javafx.scene.Parent) {
                findAndClickButton((javafx.scene.Parent) node, view);
            }
        }
    }
}
