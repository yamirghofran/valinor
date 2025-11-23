package com.valinor.demo;

import com.valinor.service.dto.reservation.CreateReservationRequest;
import com.valinor.service.dto.reservation.ReservationResponse;
import com.valinor.service.dto.reservation.UpdateReservationRequest;
import com.valinor.domain.model.Reservation;
import com.valinor.domain.enums.ReservationStatus;
import com.valinor.exception.ReservationException;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.*;
import com.valinor.service.reservation.ReservationService;
import com.valinor.service.restaurant.TableAvailabilityService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Demonstration of the ReservationService functionality.
 * Shows how to create, read, update, and cancel reservations.
 */
public class ReservationServiceDemo {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Reservation Service Demo ===\n");
            
            // Initialize repositories
            CustomerRepository customerRepository = new CustomerRepository("data/customers.csv");
            RestaurantRepository restaurantRepository = new RestaurantRepository("data/restaurants.csv");
            SectionRepository sectionRepository = new SectionRepository("data/sections.csv");
            TableRepository tableRepository = new TableRepository("data/tables.csv");
            ReservationRepository reservationRepository = new ReservationRepository("data/reservations.csv");
            
            // Initialize services
            TableAvailabilityService availabilityService = new TableAvailabilityService(
                tableRepository, sectionRepository);
            
            ReservationService reservationService = new ReservationService(
                reservationRepository,
                customerRepository,
                restaurantRepository,
                tableRepository,
                availabilityService
            );
            
            // Demo 1: List existing reservations
            System.out.println("1. Listing existing reservations:");
            System.out.println("-----------------------------------");
            List<Reservation> allReservations = reservationRepository.findAll();
            for (Reservation reservation : allReservations) {
                System.out.println("  - Reservation #" + reservation.getReservationId() + 
                                 " | Customer: " + reservation.getCustomerId() +
                                 " | Table: " + reservation.getTableId() +
                                 " | Party: " + reservation.getPartySize() +
                                 " | Time: " + reservation.getReservationDatetime() +
                                 " | Status: " + reservation.getStatus());
            }
            System.out.println();
            
            // Demo 2: Create a new reservation with specific table
            System.out.println("2. Creating a new reservation with specific table:");
            System.out.println("-----------------------------------");
            CreateReservationRequest createRequest = new CreateReservationRequest(
                1L,  // Customer ID (John Doe)
                1L,  // Restaurant ID
                2L,  // Table ID
                3,   // Party size
                LocalDateTime.of(2025, 11, 22, 19, 0), // Nov 22, 2025 at 7:00 PM
                "Window seat preferred"
            );
            
            Reservation newReservation = reservationService.createReservation(createRequest);
            System.out.println("  Created reservation #" + newReservation.getReservationId());
            System.out.println("  Customer: " + newReservation.getCustomerId());
            System.out.println("  Table: " + newReservation.getTableId());
            System.out.println("  Date/Time: " + newReservation.getReservationDatetime());
            System.out.println("  Status: " + newReservation.getStatus());
            System.out.println();
            
            // Demo 3: Create reservation with auto-assignment
            System.out.println("3. Creating reservation with auto table assignment:");
            System.out.println("-----------------------------------");
            CreateReservationRequest autoRequest = new CreateReservationRequest(
                2L,  // Customer ID (Jane Smith)
                1L,  // Restaurant ID
                4,   // Party size
                LocalDateTime.of(2025, 11, 23, 18, 30) // Nov 23, 2025 at 6:30 PM
            );
            
            Reservation autoReservation = reservationService.createReservationWithAutoAssignment(autoRequest);
            System.out.println("  Auto-assigned to table: " + autoReservation.getTableId());
            System.out.println("  Reservation ID: " + autoReservation.getReservationId());
            System.out.println();
            
            // Demo 4: Get reservations by customer
            System.out.println("4. Getting reservations for customer #1:");
            System.out.println("-----------------------------------");
            List<Reservation> customerReservations = reservationService.getReservationsByCustomer(1L);
            System.out.println("  Found " + customerReservations.size() + " reservation(s)");
            for (Reservation res : customerReservations) {
                System.out.println("    - " + res.getReservationDatetime() + " at table " + res.getTableId());
            }
            System.out.println();
            
