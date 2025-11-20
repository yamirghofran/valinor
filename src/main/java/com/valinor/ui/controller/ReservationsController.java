package com.valinor.ui.controller;

import com.valinor.data.entity.Customer;
import com.valinor.data.entity.Reservation;
import com.valinor.data.entity.ReservationStatus;
import com.valinor.data.entity.Table;
import com.valinor.data.entity.Section;
import com.valinor.data.repository.CustomerRepository;
import com.valinor.data.repository.ReservationRepository;
import com.valinor.data.repository.TableRepository;
import com.valinor.data.repository.SectionRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for reservations management view
 */
public class ReservationsController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationsController.class);

    @FXML
    private TableView<Reservation> reservationsTable;

    @FXML
    private TableColumn<Reservation, Long> idColumn;

    @FXML
    private TableColumn<Reservation, String> customerColumn;

    @FXML
    private TableColumn<Reservation, String> dateColumn;

    @FXML
    private TableColumn<Reservation, String> timeColumn;

    @FXML
    private TableColumn<Reservation, String> tableSectionColumn;

    @FXML
    private TableColumn<Reservation, Integer> partySizeColumn;

    @FXML
    private TableColumn<Reservation, String> statusColumn;

    @FXML
    private TableColumn<Reservation, String> notesColumn;

    @FXML
    private TableColumn<Reservation, Void> actionsColumn;

    @FXML
    private ComboBox<String> statusFilterCombo;

    @FXML
    private DatePicker datePicker;

    private ReservationRepository reservationRepository;
    private CustomerRepository customerRepository;
    private TableRepository tableRepository;
    private SectionRepository sectionRepository;
    private ObservableList<Reservation> reservations;
    private List<Reservation> allReservations;

    @FXML
    public void initialize() {
        try {
            reservationRepository = new ReservationRepository(com.valinor.ui.util.DataPaths.RESERVATIONS_CSV);
            customerRepository = new CustomerRepository(com.valinor.ui.util.DataPaths.CUSTOMERS_CSV);
            tableRepository = new TableRepository(com.valinor.ui.util.DataPaths.TABLES_CSV);
            sectionRepository = new SectionRepository(com.valinor.ui.util.DataPaths.SECTIONS_CSV);
            reservations = FXCollections.observableArrayList();

            setupTableColumns();
            loadReservations();
            setupFilters();

            reservationsTable.setItems(reservations);

            logger.info("Reservations controller initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize reservations controller", e);
        }
    }

    private void setupTableColumns() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Setup basic columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));

        // Customer name lookup
        customerColumn.setCellValueFactory(cellData -> {
            try {
                Long customerId = cellData.getValue().getCustomerId();
                Optional<Customer> customer = customerRepository.findById(customerId);
                return new javafx.beans.property.SimpleStringProperty(
                    customer.map(Customer::getFullName).orElse("Unknown"));
            } catch (Exception e) {
                logger.error("Failed to load customer", e);
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });

        // Date column
        dateColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getReservationDatetime();
            return new javafx.beans.property.SimpleStringProperty(
                dateTime != null ? dateTime.format(dateFormatter) : "");
        });

        // Time column
        timeColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getReservationDatetime();
            return new javafx.beans.property.SimpleStringProperty(
                dateTime != null ? dateTime.format(timeFormatter) : "");
        });

        // Table and section lookup
        tableSectionColumn.setCellValueFactory(cellData -> {
            try {
                Long tableId = cellData.getValue().getTableId();
                Optional<Table> table = tableRepository.findById(tableId);
                if (table.isPresent()) {
                    Optional<Section> section = sectionRepository.findById(table.get().getSectionId());
                    String tableName = "Table " + table.get().getTableNumber();
                    String sectionName = section.map(Section::getName).orElse("Unknown");
                    return new javafx.beans.property.SimpleStringProperty(tableName + " (" + sectionName + ")");
                }
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            } catch (Exception e) {
                logger.error("Failed to load table/section", e);
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });

        partySizeColumn.setCellValueFactory(new PropertyValueFactory<>("partySize"));

        // Status column
        statusColumn.setCellValueFactory(cellData -> {
            ReservationStatus status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(
                status != null ? status.name() : "UNKNOWN");
        });

        notesColumn.setCellValueFactory(new PropertyValueFactory<>("specialRequests"));

        // Setup actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏ Edit");
            private final Button cancelButton = new Button("✕ Cancel");
            private final HBox buttons = new HBox(5, editButton, cancelButton);

            {
                buttons.setAlignment(Pos.CENTER);
                editButton.getStyleClass().add("button-edit");
                cancelButton.getStyleClass().add("button-delete");

                // Set minimum widths to prevent text cutoff
                editButton.setMinWidth(70);
                cancelButton.setMinWidth(85);

                // Add tooltips
                editButton.setTooltip(new Tooltip("Edit reservation details"));
                cancelButton.setTooltip(new Tooltip("Cancel reservation"));

                editButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleEditReservation(reservation);
                });

                cancelButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleCancelReservation(reservation);
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

    private void loadReservations() {
        try {
            Long restaurantId = SessionManager.getInstance().getRestaurantId();
            if (restaurantId != null) {
                allReservations = reservationRepository.findAll().stream()
                        .filter(r -> restaurantId.equals(r.getRestaurantId()))
                        .collect(Collectors.toList());
                applyFilters();
            }
        } catch (Exception e) {
            logger.error("Failed to load reservations", e);
        }
    }

    private void setupFilters() {
        statusFilterCombo.setItems(FXCollections.observableArrayList(
                "All", "CONFIRMED", "COMPLETED", "CANCELLED", "NO_SHOW"
        ));
        statusFilterCombo.setValue("All");

        // Connect filter change events
        statusFilterCombo.setOnAction(event -> applyFilters());
        datePicker.setOnAction(event -> applyFilters());
    }

    private void applyFilters() {
        if (allReservations == null) {
            return;
        }

        List<Reservation> filtered = allReservations.stream()
                .filter(r -> {
                    // Status filter
                    String statusFilter = statusFilterCombo.getValue();
                    if (statusFilter != null && !"All".equals(statusFilter)) {
                        if (r.getStatus() == null || !r.getStatus().name().equals(statusFilter)) {
                            return false;
                        }
                    }

                    // Date filter
                    LocalDate dateFilter = datePicker.getValue();
                    if (dateFilter != null && r.getReservationDatetime() != null) {
                        if (!r.getReservationDatetime().toLocalDate().equals(dateFilter)) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        reservations.setAll(filtered);
    }

    @FXML
    private void handleNewReservation() {
        Dialog<Reservation> dialog = createReservationDialog(null);
        Optional<Reservation> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                Reservation newReservation = result.get();
                newReservation.setRestaurantId(SessionManager.getInstance().getRestaurantId());
                reservationRepository.save(newReservation);
                loadReservations();
                logger.info("Reservation created for customer: {}", newReservation.getCustomerId());
            } catch (Exception e) {
                logger.error("Failed to create reservation", e);
                showAlert("Error", "Failed to create reservation: " + e.getMessage());
            }
        }
    }

    private void handleEditReservation(Reservation reservation) {
        Dialog<Reservation> dialog = createReservationDialog(reservation);
        Optional<Reservation> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                Reservation updatedReservation = result.get();
                reservationRepository.update(updatedReservation);
                loadReservations();
                logger.info("Reservation updated: {}", updatedReservation.getReservationId());
            } catch (Exception e) {
                logger.error("Failed to update reservation", e);
                showAlert("Error", "Failed to update reservation: " + e.getMessage());
            }
        }
    }

    private Dialog<Reservation> createReservationDialog(Reservation existingReservation) {
        Dialog<Reservation> dialog = new Dialog<>();
        dialog.setTitle(existingReservation == null ? "New Reservation" : "Edit Reservation");
        dialog.setHeaderText(existingReservation == null ? "Create New Reservation" : "Edit Reservation");

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form fields
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        // Load customers
        List<Customer> customersList;
        try {
            customersList = customerRepository.findAll();
        } catch (Exception e) {
            logger.error("Failed to load customers", e);
            customersList = new java.util.ArrayList<>();
        }

        ComboBox<Customer> customerCombo = new ComboBox<>();
        customerCombo.setItems(FXCollections.observableArrayList(customersList));
        customerCombo.setConverter(new javafx.util.StringConverter<Customer>() {
            @Override
            public String toString(Customer customer) {
                return customer == null ? "" : customer.getFullName() + " (" + customer.getEmail() + ")";
            }

            @Override
            public Customer fromString(String string) {
                return null;
            }
        });
        if (existingReservation != null) {
            customersList.stream()
                    .filter(c -> c.getCustomerId().equals(existingReservation.getCustomerId()))
                    .findFirst()
                    .ifPresent(customerCombo::setValue);
        }

        // Load tables
        List<Table> tablesList;
        try {
            Long restaurantId = SessionManager.getInstance().getRestaurantId();
            tablesList = tableRepository.findAll().stream()
                    .filter(t -> {
                        try {
                            Optional<Section> section = sectionRepository.findById(t.getSectionId());
                            return section.isPresent() && restaurantId.equals(section.get().getRestaurantId());
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to load tables", e);
            tablesList = new java.util.ArrayList<>();
        }

        ComboBox<Table> tableCombo = new ComboBox<>();
        tableCombo.setItems(FXCollections.observableArrayList(tablesList));
        tableCombo.setConverter(new javafx.util.StringConverter<Table>() {
            @Override
            public String toString(Table table) {
                if (table == null) return "";
                try {
                    Optional<Section> section = sectionRepository.findById(table.getSectionId());
                    String sectionName = section.map(Section::getName).orElse("Unknown");
                    return "Table " + table.getTableNumber() + " (" + sectionName + ") - Capacity: " + table.getCapacity();
                } catch (Exception e) {
                    return "Table " + table.getTableNumber();
                }
            }

            @Override
            public Table fromString(String string) {
                return null;
            }
        });
        if (existingReservation != null) {
            tablesList.stream()
                    .filter(t -> t.getTableId().equals(existingReservation.getTableId()))
                    .findFirst()
                    .ifPresent(tableCombo::setValue);
        }

        DatePicker datePicker = new DatePicker();
        if (existingReservation != null && existingReservation.getReservationDatetime() != null) {
            datePicker.setValue(existingReservation.getReservationDatetime().toLocalDate());
        } else {
            datePicker.setValue(LocalDate.now());
        }

        // Time fields
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 12);
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0, 15); // 15 min increments
        if (existingReservation != null && existingReservation.getReservationDatetime() != null) {
            hourSpinner.getValueFactory().setValue(existingReservation.getReservationDatetime().getHour());
            minuteSpinner.getValueFactory().setValue(existingReservation.getReservationDatetime().getMinute());
        }

        Spinner<Integer> partySizeSpinner = new Spinner<>(1, 20, 2);
        if (existingReservation != null) {
            partySizeSpinner.getValueFactory().setValue(existingReservation.getPartySize());
        }

        TextArea specialRequestsField = new TextArea();
        specialRequestsField.setPromptText("Special requests (optional)");
        specialRequestsField.setPrefRowCount(3);
        if (existingReservation != null && existingReservation.getSpecialRequests() != null) {
            specialRequestsField.setText(existingReservation.getSpecialRequests());
        }

        HBox timeBox = new HBox(5, hourSpinner, new Label(":"), minuteSpinner);

        grid.add(new Label("Customer:"), 0, 0);
        grid.add(customerCombo, 1, 0);
        grid.add(new Label("Table:"), 0, 1);
        grid.add(tableCombo, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Time:"), 0, 3);
        grid.add(timeBox, 1, 3);
        grid.add(new Label("Party Size:"), 0, 4);
        grid.add(partySizeSpinner, 1, 4);
        grid.add(new Label("Special Requests:"), 0, 5);
        grid.add(specialRequestsField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Enable/disable save button based on validation
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Validation
        Runnable validateForm = () -> {
            boolean isValid = customerCombo.getValue() != null
                    && tableCombo.getValue() != null
                    && datePicker.getValue() != null;
            saveButton.setDisable(!isValid);
        };

        customerCombo.valueProperty().addListener((obs, old, newVal) -> validateForm.run());
        tableCombo.valueProperty().addListener((obs, old, newVal) -> validateForm.run());
        datePicker.valueProperty().addListener((obs, old, newVal) -> validateForm.run());

        // Convert result to Reservation when save is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Reservation reservation = existingReservation != null ? existingReservation : new Reservation();
                reservation.setCustomerId(customerCombo.getValue().getCustomerId());
                reservation.setTableId(tableCombo.getValue().getTableId());

                LocalDateTime dateTime = LocalDateTime.of(
                        datePicker.getValue(),
                        java.time.LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue())
                );
                reservation.setReservationDatetime(dateTime);
                reservation.setPartySize(partySizeSpinner.getValue());
                reservation.setSpecialRequests(specialRequestsField.getText().trim().isEmpty() ? null : specialRequestsField.getText().trim());

                // If new reservation, set default status
                if (existingReservation == null) {
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                }

                return reservation;
            }
            return null;
        });

        return dialog;
    }

    private void handleCancelReservation(Reservation reservation) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Reservation");
        confirm.setHeaderText("Are you sure you want to cancel this reservation?");

        try {
            Optional<Customer> customer = customerRepository.findById(reservation.getCustomerId());
            String customerName = customer.map(Customer::getFullName).orElse("Unknown");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
            String dateTime = reservation.getReservationDatetime().format(formatter);
            confirm.setContentText(customerName + " - " + dateTime);
        } catch (Exception e) {
            logger.error("Failed to load customer info", e);
            confirm.setContentText("Reservation #" + reservation.getReservationId());
        }

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.update(reservation);
                loadReservations();
                logger.info("Cancelled reservation: {}", reservation.getReservationId());
            } catch (Exception e) {
                logger.error("Failed to cancel reservation", e);
                showAlert("Error", "Failed to cancel reservation: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClearFilters() {
        statusFilterCombo.setValue("All");
        datePicker.setValue(null);
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
