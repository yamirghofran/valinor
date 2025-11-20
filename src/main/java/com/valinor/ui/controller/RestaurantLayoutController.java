package com.valinor.ui.controller;

import com.valinor.data.entity.Section;
import com.valinor.data.entity.Table;
import com.valinor.data.repository.SectionRepository;
import com.valinor.data.repository.TableRepository;
import com.valinor.ui.util.SessionManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for restaurant layout management view
 */
public class RestaurantLayoutController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantLayoutController.class);

    @FXML
    private TableView<Section> sectionsTable;

    @FXML
    private TableColumn<Section, String> sectionNameColumn;

    @FXML
    private TableColumn<Section, String> sectionDescriptionColumn;

    @FXML
    private TableColumn<Section, Integer> sectionTableCountColumn;

    @FXML
    private TableColumn<Section, Void> sectionActionsColumn;

    @FXML
    private TableView<Table> tablesTable;

    @FXML
    private TableColumn<Table, String> tableNumberColumn;

    @FXML
    private TableColumn<Table, String> tableSectionColumn;

    @FXML
    private TableColumn<Table, Integer> tableCapacityColumn;

    @FXML
    private TableColumn<Table, String> tableStatusColumn;

    @FXML
    private TableColumn<Table, Void> tableActionsColumn;

    @FXML
    private ComboBox<String> sectionFilterCombo;

    private SectionRepository sectionRepository;
    private TableRepository tableRepository;
    private ObservableList<Section> sections;
    private ObservableList<Table> tables;

    @FXML
    public void initialize() {
        try {
            // Initialize repositories
            sectionRepository = new SectionRepository(com.valinor.ui.util.DataPaths.SECTIONS_CSV);
            tableRepository = new TableRepository(com.valinor.ui.util.DataPaths.TABLES_CSV);

            sections = FXCollections.observableArrayList();
            tables = FXCollections.observableArrayList();

            // Setup sections table
            setupSectionsTable();

            // Setup tables table
            setupTablesTable();

            // Load data
            loadSections();
            loadTables();

            // Setup section filter
            setupSectionFilter();

            logger.info("Restaurant layout controller initialized");
        } catch (Exception e) {
            logger.error("Failed to initialize restaurant layout controller", e);
        }
    }

    private void setupSectionsTable() {
        sectionNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        sectionDescriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNotes()));

        sectionTableCountColumn.setCellValueFactory(cellData -> {
            Section section = cellData.getValue();
            try {
                long count = tableRepository.findAll().stream()
                        .filter(table -> section.getSectionId().equals(table.getSectionId()))
                        .count();
                return new SimpleIntegerProperty((int) count).asObject();
            } catch (Exception e) {
                return new SimpleIntegerProperty(0).asObject();
            }
        });

        // Add action buttons
        sectionActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏ Edit");
            private final Button deleteButton = new Button("🗑 Delete");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                buttons.setAlignment(javafx.geometry.Pos.CENTER);
                editButton.getStyleClass().add("button-edit");
                deleteButton.getStyleClass().add("button-delete");

                // Set minimum widths to prevent text cutoff
                editButton.setMinWidth(70);
                deleteButton.setMinWidth(80);

                // Add tooltips for clarity
                editButton.setTooltip(new Tooltip("Edit section"));
                deleteButton.setTooltip(new Tooltip("Delete section"));

                editButton.setOnAction(event -> {
                    Section section = getTableView().getItems().get(getIndex());
                    handleEditSection(section);
                });

                deleteButton.setOnAction(event -> {
                    Section section = getTableView().getItems().get(getIndex());
                    handleDeleteSection(section);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        sectionsTable.setItems(sections);
    }

    private void setupTablesTable() {
        tableNumberColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTableNumber()));

        tableSectionColumn.setCellValueFactory(cellData -> {
            Table table = cellData.getValue();
            try {
                Section section = sectionRepository.findById(table.getSectionId()).orElse(null);
                return new SimpleStringProperty(section != null ? section.getName() : "N/A");
            } catch (Exception e) {
                return new SimpleStringProperty("N/A");
            }
        });

        tableCapacityColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());

        tableStatusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isActive() ? "Active" : "Inactive"));

        // Add action buttons
        tableActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏ Edit");
            private final Button toggleButton = new Button("⚡ Toggle");
            private final Button deleteButton = new Button("🗑 Delete");
            private final HBox buttons = new HBox(5, editButton, toggleButton, deleteButton);

            {
                buttons.setAlignment(javafx.geometry.Pos.CENTER);
                editButton.getStyleClass().add("button-edit");
                toggleButton.getStyleClass().add("button-action");
                deleteButton.getStyleClass().add("button-delete");

                // Set minimum widths to prevent text cutoff
                editButton.setMinWidth(70);
                toggleButton.setMinWidth(85);
                deleteButton.setMinWidth(80);

                // Add tooltips for clarity
                editButton.setTooltip(new Tooltip("Edit table details"));
                toggleButton.setTooltip(new Tooltip("Toggle active/inactive status"));
                deleteButton.setTooltip(new Tooltip("Delete table"));

                editButton.setOnAction(event -> {
                    Table table = getTableView().getItems().get(getIndex());
                    handleEditTable(table);
                });

                toggleButton.setOnAction(event -> {
                    Table table = getTableView().getItems().get(getIndex());
                    handleToggleTable(table);
                });

                deleteButton.setOnAction(event -> {
                    Table table = getTableView().getItems().get(getIndex());
                    handleDeleteTable(table);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        tablesTable.setItems(tables);
    }

    private void setupSectionFilter() {
        try {
            List<String> sectionNames = sections.stream()
                    .map(Section::getName)
                    .collect(Collectors.toList());
            sectionNames.add(0, "All Sections");

            sectionFilterCombo.setItems(FXCollections.observableArrayList(sectionNames));
            sectionFilterCombo.setValue("All Sections");

            sectionFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> filterTables(newVal));
        } catch (Exception e) {
            logger.error("Failed to setup section filter", e);
        }
    }

    private void filterTables(String sectionName) {
        try {
            if (sectionName == null || sectionName.equals("All Sections")) {
                loadTables();
            } else {
                Section selectedSection = sections.stream()
                        .filter(s -> s.getName().equals(sectionName))
                        .findFirst()
                        .orElse(null);

                if (selectedSection != null) {
                    List<Table> filtered = tableRepository.findAll().stream()
                            .filter(table -> selectedSection.getSectionId().equals(table.getSectionId()))
                            .collect(Collectors.toList());
                    tables.setAll(filtered);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to filter tables", e);
        }
    }

    private void loadSections() {
        try {
            Long restaurantId = SessionManager.getInstance().getRestaurantId();
            if (restaurantId != null) {
                List<Section> sectionList = sectionRepository.findAll().stream()
                        .filter(section -> restaurantId.equals(section.getRestaurantId()))
                        .collect(Collectors.toList());
                sections.setAll(sectionList);
            }
        } catch (Exception e) {
            logger.error("Failed to load sections", e);
        }
    }

    private void loadTables() {
        try {
            Long restaurantId = SessionManager.getInstance().getRestaurantId();
            if (restaurantId != null) {
                List<Table> tableList = tableRepository.findAll().stream()
                        .filter(table -> {
                            try {
                                Section section = sectionRepository.findById(table.getSectionId()).orElse(null);
                                return section != null && restaurantId.equals(section.getRestaurantId());
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());
                tables.setAll(tableList);
            }
        } catch (Exception e) {
            logger.error("Failed to load tables", e);
        }
    }

    @FXML
    private void handleAddSection() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Section");
            dialog.setHeaderText("Create New Section");
            dialog.setContentText("Section Name:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    try {
                        Section section = new Section();
                        section.setName(name.trim());
                        section.setRestaurantId(SessionManager.getInstance().getRestaurantId());
                        section.setNotes("");

                        sectionRepository.save(section);
                        loadSections();
                        setupSectionFilter();

                        logger.info("Section added: {}", name);
                    } catch (Exception e) {
                        logger.error("Failed to add section", e);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Error in handleAddSection", e);
        }
    }

    private void handleEditSection(Section section) {
        try {
            TextInputDialog dialog = new TextInputDialog(section.getName());
            dialog.setTitle("Edit Section");
            dialog.setHeaderText("Edit Section Name");
            dialog.setContentText("Section Name:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    try {
                        section.setName(name.trim());
                        sectionRepository.save(section);
                        loadSections();
                        setupSectionFilter();

                        logger.info("Section updated: {}", name);
                    } catch (Exception e) {
                        logger.error("Failed to edit section", e);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Error in handleEditSection", e);
        }
    }

    private void handleDeleteSection(Section section) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Section");
            alert.setHeaderText("Delete " + section.getName() + "?");
            alert.setContentText("This will also delete all tables in this section.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                sectionRepository.deleteById(section.getSectionId());
                loadSections();
                loadTables();
                setupSectionFilter();

                logger.info("Section deleted: {}", section.getName());
            }
        } catch (Exception e) {
            logger.error("Failed to delete section", e);
        }
    }

    @FXML
    private void handleAddTable() {
        if (sections.isEmpty()) {
            showAlert("No Sections", "Please create a section first before adding tables.");
            return;
        }

        Dialog<Table> dialog = createTableDialog(null);
        Optional<Table> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                Table newTable = result.get();
                newTable.setIsActive(true);
                tableRepository.save(newTable);
                loadTables();
                logger.info("Table created: {}", newTable.getTableNumber());
            } catch (Exception e) {
                logger.error("Failed to create table", e);
                showAlert("Error", "Failed to create table: " + e.getMessage());
            }
        }
    }

    private void handleEditTable(Table table) {
        Dialog<Table> dialog = createTableDialog(table);
        Optional<Table> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                Table updatedTable = result.get();
                tableRepository.update(updatedTable);
                loadTables();
                logger.info("Table updated: {}", updatedTable.getTableNumber());
            } catch (Exception e) {
                logger.error("Failed to update table", e);
                showAlert("Error", "Failed to update table: " + e.getMessage());
            }
        }
    }

    private Dialog<Table> createTableDialog(Table existingTable) {
        Dialog<Table> dialog = new Dialog<>();
        dialog.setTitle(existingTable == null ? "Add Table" : "Edit Table");
        dialog.setHeaderText(existingTable == null ? "Create New Table" : "Edit Table");

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form fields
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField tableNumberField = new TextField();
        tableNumberField.setPromptText("Table Number");
        if (existingTable != null) {
            tableNumberField.setText(existingTable.getTableNumber());
        }

        ComboBox<Section> sectionCombo = new ComboBox<>();
        sectionCombo.setItems(sections);
        sectionCombo.setConverter(new javafx.util.StringConverter<Section>() {
            @Override
            public String toString(Section section) {
                return section == null ? "" : section.getName();
            }

            @Override
            public Section fromString(String string) {
                return null;
            }
        });
        if (existingTable != null) {
            // Find and select the current section
            sections.stream()
                .filter(s -> s.getSectionId().equals(existingTable.getSectionId()))
                .findFirst()
                .ifPresent(sectionCombo::setValue);
        } else {
            sectionCombo.setValue(sections.get(0));
        }

        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacity");
        if (existingTable != null) {
            capacityField.setText(String.valueOf(existingTable.getCapacity()));
        }

        grid.add(new Label("Table Number:"), 0, 0);
        grid.add(tableNumberField, 1, 0);
        grid.add(new Label("Section:"), 0, 1);
        grid.add(sectionCombo, 1, 1);
        grid.add(new Label("Capacity:"), 0, 2);
        grid.add(capacityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Enable/disable save button based on validation
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Validation
        tableNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || capacityField.getText().trim().isEmpty());
        });

        capacityField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(tableNumberField.getText().trim().isEmpty() || newValue.trim().isEmpty());
        });

        // Convert result to Table when save is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Table table = existingTable != null ? existingTable : new Table();
                    table.setTableNumber(tableNumberField.getText().trim());
                    table.setSectionId(sectionCombo.getValue().getSectionId());
                    table.setCapacity(Integer.parseInt(capacityField.getText().trim()));

                    return table;
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Capacity must be a valid number.");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private void handleToggleTable(Table table) {
        try {
            table.setIsActive(!table.isActive());
            tableRepository.save(table);
            loadTables();

            logger.info("Table {} status changed to: {}", table.getTableNumber(), table.isActive());
        } catch (Exception e) {
            logger.error("Failed to toggle table", e);
        }
    }

    private void handleDeleteTable(Table table) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Table");
            alert.setHeaderText("Delete Table " + table.getTableNumber() + "?");
            alert.setContentText("This action cannot be undone.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                tableRepository.deleteById(table.getTableId());
                loadTables();

                logger.info("Table deleted: {}", table.getTableNumber());
            }
        } catch (Exception e) {
            logger.error("Failed to delete table", e);
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
