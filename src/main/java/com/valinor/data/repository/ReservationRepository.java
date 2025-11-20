package com.valinor.data.repository;

import com.valinor.data.entity.Reservation;
import com.valinor.data.entity.ReservationStatus;
import com.valinor.data.exception.RepositoryException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSV-based repository for Reservation entities.
 * Provides CRUD operations and conflict detection for reservations.
 */
public class ReservationRepository extends AbstractCsvRepository<Reservation, Long> {
    
    // Default reservation duration in minutes (2 hours)
    private static final int DEFAULT_DURATION_MINUTES = 120;
    
    /**
     * Constructs a new ReservationRepository.
     * 
     * @param filePath the path to the reservations CSV file
     * @throws RepositoryException if initialization fails
     */
    public ReservationRepository(String filePath) throws RepositoryException {
        super(filePath, new ReservationEntityMapper());
    }
    
    /**
     * Finds all reservations for a specific customer.
     * 
     * @param customerId the customer ID
     * @return list of reservations for the customer
     * @throws RepositoryException if search fails
     */
    public List<Reservation> findByCustomerId(Long customerId) throws RepositoryException {
        return findByField("customer_id", customerId);
    }
    
    /**
     * Finds all reservations for a specific restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @return list of reservations for the restaurant
     * @throws RepositoryException if search fails
     */
    public List<Reservation> findByRestaurantId(Long restaurantId) throws RepositoryException {
        return findByField("restaurant_id", restaurantId);
    }
    
    /**
     * Finds all reservations for a specific table.
     * 
     * @param tableId the table ID
     * @return list of reservations for the table
     * @throws RepositoryException if search fails
     */
    public List<Reservation> findByTableId(Long tableId) throws RepositoryException {
        return findByField("table_id", tableId);
    }
    
    /**
     * Finds all reservations for a specific restaurant on a specific date.
     * 
     * @param restaurantId the restaurant ID
     * @param date the date
     * @return list of reservations on the specified date
     * @throws RepositoryException if search fails
     */
    public List<Reservation> findByDate(Long restaurantId, LocalDate date) throws RepositoryException {
        return findWhere(reservation -> 
            reservation.getRestaurantId().equals(restaurantId) &&
            reservation.getReservationDatetime().toLocalDate().equals(date)
        );
    }
    
    /**
     * Finds all reservations for a table on a specific date.
     * 
     * @param tableId the table ID
     * @param date the date
     * @return list of reservations for the table on the specified date
     * @throws RepositoryException if search fails
     */
    public List<Reservation> findByTableIdAndDate(Long tableId, LocalDate date) throws RepositoryException {
        return findWhere(reservation -> 
            reservation.getTableId().equals(tableId) &&
            reservation.getReservationDatetime().toLocalDate().equals(date)
        );
    }
    
    /**
     * Finds all reservations within a date range for a restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @param startDateTime the start of the date range (inclusive)
     * @param endDateTime the end of the date range (inclusive)
     * @return list of reservations within the date range
     * @throws RepositoryException if search fails
     */
    public List<Reservation> findByDateRange(Long restaurantId, LocalDateTime startDateTime, 
                                             LocalDateTime endDateTime) throws RepositoryException {
        return findWhere(reservation -> 
            reservation.getRestaurantId().equals(restaurantId) &&
            !reservation.getReservationDatetime().isBefore(startDateTime) &&
            !reservation.getReservationDatetime().isAfter(endDateTime)
        );
    }
    
    /**
     * Finds all reservations with a specific status.
     * 
     * @param status the reservation status
     * @return list of reservations with the specified status
     * @throws RepositoryException if search fails
     */
    public List<Reservation> findByStatus(ReservationStatus status) throws RepositoryException {
        return findWhere(reservation -> reservation.getStatus().equals(status));
    }
    
    /**
     * Finds all active (confirmed) reservations for a restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @param dateTime the date and time to check (typically current time)
     * @return list of active reservations
     * @throws RepositoryException if search fails
     */
    public List<Reservation> findActiveReservations(Long restaurantId, LocalDateTime dateTime) throws RepositoryException {
        return findWhere(reservation -> 
            reservation.getRestaurantId().equals(restaurantId) &&
            reservation.getStatus().equals(ReservationStatus.CONFIRMED) &&
            !reservation.getReservationDatetime().isBefore(dateTime)
        );
    }
    
    /**
     * Finds all confirmed reservations for a restaurant on a specific date.
     * 
     * @param restaurantId the restaurant ID
     * @param date the date
     * @return list of confirmed reservations
     * @throws RepositoryException if search fails
     */
    public List<Reservation> findConfirmedReservations(Long restaurantId, LocalDate date) throws RepositoryException {
        return findWhere(reservation -> 
            reservation.getRestaurantId().equals(restaurantId) &&
            reservation.getStatus().equals(ReservationStatus.CONFIRMED) &&
            reservation.getReservationDatetime().toLocalDate().equals(date)
        );
    }
    
