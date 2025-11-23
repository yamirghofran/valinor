package com.valinor.repository;

import com.valinor.domain.model.Table;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.mapper.TableEntityMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CSV-based repository for Table entities.
 * Provides CRUD operations for table data stored in CSV files.
 */
public class TableRepository extends AbstractCsvRepository<Table, Long> {
    
    /**
     * Constructs a new TableRepository.
     * 
     * @param filePath the path to the tables CSV file
     * @throws RepositoryException if initialization fails
     */
    public TableRepository(String filePath) throws RepositoryException {
        super(filePath, new TableEntityMapper());
    }
    
    /**
     * Finds all tables in a specific section.
     * Critical for retrieving the layout of a section.
     * 
     * @param sectionId the section ID to search for
     * @return list of tables belonging to the section
     * @throws RepositoryException if search fails
     */
    public List<Table> findBySectionId(Long sectionId) throws RepositoryException {
        return findByField("section_id", sectionId);
    }
    
    /**
     * Finds tables by table number.
     * Note: Table numbers may not be unique across sections.
     * 
     * @param tableNumber the table number to search for
     * @return list of tables matching the table number
     * @throws RepositoryException if search fails
     */
    public List<Table> findByTableNumber(String tableNumber) throws RepositoryException {
        return findByField("table_number", tableNumber);
    }
    
    /**
     * Finds a specific table by section ID and table number.
     * This combination should be unique within a restaurant.
     * 
     * @param sectionId the section ID
     * @param tableNumber the table number
     * @return list of tables matching both criteria
     * @throws RepositoryException if search fails
     */
    public List<Table> findBySectionIdAndTableNumber(Long sectionId, String tableNumber) throws RepositoryException {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("section_id", sectionId);
        criteria.put("table_number", tableNumber);
        return findByFields(criteria);
    }
    
    /**
     * Finds a single table by section ID and table number.
     * 
     * @param sectionId the section ID
     * @param tableNumber the table number
     * @return optional containing the table if found
     * @throws RepositoryException if search fails
     */
    public Optional<Table> findOneBySectionIdAndTableNumber(Long sectionId, String tableNumber) throws RepositoryException {
        List<Table> results = findBySectionIdAndTableNumber(sectionId, tableNumber);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    /**
     * Finds only active tables in a specific section.
     * Filters out inactive/disabled tables.
     * 
     * @param sectionId the section ID to search for
     * @return list of active tables in the section
     * @throws RepositoryException if search fails
     */
    public List<Table> findActiveTablesBySectionId(Long sectionId) throws RepositoryException {
        return findBySectionId(sectionId).stream()
                .filter(Table::isActive)
                .collect(Collectors.toList());
    }
    
    /**
     * Finds tables by seating capacity.
     * Useful for finding tables that can accommodate a specific party size.
     * 
     * @param capacity the seating capacity
     * @return list of tables with the specified capacity
     * @throws RepositoryException if search fails
     */
    public List<Table> findByCapacity(Integer capacity) throws RepositoryException {
        return findByField("capacity", capacity);
    }
    
    /**
     * Finds tables with capacity greater than or equal to the specified size.
     * Useful for finding suitable tables for a reservation party.
     * 
     * @param minCapacity the minimum capacity required
     * @return list of tables with capacity >= minCapacity
     * @throws RepositoryException if search fails
     */
    public List<Table> findByMinCapacity(Integer minCapacity) throws RepositoryException {
        return findWhere(table -> table.getCapacity() != null && table.getCapacity() >= minCapacity);
    }
    
    /**
     * Finds available (active) tables in a section.
     * Alias for findActiveTablesBySectionId for clarity.
     * 
     * @param sectionId the section ID
     * @return list of available tables in the section
     * @throws RepositoryException if search fails
     */
    public List<Table> findAvailableTablesBySectionId(Long sectionId) throws RepositoryException {
        return findActiveTablesBySectionId(sectionId);
    }
    
    /**
     * Finds all active tables across all sections.
     * 
     * @return list of all active tables
     * @throws RepositoryException if search fails
     */
    public List<Table> findAllActiveTables() throws RepositoryException {
        return findWhere(Table::isActive);
    }
    
    /**
     * Finds all inactive tables across all sections.
     * 
     * @return list of all inactive tables
     * @throws RepositoryException if search fails
     */
    public List<Table> findAllInactiveTables() throws RepositoryException {
        return findWhere(table -> !table.isActive());
    }
    
    /**
     * Counts the number of tables in a section.
     * 
     * @param sectionId the section ID
     * @return the count of tables
     * @throws RepositoryException if counting fails
     */
    public long countBySectionId(Long sectionId) throws RepositoryException {
        return findBySectionId(sectionId).size();
    }
    
    /**
     * Counts the number of active tables in a section.
     * 
     * @param sectionId the section ID
     * @return the count of active tables
     * @throws RepositoryException if counting fails
     */
    public long countActiveBySectionId(Long sectionId) throws RepositoryException {
        return findActiveTablesBySectionId(sectionId).size();
    }
    
    /**
     * Deletes all tables in a section.
     * Useful for cascade deletes when removing a section.
     * 
     * @param sectionId the section ID
     * @return the number of tables deleted
     * @throws RepositoryException if deletion fails
     */
    public int deleteBySectionId(Long sectionId) throws RepositoryException {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("section_id", sectionId);
        return deleteByFields(criteria);
    }
    
    /**
     * Activates a table (sets is_active to true).
     * 
     * @param tableId the table ID
     * @return the updated table
     * @throws RepositoryException if update fails or table not found
     */
    public Table activateTable(Long tableId) throws RepositoryException {
        Optional<Table> tableOpt = findById(tableId);
        if (!tableOpt.isPresent()) {
            throw new RepositoryException("Table not found with ID: " + tableId);
        }
        
        Table table = tableOpt.get();
        table.setIsActive(true);
        return update(table);
    }
    
    /**
     * Deactivates a table (sets is_active to false).
     * 
     * @param tableId the table ID
     * @return the updated table
     * @throws RepositoryException if update fails or table not found
     */
    public Table deactivateTable(Long tableId) throws RepositoryException {
        Optional<Table> tableOpt = findById(tableId);
        if (!tableOpt.isPresent()) {
            throw new RepositoryException("Table not found with ID: " + tableId);
        }
        
        Table table = tableOpt.get();
        table.setIsActive(false);
        return update(table);
    }
    
    @Override
    protected boolean fieldValueMatches(Table entity, String fieldName, Object expectedValue) {
        if (entity == null || expectedValue == null) {
            return false;
        }
        
        try {
            switch (fieldName.toLowerCase()) {
                case "table_id":
                    return expectedValue.equals(entity.getTableId());
                case "section_id":
                    return expectedValue.equals(entity.getSectionId());
                case "table_number":
                    return expectedValue.equals(entity.getTableNumber());
                case "capacity":
                    return expectedValue.equals(entity.getCapacity());
                case "is_active":
                    return expectedValue.equals(entity.getIsActive());
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
