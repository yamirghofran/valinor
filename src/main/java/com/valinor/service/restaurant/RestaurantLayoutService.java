package com.valinor.service.restaurant;

import com.valinor.domain.model.Restaurant;
import com.valinor.domain.model.Section;
import com.valinor.domain.model.Table;
import com.valinor.exception.RepositoryException;
import com.valinor.exception.LayoutValidationException;
import com.valinor.repository.RestaurantRepository;
import com.valinor.repository.SectionRepository;
import com.valinor.repository.TableRepository;
import com.valinor.service.dto.restaurant.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Main service for managing restaurant layouts.
 * Provides high-level operations for creating, updating, and querying restaurant layouts
 * including sections and tables.
 */
public class RestaurantLayoutService {
    
    private static final Logger logger = LoggerFactory.getLogger(RestaurantLayoutService.class);
    
    private final RestaurantRepository restaurantRepository;
    private final SectionRepository sectionRepository;
    private final TableRepository tableRepository;
    private final LayoutValidationService validationService;
    
    /**
     * Constructs a new RestaurantLayoutService.
     * 
     * @param restaurantRepository the restaurant repository
     * @param sectionRepository the section repository
     * @param tableRepository the table repository
     */
    public RestaurantLayoutService(RestaurantRepository restaurantRepository,
                                    SectionRepository sectionRepository,
                                    TableRepository tableRepository) {
        this.restaurantRepository = restaurantRepository;
        this.sectionRepository = sectionRepository;
        this.tableRepository = tableRepository;
        this.validationService = new LayoutValidationService(restaurantRepository, sectionRepository, tableRepository);
    }
    
    /**
     * Creates a new section in a restaurant.
     * 
     * @param request the section creation request
     * @return the created section
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if creation fails
     */
    public Section createSection(CreateSectionRequest request) 
            throws LayoutValidationException, RepositoryException {
        logger.info("Creating section '{}' for restaurant {}", request.getName(), request.getRestaurantId());
        
        // Validate restaurant exists
        validationService.validateRestaurantExists(request.getRestaurantId());
        
        // Create section entity
        Section section = new Section(request.getRestaurantId(), request.getName());
        section.setNumTables(request.getNumTables());
        section.setNotes(request.getNotes());
        
        // Save section
        section = sectionRepository.save(section);
        
        logger.info("Created section with ID: {}", section.getSectionId());
        return section;
    }
    
    /**
     * Updates an existing section.
     * 
     * @param sectionId the section ID
     * @param request the update request
     * @return the updated section
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if update fails
     */
    public Section updateSection(Long sectionId, UpdateSectionRequest request) 
            throws LayoutValidationException, RepositoryException {
        logger.info("Updating section {}", sectionId);
        
        // Validate section exists
        validationService.validateSectionExists(sectionId);
        
        // Get existing section
        Optional<Section> sectionOpt = sectionRepository.findById(sectionId);
        if (!sectionOpt.isPresent()) {
            throw new LayoutValidationException("Section not found: " + sectionId);
        }
        
        Section section = sectionOpt.get();
        
        // Update fields if provided
        if (request.getName() != null) {
            section.setName(request.getName());
        }
        if (request.getNumTables() != null) {
            section.setNumTables(request.getNumTables());
        }
        if (request.getNotes() != null) {
            section.setNotes(request.getNotes());
        }
        
        // Save updated section
        section = sectionRepository.update(section);
        
        logger.info("Updated section {}", sectionId);
        return section;
    }
    
    /**
     * Deletes a section and optionally its tables.
     * 
     * @param sectionId the section ID
     * @param cascade if true, also delete all tables in the section
     * @return the number of tables deleted (if cascade=true)
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if deletion fails
     */
    public int deleteSection(Long sectionId, boolean cascade) 
            throws LayoutValidationException, RepositoryException {
        logger.info("Deleting section {} (cascade={})", sectionId, cascade);
        
        // Validate section exists
        validationService.validateSectionExists(sectionId);
        
        int tablesDeleted = 0;
        
        if (!cascade && !validationService.canDeleteSection(sectionId)) {
            int tableCount = validationService.getTableCountForSection(sectionId);
            throw new LayoutValidationException(
                "Cannot delete section " + sectionId + " because it has " + tableCount + 
                " table(s). Use cascade=true to delete tables as well.");
        }
        
        // Delete tables if cascade
        if (cascade) {
            tablesDeleted = tableRepository.deleteBySectionId(sectionId);
            logger.info("Deleted {} table(s) from section {}", tablesDeleted, sectionId);
        }
        
        // Delete section
        boolean deleted = sectionRepository.deleteById(sectionId);
        if (!deleted) {
            throw new RepositoryException("Failed to delete section " + sectionId);
        }
        
        logger.info("Deleted section {} with {} table(s)", sectionId, tablesDeleted);
        return tablesDeleted;
    }
    