            // Demo 5: Get reservations by date
            System.out.println("5. Getting reservations for Nov 20, 2025:");
            System.out.println("-----------------------------------");
            List<Reservation> dateReservations = reservationService.getReservationsByRestaurant(
                1L, LocalDate.of(2025, 11, 20));
            System.out.println("  Found " + dateReservations.size() + " reservation(s)");
            for (Reservation res : dateReservations) {
                System.out.println("    - Table " + res.getTableId() + 
                                 " at " + res.getReservationDatetime().toLocalTime() +
                                 " for party of " + res.getPartySize());
            }
            System.out.println();
            
            // Demo 6: Update a reservation
            System.out.println("6. Updating a reservation:");
            System.out.println("-----------------------------------");
            UpdateReservationRequest updateRequest = new UpdateReservationRequest();
            updateRequest.setPartySize(4); // Change from 3 to 4
            updateRequest.setSpecialRequests("Birthday celebration - need cake");
            
            Reservation updatedReservation = reservationService.updateReservation(
                newReservation.getReservationId(), updateRequest);
            System.out.println("  Updated reservation #" + updatedReservation.getReservationId());
            System.out.println("  New party size: " + updatedReservation.getPartySize());
            System.out.println("  Special requests: " + updatedReservation.getSpecialRequests());
            System.out.println();
            
            // Demo 7: Convert to response DTOs
            System.out.println("7. Converting to response DTOs:");
            System.out.println("-----------------------------------");
            ReservationResponse response = reservationService.toResponse(newReservation);
            System.out.println("  Reservation #" + response.getReservationId());
            System.out.println("  Customer: " + response.getCustomerName());
            System.out.println("  Restaurant: " + response.getRestaurantName());
            System.out.println("  Table: " + response.getTableNumber());
            System.out.println();
            
            // Demo 8: Get active reservations
            System.out.println("8. Getting active reservations:");
            System.out.println("-----------------------------------");
            List<Reservation> activeReservations = reservationService.getActiveReservations(1L);
            System.out.println("  Found " + activeReservations.size() + " active reservation(s)");
            System.out.println();
            
            // Demo 9: Cancel a reservation
            System.out.println("9. Cancelling a reservation:");
            System.out.println("-----------------------------------");
            Reservation cancelledReservation = reservationService.cancelReservation(
                newReservation.getReservationId());
            System.out.println("  Cancelled reservation #" + cancelledReservation.getReservationId());
            System.out.println("  New status: " + cancelledReservation.getStatus());
            System.out.println();
            
            // Demo 10: Error handling - conflicting reservation
            System.out.println("10. Error handling - conflicting reservation:");
            System.out.println("-----------------------------------");
            try {
                CreateReservationRequest conflictRequest = new CreateReservationRequest(
                    2L,
                    1L,
                    1L,  // Table 1 is already reserved at 7:00 PM on Nov 20
                    2,
                    LocalDateTime.of(2025, 11, 20, 19, 30) // 30 minutes after existing
                );
                reservationService.createReservation(conflictRequest);
            } catch (ReservationException e) {
                System.out.println("  Expected error: " + e.getMessage());
            }
            System.out.println();
            
            // Demo 11: Mark as completed
            System.out.println("11. Marking reservation as completed:");
            System.out.println("-----------------------------------");
            Reservation completedReservation = reservationService.markAsCompleted(autoReservation.getReservationId());
            System.out.println("  Reservation #" + completedReservation.getReservationId() + 
                             " marked as " + completedReservation.getStatus());
            System.out.println();
            
            System.out.println("=== Demo Complete ===");
            
        } catch (RepositoryException e) {
            System.err.println("Repository error: " + e.getMessage());
            e.printStackTrace();
        } catch (ReservationException e) {
            System.err.println("Reservation service error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
