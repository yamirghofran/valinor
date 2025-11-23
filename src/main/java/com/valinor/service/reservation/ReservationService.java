package com.valinor.service.reservation;

import com.valinor.service.dto.reservation.CreateReservationRequest;
import com.valinor.service.dto.reservation.ReservationResponse;
import com.valinor.service.dto.reservation.UpdateReservationRequest;
import com.valinor.domain.model.Customer;
import com.valinor.domain.model.Reservation;
import com.valinor.domain.enums.ReservationStatus;
import com.valinor.domain.model.Restaurant;
import com.valinor.domain.model.Table;
import com.valinor.exception.InsufficientCapacityException;
import com.valinor.exception.ReservationConflictException;
import com.valinor.exception.ReservationException;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.CustomerRepository;
import com.valinor.repository.ReservationRepository;
import com.valinor.repository.RestaurantRepository;
import com.valinor.repository.TableRepository;
import com.valinor.service.restaurant.TableAvailabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing reservations.
 * Provides business logic for reservation CRUD operations, validation, and conflict detection.
 */
public class ReservationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    
    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final TableRepository tableRepository;
    private final TableAvailabilityService tableAvailabilityService;
    
    // Default reservation duration in minutes (2 hours)
    private static final int DEFAULT_DURATION_MINUTES = 120;
    
    /**
     * Constructs a new ReservationService.
     * 
     * @param reservationRepository the reservation repository
     * @param customerRepository the customer repository
     * @param restaurantRepository the restaurant repository
     * @param tableRepository the table repository
     * @param tableAvailabilityService the table availability service
     */
    public ReservationService(ReservationRepository reservationRepository,
                             CustomerRepository customerRepository,
                             RestaurantRepository restaurantRepository,
                             TableRepository tableRepository,
                             TableAvailabilityService tableAvailabilityService) {
        if (reservationRepository == null) {
            throw new IllegalArgumentException("ReservationRepository cannot be null");
        }
        if (customerRepository == null) {
            throw new IllegalArgumentException("CustomerRepository cannot be null");
        }
        if (restaurantRepository == null) {
            throw new IllegalArgumentException("RestaurantRepository cannot be null");
        }
        if (tableRepository == null) {
            throw new IllegalArgumentException("TableRepository cannot be null");
        }
        if (tableAvailabilityService == null) {
            throw new IllegalArgumentException("TableAvailabilityService cannot be null");
        }
        
        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.tableRepository = tableRepository;
        this.tableAvailabilityService = tableAvailabilityService;
    }
    
    /**
     * Creates a new reservation with a specific table.
     * 
     * @param request the reservation creation request
     * @return the created reservation
     * @throws ReservationException if creation fails or validation fails
     */
    public Reservation createReservation(CreateReservationRequest request) throws ReservationException {
        if (request == null) {
            throw new IllegalArgumentException("Reservation create request cannot be null");
        }
        
        if (request.getTableId() == null) {
            throw new ReservationException("Table ID is required. Use createReservationWithAutoAssignment for automatic table selection.");
        }
        
        try {
            // 1. Validate request
            validateCreateRequest(request);
            
            // 2. Verify customer exists
            Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ReservationException("Customer not found with ID: " + request.getCustomerId()));
            
            // 3. Verify restaurant exists
            restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ReservationException("Restaurant not found with ID: " + request.getRestaurantId()));
            
            // 4. Verify table exists and is active
            Table table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ReservationException("Table not found with ID: " + request.getTableId()));
            
            if (!table.isActive()) {
                throw new ReservationException("Table is not active");
            }
            
            // 5. Validate capacity
            if (table.getCapacity() < request.getPartySize()) {
                throw new InsufficientCapacityException(
                    "Table capacity (" + table.getCapacity() + ") insufficient for party size (" + request.getPartySize() + ")",
                    request.getPartySize(),
                    table.getCapacity()
                );
            }
            
            // 6. Check for conflicts
            boolean hasConflict = reservationRepository.hasConflictingReservation(
                request.getTableId(),
                request.getReservationDatetime(),
                DEFAULT_DURATION_MINUTES
            );
            
            if (hasConflict) {
                throw new ReservationConflictException(
                    "Table is already reserved at the requested time",
                    request.getTableId(),
                    request.getReservationDatetime()
                );
            }
            
            // 7. Create and save reservation
            Reservation reservation = new Reservation(
                request.getCustomerId(),
                request.getRestaurantId(),
                request.getTableId(),
                request.getPartySize(),
                request.getReservationDatetime()
            );
            
            reservation.setSpecialRequests(request.getSpecialRequests());
            reservation.setStatus(ReservationStatus.CONFIRMED);
            
            reservation = reservationRepository.save(reservation);
            
            logger.info("Created reservation {} for customer {} at table {} on {}", 
                       reservation.getReservationId(), customer.getFullName(), 
                       table.getTableNumber(), reservation.getReservationDatetime());
            
            return reservation;
            
        } catch (ReservationException e) {
            throw e;
        } catch (RepositoryException e) {
            logger.error("Repository error during reservation creation", e);
            throw new ReservationException("Failed to create reservation", e);
        }
    }
    
    /**
     * Creates a new reservation with automatic table assignment.
     * 
     * @param request the reservation creation request (tableId can be null)
     * @return the created reservation
     * @throws ReservationException if creation fails or no suitable table found
     */
    public Reservation createReservationWithAutoAssignment(CreateReservationRequest request) throws ReservationException {
        if (request == null) {
            throw new IllegalArgumentException("Reservation create request cannot be null");
        }
        
        try {
            // Use TableAvailabilityService to find optimal table
            Optional<Table> optimalTable = tableAvailabilityService.getOptimalTable(
                request.getRestaurantId(),
                request.getReservationDatetime(),
                request.getPartySize()
            );
            
            if (!optimalTable.isPresent()) {
                throw new ReservationException("No available tables for the requested time and party size");
            }
            
            // Set the table and create reservation
            request.setTableId(optimalTable.get().getTableId());
            return createReservation(request);
            
        } catch (RepositoryException e) {
            logger.error("Repository error during auto-assignment", e);
            throw new ReservationException("Failed to find available table", e);
        }
    }
    
    /**
     * Updates an existing reservation.
     * 
     * @param reservationId the reservation ID to update
     * @param request the update request
     * @return the updated reservation
     * @throws ReservationException if update fails
     */
    public Reservation updateReservation(Long reservationId, UpdateReservationRequest request) throws ReservationException {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }
        
        if (request == null) {
            throw new IllegalArgumentException("Reservation update request cannot be null");
        }
        
        if (!request.hasUpdates()) {
            throw new ReservationException("No fields to update");
        }
        
        try {
            // Find existing reservation
            Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
            if (!reservationOpt.isPresent()) {
                throw new ReservationException("Reservation not found with ID: " + reservationId);
            }
            
            Reservation reservation = reservationOpt.get();
            
            // Validate and apply updates
            if (request.getTableId() != null) {
                Table newTable = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new ReservationException("Table not found with ID: " + request.getTableId()));
                
                if (!newTable.isActive()) {
                    throw new ReservationException("Table is not active");
                }
                
                // Check capacity with new or existing party size
                Integer partySize = request.getPartySize() != null ? request.getPartySize() : reservation.getPartySize();
                if (newTable.getCapacity() < partySize) {
                    throw new InsufficientCapacityException(
                        "Table capacity insufficient for party size",
                        partySize,
                        newTable.getCapacity()
                    );
                }
                
                // Check for conflicts on new table
                LocalDateTime datetime = request.getReservationDatetime() != null ? 
                                        request.getReservationDatetime() : reservation.getReservationDatetime();
                boolean hasConflict = reservationRepository.hasConflictingReservation(
                    request.getTableId(), datetime, DEFAULT_DURATION_MINUTES, reservationId);
                
                if (hasConflict) {
                    throw new ReservationConflictException("Table is already reserved at the requested time");
                }
                
                reservation.setTableId(request.getTableId());
            }
            
            if (request.getPartySize() != null) {
                // Validate capacity with current or new table
                Table table = tableRepository.findById(reservation.getTableId())
                    .orElseThrow(() -> new ReservationException("Table not found"));
                
                if (table.getCapacity() < request.getPartySize()) {
                    throw new InsufficientCapacityException(
                        "Table capacity insufficient for new party size",
                        request.getPartySize(),
                        table.getCapacity()
                    );
                }
                
                reservation.setPartySize(request.getPartySize());
            }
            
            if (request.getReservationDatetime() != null) {
                // Check for conflicts at new time
                boolean hasConflict = reservationRepository.hasConflictingReservation(
                    reservation.getTableId(), request.getReservationDatetime(), 
                    DEFAULT_DURATION_MINUTES, reservationId);
                
                if (hasConflict) {
                    throw new ReservationConflictException("Table is already reserved at the requested time");
                }
                
                reservation.setReservationDatetime(request.getReservationDatetime());
            }
            
            if (request.getSpecialRequests() != null) {
                reservation.setSpecialRequests(request.getSpecialRequests());
            }
            
            // Mark as updated
            reservation.markAsUpdated();
            
            // Update reservation
            reservation = reservationRepository.update(reservation);
            
            logger.info("Updated reservation {}", reservationId);
            return reservation;
            
        } catch (ReservationException e) {
            throw e;
        } catch (RepositoryException e) {
            logger.error("Repository error during reservation update", e);
            throw new ReservationException("Failed to update reservation", e);
        }
    }
    
    /**
     * Cancels a reservation.
     * 
     * @param reservationId the reservation ID to cancel
     * @return the cancelled reservation
     * @throws ReservationException if cancellation fails
     */
    public Reservation cancelReservation(Long reservationId) throws ReservationException {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }
        
        try {
            Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
            if (!reservationOpt.isPresent()) {
                throw new ReservationException("Reservation not found with ID: " + reservationId);
            }
            
            Reservation reservation = reservationOpt.get();
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservation.markAsUpdated();
            
            reservation = reservationRepository.update(reservation);
            
            logger.info("Cancelled reservation {}", reservationId);
            return reservation;
            
        } catch (RepositoryException e) {
            logger.error("Repository error during reservation cancellation", e);
            throw new ReservationException("Failed to cancel reservation", e);
        }
    }
    
    /**
     * Marks a reservation as completed.
     * 
     * @param reservationId the reservation ID
     * @return the updated reservation
     * @throws ReservationException if update fails
     */
    public Reservation markAsCompleted(Long reservationId) throws ReservationException {
        return updateReservationStatus(reservationId, ReservationStatus.COMPLETED);
    }
    
    /**
     * Marks a reservation as no-show.
     * 
     * @param reservationId the reservation ID
     * @return the updated reservation
     * @throws ReservationException if update fails
     */
    public Reservation markAsNoShow(Long reservationId) throws ReservationException {
        return updateReservationStatus(reservationId, ReservationStatus.NO_SHOW);
    }
    
    /**
     * Gets a reservation by ID.
     * 
     * @param reservationId the reservation ID
     * @return optional containing the reservation if found
     * @throws ReservationException if retrieval fails
     */
    public Optional<Reservation> getReservationById(Long reservationId) throws ReservationException {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }
        
        try {
            return reservationRepository.findById(reservationId);
        } catch (RepositoryException e) {
            logger.error("Repository error during reservation retrieval", e);
            throw new ReservationException("Failed to retrieve reservation", e);
        }
    }
    
    /**
     * Gets all reservations for a customer.
     * 
     * @param customerId the customer ID
     * @return list of reservations
     * @throws ReservationException if retrieval fails
     */
    public List<Reservation> getReservationsByCustomer(Long customerId) throws ReservationException {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        
        try {
            return reservationRepository.findByCustomerId(customerId);
        } catch (RepositoryException e) {
            logger.error("Repository error during reservations retrieval", e);
            throw new ReservationException("Failed to retrieve reservations", e);
        }
    }
    
    /**
     * Gets all reservations for a restaurant on a specific date.
     * 
     * @param restaurantId the restaurant ID
     * @param date the date
     * @return list of reservations
     * @throws ReservationException if retrieval fails
     */
    public List<Reservation> getReservationsByRestaurant(Long restaurantId, LocalDate date) throws ReservationException {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        
        try {
            return reservationRepository.findByDate(restaurantId, date);
        } catch (RepositoryException e) {
            logger.error("Repository error during reservations retrieval", e);
            throw new ReservationException("Failed to retrieve reservations", e);
        }
    }
    
    /**
     * Gets all active reservations for a restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @return list of active reservations
     * @throws ReservationException if retrieval fails
     */
    public List<Reservation> getActiveReservations(Long restaurantId) throws ReservationException {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID cannot be null");
        }
        
        try {
            return reservationRepository.findActiveReservations(restaurantId, LocalDateTime.now());
        } catch (RepositoryException e) {
            logger.error("Repository error during reservations retrieval", e);
            throw new ReservationException("Failed to retrieve active reservations", e);
        }
    }
    
    /**
     * Converts a reservation to a response DTO.
     * 
     * @param reservation the reservation entity
     * @return the reservation response DTO
     */
    public ReservationResponse toResponse(Reservation reservation) {
        ReservationResponse response = ReservationResponse.fromReservation(reservation);
        
        // Enrich with related entity information
        try {
            Optional<Customer> customerOpt = customerRepository.findById(reservation.getCustomerId());
            if (customerOpt.isPresent()) {
                response.setCustomerName(customerOpt.get().getFullName());
            }
            
            Optional<Restaurant> restaurantOpt = restaurantRepository.findById(reservation.getRestaurantId());
            if (restaurantOpt.isPresent()) {
                response.setRestaurantName(restaurantOpt.get().getName());
            }
            
            Optional<Table> tableOpt = tableRepository.findById(reservation.getTableId());
            if (tableOpt.isPresent()) {
                response.setTableNumber(tableOpt.get().getTableNumber());
            }
        } catch (RepositoryException e) {
            logger.warn("Failed to enrich reservation response", e);
        }
        
        return response;
    }
    
    /**
     * Converts a list of reservations to response DTOs.
     * 
     * @param reservations the list of reservation entities
     * @return the list of reservation response DTOs
     */
    public List<ReservationResponse> toResponses(List<Reservation> reservations) {
        return reservations.stream()
                          .map(this::toResponse)
                          .collect(Collectors.toList());
    }
    
    /**
     * Updates a reservation's status.
     * 
     * @param reservationId the reservation ID
     * @param status the new status
     * @return the updated reservation
     * @throws ReservationException if update fails
     */
    private Reservation updateReservationStatus(Long reservationId, ReservationStatus status) throws ReservationException {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }
        
        try {
            Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
            if (!reservationOpt.isPresent()) {
                throw new ReservationException("Reservation not found with ID: " + reservationId);
            }
            
            Reservation reservation = reservationOpt.get();
            reservation.setStatus(status);
            
            reservation = reservationRepository.update(reservation);
            
            logger.info("Updated reservation {} status to {}", reservationId, status);
            return reservation;
            
        } catch (RepositoryException e) {
            logger.error("Repository error during status update", e);
            throw new ReservationException("Failed to update reservation status", e);
        }
    }
    
    /**
     * Validates a reservation creation request.
     * 
     * @param request the request to validate
     * @throws ReservationException if validation fails
     */
    private void validateCreateRequest(CreateReservationRequest request) throws ReservationException {
        if (request.getCustomerId() == null) {
            throw new ReservationException("Customer ID is required");
        }
        
        if (request.getRestaurantId() == null) {
            throw new ReservationException("Restaurant ID is required");
        }
        
        if (request.getPartySize() == null || request.getPartySize() <= 0) {
            throw new ReservationException("Party size must be greater than 0");
        }
        
        if (request.getReservationDatetime() == null) {
            throw new ReservationException("Reservation date and time is required");
        }
        
        // Validate reservation is in the future (with some tolerance for testing)
        if (request.getReservationDatetime().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new ReservationException("Reservation time must be in the future");
        }
    }
}