    /**
     * Creates a new table in a section.
     * 
     * @param request the table creation request
     * @return the created table
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if creation fails
     */
    public Table createTable(CreateTableRequest request) 
            throws LayoutValidationException, RepositoryException {
        logger.info("Creating table '{}' in section {}", request.getTableNumber(), request.getSectionId());
        
        // Validate section exists
        validationService.validateSectionExists(request.getSectionId());
        
        // Validate table capacity
        validationService.validateTableCapacity(request.getCapacity());
        
        // Validate table number is unique in section
        validationService.validateTableNumberUnique(request.getSectionId(), request.getTableNumber(), null);
        
        // Create table entity
        Table table = new Table(request.getSectionId(), request.getTableNumber(), request.getCapacity());
        table.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        // Save table
        table = tableRepository.save(table);
        
        logger.info("Created table with ID: {}", table.getTableId());
        return table;
    }
    
    /**
     * Updates an existing table.
     * 
     * @param tableId the table ID
     * @param request the update request
     * @return the updated table
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if update fails
     */
    public Table updateTable(Long tableId, UpdateTableRequest request) 
            throws LayoutValidationException, RepositoryException {
        logger.info("Updating table {}", tableId);
        
        // Validate table exists
        validationService.validateTableExists(tableId);
        
        // Get existing table
        Optional<Table> tableOpt = tableRepository.findById(tableId);
        if (!tableOpt.isPresent()) {
            throw new LayoutValidationException("Table not found: " + tableId);
        }
        
        Table table = tableOpt.get();
        
        // Update table number if provided
        if (request.getTableNumber() != null) {
            validationService.validateTableNumberUnique(
                table.getSectionId(), request.getTableNumber(), tableId);
            table.setTableNumber(request.getTableNumber());
        }
        
        // Update capacity if provided
        if (request.getCapacity() != null) {
            validationService.validateTableCapacity(request.getCapacity());
            table.setCapacity(request.getCapacity());
        }
        
        // Update active status if provided
        if (request.getIsActive() != null) {
            table.setIsActive(request.getIsActive());
        }
        
        // Save updated table
        table = tableRepository.update(table);
        
        logger.info("Updated table {}", tableId);
        return table;
    }
    
    /**
     * Deletes a table.
     * 
     * @param tableId the table ID
     * @return true if deleted successfully
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if deletion fails
     */
    public boolean deleteTable(Long tableId) 
            throws LayoutValidationException, RepositoryException {
        logger.info("Deleting table {}", tableId);
        
        // Validate table exists
        validationService.validateTableExists(tableId);
        
        // Delete table
        boolean deleted = tableRepository.deleteById(tableId);
        
        if (deleted) {
            logger.info("Deleted table {}", tableId);
        } else {
            logger.warn("Failed to delete table {}", tableId);
        }
        
        return deleted;
    }
    
    /**
     * Activates a table (sets is_active to true).
     * 
     * @param tableId the table ID
     * @return the updated table
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if update fails
     */
    public Table activateTable(Long tableId) 
            throws LayoutValidationException, RepositoryException {
        logger.info("Activating table {}", tableId);
        
        validationService.validateTableExists(tableId);
        Table table = tableRepository.activateTable(tableId);
        
        logger.info("Activated table {}", tableId);
        return table;
    }
    
    /**
     * Deactivates a table (sets is_active to false).
     * 
     * @param tableId the table ID
     * @return the updated table
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if update fails
     */
    public Table deactivateTable(Long tableId) 
            throws LayoutValidationException, RepositoryException {
        logger.info("Deactivating table {}", tableId);
        
        validationService.validateTableExists(tableId);
        Table table = tableRepository.deactivateTable(tableId);
        
        logger.info("Deactivated table {}", tableId);
        return table;
    }
    
