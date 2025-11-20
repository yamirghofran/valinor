package com.valinor.ui.controller;

import com.valinor.data.entity.Reservation;
import com.valinor.data.repository.ReservationRepository;
import com.valinor.ui.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for reservations management view
 */
public class ReservationsController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationsController.class);

    @FXML
    private TableView<Reservation> reservationsTable;

    @FXML
    private TableColumn<Reservation, ?> idColumn;

    @FXML
    private TableColumn<Reservation, ?> customerColumn;

    @FXML
    private TableColumn<Reservation, ?> dateColumn;

    @FXML
    private TableColumn<Reservation, ?> timeColumn;

    @FXML
    private TableColumn<Reservation, ?> tableSectionColumn;

    @FXML
    private TableColumn<Reservation, ?> partySizeColumn;

    @FXML
    private TableColumn<Reservation, ?> statusColumn;

    @FXML
    private TableColumn<Reservation, ?> notesColumn;

    @FXML
    private TableColumn<Reservation, Void> actionsColumn;

    @FXML
    private ComboBox<String> statusFilterCombo;

    @FXML
    private DatePicker datePicker;

    private ReservationRepository reservationRepository;
    private ObservableList<Reservation> reservations;

    @FXML
    public void initialize() {
        try {
            reservationRepository = new ReservationRepository(com.valinor.ui.util.DataPaths.RESERVATIONS_CSV);
            reservations = FXCollections.observableArrayList();

            loadReservations();
            setupFilters();

            reservationsTable.setItems(reservations);

            logger.info("Reservations controller initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize reservations controller", e);
        }
    }

    private void loadReservations() {
        try {
            Long restaurantId = SessionManager.getInstance().getRestaurantId();
            if (restaurantId != null) {
                List<Reservation> reservationList = reservationRepository.findAll().stream()
                        .filter(r -> restaurantId.equals(r.getRestaurantId()))
                        .collect(Collectors.toList());
                reservations.setAll(reservationList);
            }
        } catch (Exception e) {
            logger.error("Failed to load reservations", e);
        }
    }

    private void setupFilters() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(
                "All", "Confirmed", "Pending", "Cancelled", "Completed"
        ));
        statusFilterCombo.setValue("All");
    }

    @FXML
    private void handleNewReservation() {
        showAlert("New Reservation", "Reservation creation dialog will be implemented.");
    }

    @FXML
    private void handleClearFilters() {
        statusFilterCombo.setValue("All");
        datePicker.setValue(null);
        loadReservations();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