    /**
     * Checks if a table has any conflicting reservations at the requested time.
     * This is the critical method for preventing double-booking.
     * 
     * Algorithm:
     * 1. Get all reservations for the table on the requested date
     * 2. For each non-cancelled reservation, check for time overlap
     * 3. Overlap occurs if: (StartA < EndB) AND (EndA > StartB)
     * 
     * @param tableId the table ID
     * @param requestedTime the requested reservation start time
     * @param durationMinutes the reservation duration in minutes
     * @return true if there is a conflicting reservation, false otherwise
     * @throws RepositoryException if check fails
     */
    public boolean hasConflictingReservation(Long tableId, LocalDateTime requestedTime, 
                                             int durationMinutes) throws RepositoryException {
        // Default duration if not specified or invalid
        if (durationMinutes <= 0) {
            durationMinutes = DEFAULT_DURATION_MINUTES;
        }
        
        LocalDateTime requestedEnd = requestedTime.plusMinutes(durationMinutes);
        
        // Get all reservations for this table on the same date
        List<Reservation> tableReservations = findByTableIdAndDate(tableId, requestedTime.toLocalDate());
        
        // Check each reservation for time overlap
        for (Reservation reservation : tableReservations) {
            // Skip cancelled reservations
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                continue;
            }
            
            LocalDateTime existingStart = reservation.getReservationDatetime();
            LocalDateTime existingEnd = existingStart.plusMinutes(DEFAULT_DURATION_MINUTES);
            
            // Check for overlap: (StartA < EndB) AND (EndA > StartB)
            boolean overlaps = requestedTime.isBefore(existingEnd) && 
                              requestedEnd.isAfter(existingStart);
            
            if (overlaps) {
                return true; // Conflict found
            }
        }
        
        return false; // No conflicts
    }
    
    /**
     * Checks if a table has any conflicting reservations, excluding a specific reservation.
     * Useful when updating an existing reservation.
     * 
     * @param tableId the table ID
     * @param requestedTime the requested reservation start time
     * @param durationMinutes the reservation duration in minutes
     * @param excludeReservationId the reservation ID to exclude from conflict check
     * @return true if there is a conflicting reservation, false otherwise
     * @throws RepositoryException if check fails
     */
    public boolean hasConflictingReservation(Long tableId, LocalDateTime requestedTime, 
                                             int durationMinutes, Long excludeReservationId) throws RepositoryException {
        // Default duration if not specified or invalid
        if (durationMinutes <= 0) {
            durationMinutes = DEFAULT_DURATION_MINUTES;
        }
        
        LocalDateTime requestedEnd = requestedTime.plusMinutes(durationMinutes);
        
        // Get all reservations for this table on the same date
        List<Reservation> tableReservations = findByTableIdAndDate(tableId, requestedTime.toLocalDate());
        
        // Check each reservation for time overlap
        for (Reservation reservation : tableReservations) {
            // Skip the reservation we're updating
            if (reservation.getReservationId().equals(excludeReservationId)) {
                continue;
            }
            
            // Skip cancelled reservations
            if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                continue;
            }
            
            LocalDateTime existingStart = reservation.getReservationDatetime();
            LocalDateTime existingEnd = existingStart.plusMinutes(DEFAULT_DURATION_MINUTES);
            
            // Check for overlap: (StartA < EndB) AND (EndA > StartB)
            boolean overlaps = requestedTime.isBefore(existingEnd) && 
                              requestedEnd.isAfter(existingStart);
            
            if (overlaps) {
                return true; // Conflict found
            }
        }
        
        return false; // No conflicts
    }
    
    /**
     * Counts the number of reservations for a restaurant on a specific date.
     * 
     * @param restaurantId the restaurant ID
     * @param date the date
     * @return the count of reservations
     * @throws RepositoryException if count fails
     */
    public int countByRestaurantAndDate(Long restaurantId, LocalDate date) throws RepositoryException {
        return findByDate(restaurantId, date).size();
    }
    
    /**
     * Deletes all reservations for a specific customer.
     * Useful for cascade deletions.
     * 
     * @param customerId the customer ID
     * @return the number of reservations deleted
     * @throws RepositoryException if deletion fails
     */
    public int deleteByCustomerId(Long customerId) throws RepositoryException {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("customer_id", customerId);
        return deleteByFields(criteria);
    }
    
    /**
     * Finds upcoming reservations for a customer.
     * 
     * @param customerId the customer ID
     * @param fromDateTime the start date/time (typically now)
     * @return list of upcoming reservations
     * @throws RepositoryException if search fails
     */
    public List<Reservation> findUpcomingReservations(Long customerId, LocalDateTime fromDateTime) throws RepositoryException {
        return findWhere(reservation -> 
            reservation.getCustomerId().equals(customerId) &&
            reservation.getStatus().equals(ReservationStatus.CONFIRMED) &&
            !reservation.getReservationDatetime().isBefore(fromDateTime)
        );
    }
    
    /**
     * Finds past reservations for a customer.
     * 
     * @param customerId the customer ID
     * @param beforeDateTime the cutoff date/time (typically now)
     * @return list of past reservations
     * @throws RepositoryException if search fails
     */
    public List<Reservation> findPastReservations(Long customerId, LocalDateTime beforeDateTime) throws RepositoryException {
        return findWhere(reservation -> 
            reservation.getCustomerId().equals(customerId) &&
            reservation.getReservationDatetime().isBefore(beforeDateTime)
        );
    }
    
    @Override
    protected boolean fieldValueMatches(Reservation entity, String fieldName, Object expectedValue) {
        if (entity == null || expectedValue == null) {
            return false;
        }
        
        try {
            switch (fieldName.toLowerCase()) {
                case "reservation_id":
                    return expectedValue.equals(entity.getReservationId());
                case "customer_id":
                    return expectedValue.equals(entity.getCustomerId());
                case "restaurant_id":
                    return expectedValue.equals(entity.getRestaurantId());
                case "table_id":
                    return expectedValue.equals(entity.getTableId());
                case "party_size":
                    return expectedValue.equals(entity.getPartySize());
                case "status":
                    if (expectedValue instanceof ReservationStatus) {
                        return expectedValue.equals(entity.getStatus());
                    } else if (expectedValue instanceof String) {
                        ReservationStatus status = ReservationStatus.fromValue((String) expectedValue);
                        return status != null && status.equals(entity.getStatus());
                    }
                    return false;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
