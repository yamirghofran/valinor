package com.valinor.service.restaurant;

import com.valinor.domain.model.Section;
import com.valinor.domain.model.Table;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.ReservationRepository;
import com.valinor.repository.SectionRepository;
import com.valinor.repository.TableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for checking table availability.
 * Integrates with reservation module to prevent double-booking.
 * 
 * Checks performed:
 * 1. If table is active (table.isActive())
 * 2. If table has sufficient capacity
 * 3. If table has no conflicting reservations at the requested time
 */
public class TableAvailabilityService {
    
    private static final Logger logger = LoggerFactory.getLogger(TableAvailabilityService.class);
    
    private static final int DEFAULT_DURATION_MINUTES = 120; // 2 hours
    
    private final TableRepository tableRepository;
    private final SectionRepository sectionRepository;
    private final ReservationRepository reservationRepository;
    
    /**
     * Constructs a new TableAvailabilityService without reservation checking.
     * Used for basic availability checks.
     * 
     * @param tableRepository the table repository
     * @param sectionRepository the section repository
     */
    public TableAvailabilityService(TableRepository tableRepository, 
                                     SectionRepository sectionRepository) {
        this.tableRepository = tableRepository;
        this.sectionRepository = sectionRepository;
        this.reservationRepository = null;
    }
    
    /**
     * Constructs a new TableAvailabilityService with full reservation integration.
     * 
     * @param tableRepository the table repository
     * @param sectionRepository the section repository
     * @param reservationRepository the reservation repository
     */
    public TableAvailabilityService(TableRepository tableRepository, 
                                     SectionRepository sectionRepository,
                                     ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.sectionRepository = sectionRepository;
        this.reservationRepository = reservationRepository;
    }
    
    /**
     * Checks if a table is available (active and not reserved).
     * 
     * @param tableId the table ID to check
     * @param requestedDateTime the date and time for the reservation
     * @return true if the table is available
     * @throws RepositoryException if check fails
     */
    public boolean isTableAvailable(Long tableId, LocalDateTime requestedDateTime) 
            throws RepositoryException {
        logger.debug("Checking availability for table {} at {}", tableId, requestedDateTime);
        
        Optional<Table> tableOpt = tableRepository.findById(tableId);
        if (!tableOpt.isPresent()) {
            logger.warn("Table not found: {}", tableId);
            return false;
        }
        
        Table table = tableOpt.get();
        
        // Check 1: Table must be active
        if (!table.isActive()) {
            logger.debug("Table {} is not active", tableId);
            return false;
        }
        
        // Check 2: Table must not have conflicting reservations
        if (reservationRepository != null) {
            boolean hasConflictingReservation = reservationRepository
                .hasConflictingReservation(tableId, requestedDateTime, DEFAULT_DURATION_MINUTES);
            if (hasConflictingReservation) {
                logger.debug("Table {} has conflicting reservation at {}", tableId, requestedDateTime);
                return false;
            }
        }
        
        logger.debug("Table {} is available", tableId);
        return true;
    }
    
    /**
     * Gets all available tables for a given datetime and party size.
     * Returns tables that are:
     * 1. Active
     * 2. Have capacity >= party size
     * 3. Not reserved at the requested time
     * 
     * @param restaurantId the restaurant ID
     * @param requestedDateTime the requested date and time
     * @param partySize the number of people
     * @return list of available tables
     * @throws RepositoryException if retrieval fails
     */
    public List<Table> getAvailableTables(Long restaurantId, LocalDateTime requestedDateTime, 
                                           Integer partySize) throws RepositoryException {
        logger.info("Finding available tables for restaurant {} at {} for {} people", 
                   restaurantId, requestedDateTime, partySize);
        
        // Get all sections for the restaurant
        List<Section> sections = sectionRepository.findByRestaurantId(restaurantId);
        
        List<Table> availableTables = new ArrayList<>();
        
        for (Section section : sections) {
            // Get all active tables in this section
            List<Table> activeTables = tableRepository.findActiveTablesBySectionId(section.getSectionId());
            
            // Filter by capacity
            List<Table> suitableTables = activeTables.stream()
                    .filter(table -> table.getCapacity() != null && table.getCapacity() >= partySize)
                    .collect(Collectors.toList());
            
            // Filter by reservation conflicts
            if (reservationRepository != null) {
                for (Table table : suitableTables) {
                    if (!reservationRepository.hasConflictingReservation(
                            table.getTableId(), requestedDateTime, DEFAULT_DURATION_MINUTES)) {
                        availableTables.add(table);
                    }
                }
            } else {
                // No reservation checking available
                availableTables.addAll(suitableTables);
            }
        }
        
        logger.info("Found {} available tables", availableTables.size());
        return availableTables;
    }
    
