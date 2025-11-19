package com.valinor.restauraunt.management.service;

import com.valinor.data.entity.Restaurant;
import com.valinor.data.entity.Section;
import com.valinor.data.entity.Table;
import com.valinor.data.exception.RepositoryException;
import com.valinor.data.repository.RestaurantRepository;
import com.valinor.data.repository.SectionRepository;
import com.valinor.data.repository.TableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for validating restaurant layout business rules and data integrity.
 * Ensures consistency between restaurants, sections, and tables.
 */
public class LayoutValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(LayoutValidationService.class);
    
    private final RestaurantRepository restaurantRepository;
    private final SectionRepository sectionRepository;
    private final TableRepository tableRepository;
    
    /**
     * Constructs a new LayoutValidationService.
     * 
     * @param restaurantRepository the restaurant repository
     * @param sectionRepository the section repository
     * @param tableRepository the table repository
     */
    public LayoutValidationService(RestaurantRepository restaurantRepository,
                                    SectionRepository sectionRepository,
                                    TableRepository tableRepository) {
        this.restaurantRepository = restaurantRepository;
        this.sectionRepository = sectionRepository;
        this.tableRepository = tableRepository;
    }
    
    /**
     * Validates that a restaurant exists.
     * 
     * @param restaurantId the restaurant ID
     * @throws LayoutValidationException if restaurant does not exist
     * @throws RepositoryException if validation fails
     */
    public void validateRestaurantExists(Long restaurantId) throws LayoutValidationException, RepositoryException {
        if (restaurantId == null) {
            throw new LayoutValidationException("Restaurant ID cannot be null");
        }
        
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (!restaurant.isPresent()) {
            throw new LayoutValidationException("Restaurant not found with ID: " + restaurantId);
        }
    }
    
    /**
     * Validates that a section exists.
     * 
     * @param sectionId the section ID
     * @throws LayoutValidationException if section does not exist
     * @throws RepositoryException if validation fails
     */
    public void validateSectionExists(Long sectionId) throws LayoutValidationException, RepositoryException {
        if (sectionId == null) {
            throw new LayoutValidationException("Section ID cannot be null");
        }
        
        Optional<Section> section = sectionRepository.findById(sectionId);
        if (!section.isPresent()) {
            throw new LayoutValidationException("Section not found with ID: " + sectionId);
        }
    }
    
    /**
     * Validates that a table exists.
     * 
     * @param tableId the table ID
     * @throws LayoutValidationException if table does not exist
     * @throws RepositoryException if validation fails
     */
    public void validateTableExists(Long tableId) throws LayoutValidationException, RepositoryException {
        if (tableId == null) {
            throw new LayoutValidationException("Table ID cannot be null");
        }
        
        Optional<Table> table = tableRepository.findById(tableId);
        if (!table.isPresent()) {
            throw new LayoutValidationException("Table not found with ID: " + tableId);
        }
    }
    
    /**
     * Validates that a section belongs to the specified restaurant.
     * 
     * @param sectionId the section ID
     * @param restaurantId the expected restaurant ID
     * @throws LayoutValidationException if section does not belong to restaurant
     * @throws RepositoryException if validation fails
     */
    public void validateSectionBelongsToRestaurant(Long sectionId, Long restaurantId) 
            throws LayoutValidationException, RepositoryException {
        validateSectionExists(sectionId);
        
        Optional<Section> section = sectionRepository.findById(sectionId);
        if (section.isPresent() && !section.get().getRestaurantId().equals(restaurantId)) {
            throw new LayoutValidationException(
                "Section " + sectionId + " does not belong to restaurant " + restaurantId);
        }
    }
    
    /**
     * Validates that a table belongs to the specified section.
     * 
     * @param tableId the table ID
     * @param sectionId the expected section ID
     * @throws LayoutValidationException if table does not belong to section
     * @throws RepositoryException if validation fails
     */
    public void validateTableBelongsToSection(Long tableId, Long sectionId) 
            throws LayoutValidationException, RepositoryException {
        validateTableExists(tableId);
        
        Optional<Table> table = tableRepository.findById(tableId);
        if (table.isPresent() && !table.get().getSectionId().equals(sectionId)) {
            throw new LayoutValidationException(
                "Table " + tableId + " does not belong to section " + sectionId);
        }
    }
    
    /**
     * Validates that table number is unique within a section.
     * 
     * @param sectionId the section ID
     * @param tableNumber the table number to check
     * @param excludeTableId optional table ID to exclude from check (for updates)
     * @throws LayoutValidationException if table number already exists in section
     * @throws RepositoryException if validation fails
     */
    public void validateTableNumberUnique(Long sectionId, String tableNumber, Long excludeTableId) 
            throws LayoutValidationException, RepositoryException {
        if (tableNumber == null || tableNumber.trim().isEmpty()) {
            throw new LayoutValidationException("Table number cannot be null or empty");
        }
        
        List<Table> existingTables = tableRepository.findBySectionIdAndTableNumber(sectionId, tableNumber);
        
        for (Table table : existingTables) {
            // Skip if this is the table being updated
            if (excludeTableId != null && table.getTableId().equals(excludeTableId)) {
                continue;
            }
            throw new LayoutValidationException(
                "Table number '" + tableNumber + "' already exists in section " + sectionId);
        }
    }
    
    /**
     * Validates table capacity constraints.
     * 
     * @param capacity the capacity to validate
     * @throws LayoutValidationException if capacity is invalid
     */
    public void validateTableCapacity(Integer capacity) throws LayoutValidationException {
        if (capacity == null) {
            throw new LayoutValidationException("Table capacity cannot be null");
        }
        
        if (capacity <= 0) {
            throw new LayoutValidationException("Table capacity must be greater than 0");
        }
        
        if (capacity > 50) { // Reasonable upper limit
            throw new LayoutValidationException("Table capacity cannot exceed 50 seats");
        }
    }
    
    /**
     * Validates the entire restaurant layout for consistency.
     * Checks for orphaned sections, orphaned tables, and other integrity issues.
     * 
     * @param restaurantId the restaurant ID
     * @return a list of validation issues found
     * @throws RepositoryException if validation check fails
     */
    public List<String> validateCompleteLayout(Long restaurantId) throws RepositoryException {
        List<String> issues = new ArrayList<>();
        
        // Check restaurant exists
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (!restaurant.isPresent()) {
            issues.add("Restaurant not found with ID: " + restaurantId);
            return issues; // Can't continue without restaurant
        }
        
        // Get all sections for this restaurant
        List<Section> sections = sectionRepository.findByRestaurantId(restaurantId);
        
        if (sections.isEmpty()) {
            issues.add("Warning: Restaurant has no sections defined");
        }
        
        // Validate each section
        for (Section section : sections) {
            // Check for tables in this section
            List<Table> tables = tableRepository.findBySectionId(section.getSectionId());
            
            if (tables.isEmpty()) {
                issues.add("Warning: Section '" + section.getName() + "' has no tables");
            }
            
            // Check for duplicate table numbers
            Set<String> tableNumbers = new HashSet<>();
            for (Table table : tables) {
                if (!tableNumbers.add(table.getTableNumber())) {
                    issues.add("Error: Duplicate table number '" + table.getTableNumber() + 
                             "' in section '" + section.getName() + "'");
                }
            }
            
            // Check for invalid capacities
            for (Table table : tables) {
                if (table.getCapacity() == null || table.getCapacity() <= 0) {
                    issues.add("Error: Table '" + table.getTableNumber() + "' in section '" + 
                             section.getName() + "' has invalid capacity: " + table.getCapacity());
                }
            }
        }
        
        // Check for orphaned sections (sections pointing to non-existent restaurants)
        List<Section> allSections = sectionRepository.findAll();
        for (Section section : allSections) {
            if (!restaurantRepository.existsById(section.getRestaurantId())) {
                issues.add("Error: Orphaned section '" + section.getName() + "' (section_id=" + 
                         section.getSectionId() + ") points to non-existent restaurant " + 
                         section.getRestaurantId());
            }
        }
        
        // Check for orphaned tables (tables pointing to non-existent sections)
        List<Table> allTables = tableRepository.findAll();
        for (Table table : allTables) {
            if (!sectionRepository.existsById(table.getSectionId())) {
                issues.add("Error: Orphaned table '" + table.getTableNumber() + "' (table_id=" + 
                         table.getTableId() + ") points to non-existent section " + 
                         table.getSectionId());
            }
        }
        
        logger.info("Layout validation for restaurant {} found {} issue(s)", restaurantId, issues.size());
        return issues;
    }
    
    /**
     * Checks if a section can be safely deleted.
     * 
     * @param sectionId the section ID
     * @return true if section can be deleted (has no tables or caller accepts cascade)
     * @throws RepositoryException if check fails
     */
    public boolean canDeleteSection(Long sectionId) throws RepositoryException {
        List<Table> tables = tableRepository.findBySectionId(sectionId);
        return tables.isEmpty();
    }
    
    /**
     * Gets the count of tables that would be deleted if section is deleted.
     * 
     * @param sectionId the section ID
     * @return the number of tables in the section
     * @throws RepositoryException if check fails
     */
    public int getTableCountForSection(Long sectionId) throws RepositoryException {
        return tableRepository.findBySectionId(sectionId).size();
    }
}