    /**
     * Gets the complete layout for a restaurant including all sections and tables.
     * 
     * @param restaurantId the restaurant ID
     * @return the complete restaurant layout
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if retrieval fails
     */
    public RestaurantLayoutDTO getRestaurantLayout(Long restaurantId) 
            throws LayoutValidationException, RepositoryException {
        logger.info("Retrieving layout for restaurant {}", restaurantId);
        
        // Validate restaurant exists
        validationService.validateRestaurantExists(restaurantId);
        
        // Get restaurant
        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);
        if (!restaurantOpt.isPresent()) {
            throw new LayoutValidationException("Restaurant not found: " + restaurantId);
        }
        
        Restaurant restaurant = restaurantOpt.get();
        
        // Get all sections for this restaurant
        List<Section> sections = sectionRepository.findByRestaurantId(restaurantId);
        
        // Build sections with tables
        List<SectionWithTablesDTO> sectionDTOs = new ArrayList<>();
        for (Section section : sections) {
            List<Table> tables = tableRepository.findBySectionId(section.getSectionId());
            SectionWithTablesDTO sectionDTO = new SectionWithTablesDTO(section, tables);
            sectionDTOs.add(sectionDTO);
        }
        
        RestaurantLayoutDTO layout = new RestaurantLayoutDTO(restaurant, sectionDTOs);
        
        logger.info("Retrieved layout for restaurant {}: {} sections, {} tables", 
                   restaurantId, layout.getSectionCount(), layout.getTotalTableCount());
        
        return layout;
    }
    
    /**
     * Gets a section with its tables.
     * 
     * @param sectionId the section ID
     * @return the section with tables
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if retrieval fails
     */
    public SectionWithTablesDTO getSectionWithTables(Long sectionId) 
            throws LayoutValidationException, RepositoryException {
        logger.info("Retrieving section {} with tables", sectionId);
        
        // Validate section exists
        validationService.validateSectionExists(sectionId);
        
        // Get section
        Optional<Section> sectionOpt = sectionRepository.findById(sectionId);
        if (!sectionOpt.isPresent()) {
            throw new LayoutValidationException("Section not found: " + sectionId);
        }
        
        Section section = sectionOpt.get();
        
        // Get tables for this section
        List<Table> tables = tableRepository.findBySectionId(sectionId);
        
        SectionWithTablesDTO dto = new SectionWithTablesDTO(section, tables);
        
        logger.info("Retrieved section {} with {} table(s)", sectionId, dto.getTableCount());
        
        return dto;
    }
    
    /**
     * Validates the layout for a restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @return a list of validation issues found (empty if valid)
     * @throws RepositoryException if validation check fails
     */
    public List<String> validateLayout(Long restaurantId) throws RepositoryException {
        logger.info("Validating layout for restaurant {}", restaurantId);
        
        List<String> issues = validationService.validateCompleteLayout(restaurantId);
        
        if (issues.isEmpty()) {
            logger.info("Layout for restaurant {} is valid", restaurantId);
        } else {
            logger.warn("Layout for restaurant {} has {} issue(s)", restaurantId, issues.size());
        }
        
        return issues;
    }
    
    /**
     * Gets all sections for a restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @return list of sections
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if retrieval fails
     */
    public List<Section> getSections(Long restaurantId) 
            throws LayoutValidationException, RepositoryException {
        validationService.validateRestaurantExists(restaurantId);
        return sectionRepository.findByRestaurantId(restaurantId);
    }
    
    /**
     * Gets all tables for a section.
     * 
     * @param sectionId the section ID
     * @return list of tables
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if retrieval fails
     */
    public List<Table> getTables(Long sectionId) 
            throws LayoutValidationException, RepositoryException {
        validationService.validateSectionExists(sectionId);
        return tableRepository.findBySectionId(sectionId);
    }
    
    /**
     * Gets all active tables for a section.
     * 
     * @param sectionId the section ID
     * @return list of active tables
     * @throws LayoutValidationException if validation fails
     * @throws RepositoryException if retrieval fails
     */
    public List<Table> getActiveTables(Long sectionId) 
            throws LayoutValidationException, RepositoryException {
        validationService.validateSectionExists(sectionId);
        return tableRepository.findActiveTablesBySectionId(sectionId);
    }
}