    /**
     * Gets available tables in a specific section.
     * 
     * @param sectionId the section ID
     * @param requestedDateTime the requested date and time
     * @param partySize the number of people
     * @return list of available tables in the section
     * @throws RepositoryException if retrieval fails
     */
    public List<Table> getAvailableTablesInSection(Long sectionId, LocalDateTime requestedDateTime,
                                                     Integer partySize) throws RepositoryException {
        logger.debug("Finding available tables in section {} for {} people", sectionId, partySize);
        
        // Get all active tables in this section
        List<Table> activeTables = tableRepository.findActiveTablesBySectionId(sectionId);
        
        // Filter by capacity
        List<Table> suitableTables = activeTables.stream()
                .filter(table -> table.getCapacity() != null && table.getCapacity() >= partySize)
                .collect(Collectors.toList());
        
        // Filter by reservation conflicts
        if (reservationRepository != null) {
            return suitableTables.stream()
                    .filter(table -> {
                        try {
                            return !reservationRepository.hasConflictingReservation(
                                    table.getTableId(), requestedDateTime, DEFAULT_DURATION_MINUTES);
                        } catch (RepositoryException e) {
                            logger.warn("Error checking conflicts for table {}", table.getTableId(), e);
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }
        
        return suitableTables;
    }
    
    /**
     * Validates a table assignment for a reservation.
     * Checks:
     * 1. Table exists
     * 2. Table is active
     * 3. Table has sufficient capacity
     * 4. Table is available at requested time (future implementation)
     * 
     * @param tableId the table ID
     * @param requestedDateTime the requested date and time
     * @param partySize the party size
     * @return true if assignment is valid
     * @throws RepositoryException if validation fails
     */
    public boolean validateTableAssignment(Long tableId, LocalDateTime requestedDateTime, 
                                            Integer partySize) throws RepositoryException {
        logger.debug("Validating table assignment: table={}, partySize={}", tableId, partySize);
        
        // Check table exists
        Optional<Table> tableOpt = tableRepository.findById(tableId);
        if (!tableOpt.isPresent()) {
            logger.warn("Table not found: {}", tableId);
            return false;
        }
        
        Table table = tableOpt.get();
        
        // Check table is active
        if (!table.isActive()) {
            logger.warn("Table {} is not active", tableId);
            return false;
        }
        
        // Check capacity
        if (table.getCapacity() == null || table.getCapacity() < partySize) {
            logger.warn("Table {} capacity ({}) insufficient for party size {}", 
                       tableId, table.getCapacity(), partySize);
            return false;
        }
        
        // Check for reservation conflicts
        if (reservationRepository != null) {
            if (reservationRepository.hasConflictingReservation(
                    tableId, requestedDateTime, DEFAULT_DURATION_MINUTES)) {
                logger.warn("Table {} has conflicting reservation", tableId);
                return false;
            }
        }
        
        logger.debug("Table assignment valid");
        return true;
    }
    
    /**
     * Gets the optimal table for a party size.
     * Selects the smallest table that can accommodate the party.
     * 
     * @param restaurantId the restaurant ID
     * @param requestedDateTime the requested date and time
     * @param partySize the party size
     * @return optional containing the best table if found
     * @throws RepositoryException if retrieval fails
     */
    public Optional<Table> getOptimalTable(Long restaurantId, LocalDateTime requestedDateTime,
                                            Integer partySize) throws RepositoryException {
        logger.debug("Finding optimal table for {} people", partySize);
        
        List<Table> availableTables = getAvailableTables(restaurantId, requestedDateTime, partySize);
        
        if (availableTables.isEmpty()) {
            return Optional.empty();
        }
        
        // Sort by capacity (ascending) to get smallest suitable table
        availableTables.sort((t1, t2) -> Integer.compare(t1.getCapacity(), t2.getCapacity()));
        
        Table optimal = availableTables.get(0);
        logger.debug("Optimal table: {} with capacity {}", optimal.getTableId(), optimal.getCapacity());
        
        return Optional.of(optimal);
    }
    
    /**
     * Checks capacity availability across the restaurant.
     * Returns the total available capacity at the requested time.
     * 
     * @param restaurantId the restaurant ID
     * @param requestedDateTime the requested date and time
     * @return the total available seating capacity
     * @throws RepositoryException if check fails
     */
    public int getAvailableCapacity(Long restaurantId, LocalDateTime requestedDateTime) 
            throws RepositoryException {
        List<Table> availableTables = getAvailableTables(restaurantId, requestedDateTime, 1);
        
        int totalCapacity = availableTables.stream()
                .mapToInt(table -> table.getCapacity() != null ? table.getCapacity() : 0)
                .sum();
        
        logger.debug("Total available capacity: {} seats", totalCapacity);
        return totalCapacity;
    }
    
    /**
     * Suggests alternative tables if the requested table is not available.
     * Returns tables with similar capacity in the same section first, then other sections.
     * 
     * @param tableId the originally requested table
     * @param requestedDateTime the requested date and time
     * @param partySize the party size
     * @return list of alternative tables
     * @throws RepositoryException if retrieval fails
     */
    public List<Table> suggestAlternativeTables(Long tableId, LocalDateTime requestedDateTime,
                                                 Integer partySize) throws RepositoryException {
        logger.debug("Suggesting alternatives for table {}", tableId);
        
        Optional<Table> originalTableOpt = tableRepository.findById(tableId);
        if (!originalTableOpt.isPresent()) {
            // If original table doesn't exist, return all available tables
            return getAvailableTables(getSectionRestaurantId(tableId), requestedDateTime, partySize);
        }
        
        Table originalTable = originalTableOpt.get();
        Long sectionId = originalTable.getSectionId();
        
        // First, try tables in the same section
        List<Table> sameSectionTables = getAvailableTablesInSection(sectionId, requestedDateTime, partySize);
        sameSectionTables.removeIf(table -> table.getTableId().equals(tableId));
        
        // If no alternatives in same section, look in other sections
        if (sameSectionTables.isEmpty()) {
            Section section = sectionRepository.findById(sectionId).orElse(null);
            if (section != null) {
                List<Table> otherTables = getAvailableTables(section.getRestaurantId(), 
                                                             requestedDateTime, partySize);
                otherTables.removeIf(table -> table.getSectionId().equals(sectionId));
                return otherTables;
            }
        }
        
        return sameSectionTables;
    }
    
    /**
     * Helper method to get restaurant ID from a table.
     * TODO: Remove this when proper restaurant lookup is available
     */
    private Long getSectionRestaurantId(Long tableId) throws RepositoryException {
        Optional<Table> tableOpt = tableRepository.findById(tableId);
        if (tableOpt.isPresent()) {
            Long sectionId = tableOpt.get().getSectionId();
            Optional<Section> sectionOpt = sectionRepository.findById(sectionId);
            if (sectionOpt.isPresent()) {
                return sectionOpt.get().getRestaurantId();
            }
        }
        return null;
    }
}

