# Reservation Module - Quick Start Guide

## Overview

The Reservation Module provides complete reservation management for your restaurant system. This guide will help you get started quickly.

## Basic Usage

### 1. Initialize Repositories

```java
// Create repositories
CustomerRepository customerRepository = new CustomerRepository("data/customers.csv");
RestaurantRepository restaurantRepository = new RestaurantRepository("data/restaurants.csv");
SectionRepository sectionRepository = new SectionRepository("data/sections.csv");
TableRepository tableRepository = new TableRepository("data/tables.csv");
ReservationRepository reservationRepository = new ReservationRepository("data/reservations.csv");
```

### 2. Initialize Services

```java
// Create services
CustomerService customerService = new CustomerService(customerRepository);

TableAvailabilityService availabilityService = new TableAvailabilityService(
    tableRepository, sectionRepository, reservationRepository);

ReservationService reservationService = new ReservationService(
    reservationRepository,
    customerRepository,
    restaurantRepository,
    tableRepository,
    availabilityService
);
```

### 3. Create a Customer

```java
CreateCustomerRequest customerRequest = new CreateCustomerRequest(
    "Alice",                           // First name
    "Williams",                        // Last name
    "alice.williams@example.com",      // Email (must be unique)
    "555-0103",                        // Phone
    "Shellfish",                       // Allergies (optional)
    "Vegetarian preferences"           // Notes (optional)
);

Customer customer = customerService.createCustomer(customerRequest);
System.out.println("Created customer #" + customer.getCustomerId());
```

### 4. Create a Reservation (Specific Table)

```java
CreateReservationRequest reservationRequest = new CreateReservationRequest(
    customer.getCustomerId(),          // Customer ID
    1L,                                // Restaurant ID
    5L,                                // Table ID
    4,                                 // Party size
    LocalDateTime.of(2025, 11, 25, 19, 0), // Date/time
    "Birthday celebration"             // Special requests (optional)
);

Reservation reservation = reservationService.createReservation(reservationRequest);
System.out.println("Reservation #" + reservation.getReservationId() + " created!");
```

### 5. Create a Reservation (Auto-Assign Table)

```java
CreateReservationRequest autoRequest = new CreateReservationRequest(
    customer.getCustomerId(),
    1L,                                // Restaurant ID
    4,                                 // Party size
    LocalDateTime.of(2025, 11, 26, 18, 30) // Date/time
);
// Note: No table ID specified

Reservation autoReservation = reservationService
    .createReservationWithAutoAssignment(autoRequest);
    
System.out.println("Auto-assigned to table #" + autoReservation.getTableId());
```

### 6. Update a Reservation

```java
UpdateReservationRequest updateRequest = new UpdateReservationRequest();
updateRequest.setPartySize(6);                          // Change party size
updateRequest.setSpecialRequests("Need high chair");    // Update requests

Reservation updated = reservationService.updateReservation(
    reservation.getReservationId(), 
    updateRequest
);
```

### 7. Cancel a Reservation

```java
Reservation cancelled = reservationService.cancelReservation(
    reservation.getReservationId()
);
System.out.println("Status: " + cancelled.getStatus()); // CANCELLED
```

### 8. Query Reservations

```java
// Get all reservations for a customer
List<Reservation> customerReservations = reservationService
    .getReservationsByCustomer(customer.getCustomerId());

// Get all reservations for a specific date
List<Reservation> dateReservations = reservationService
    .getReservationsByRestaurant(1L, LocalDate.of(2025, 11, 25));

// Get all active reservations
List<Reservation> activeReservations = reservationService
    .getActiveReservations(1L);
```

### 9. Convert to Response DTOs

```java
// Single reservation
ReservationResponse response = reservationService.toResponse(reservation);
System.out.println("Customer: " + response.getCustomerName());
System.out.println("Table: " + response.getTableNumber());

// Multiple reservations
List<ReservationResponse> responses = reservationService
    .toResponses(customerReservations);
```

## Common Operations

### Search Customers

```java
// Search by name
List<Customer> results = customerService.searchCustomers("Smith");

// Get by email
Optional<Customer> customer = customerService
    .getCustomerByEmail("john.doe@example.com");

// Get customers with allergies
List<Customer> allergicCustomers = customerService
    .getCustomersWithAllergies();
```

### Check Table Availability

```java
// Check specific table
boolean available = availabilityService.isTableAvailable(
    5L,                                    // Table ID
    LocalDateTime.of(2025, 11, 25, 19, 0) // Date/time
);

// Get all available tables
List<Table> availableTables = availabilityService.getAvailableTables(
    1L,                                    // Restaurant ID
    LocalDateTime.of(2025, 11, 25, 19, 0), // Date/time
    4                                      // Party size
);
```

### Manage Reservation Status

```java
// Mark as completed
Reservation completed = reservationService.markAsCompleted(reservationId);

// Mark as no-show
Reservation noShow = reservationService.markAsNoShow(reservationId);

// Cancel
Reservation cancelled = reservationService.cancelReservation(reservationId);
```

## Error Handling

```java
try {
    Reservation reservation = reservationService.createReservation(request);
} catch (ReservationConflictException e) {
    System.err.println("Table already reserved: " + e.getMessage());
    // Suggest alternative tables or times
} catch (InsufficientCapacityException e) {
    System.err.println("Table too small: " + e.getMessage());
    // Suggest larger tables
} catch (ReservationException e) {
    System.err.println("Reservation failed: " + e.getMessage());
} catch (CustomerException e) {
    System.err.println("Customer error: " + e.getMessage());
}
```

## Running the Demos

### Customer Service Demo
```bash
cd /path/to/valinor
java com.valinor.data.demo.CustomerServiceDemo
```

### Reservation Service Demo
```bash
cd /path/to/valinor
java com.valinor.data.demo.ReservationServiceDemo
```

## Key Features

### Automatic Conflict Detection
The system automatically prevents double-booking:
- Checks for time overlaps when creating reservations
- Validates availability when updating reservations
- Ignores cancelled reservations in conflict checks

### Validation
All operations are validated:
- Customer must exist
- Restaurant must exist
- Table must exist and be active
- Table capacity must be sufficient
- No time conflicts allowed
- Reservation time must be in future

### Auto-Assignment
Let the system choose the optimal table:
- Finds smallest table that fits party size
- Checks availability automatically
- Prioritizes tables in preferred sections

## Data Files

### customers.csv
Located at: `data/customers.csv`
```csv
customer_id,first_name,last_name,email,phone,allergies,notes
1,John,Doe,john.doe@example.com,555-0100,Peanuts,VIP customer
```

### reservations.csv
Located at: `data/reservations.csv`
```csv
reservation_id,customer_id,restaurant_id,table_id,party_size,reservation_datetime,status,special_requests,created_at,updated_at
1,1,1,1,4,2025-11-20T19:00:00,confirmed,Birthday,2025-11-15T10:00:00,2025-11-15T10:00:00
```

## Next Steps

1. **Run the demos** to see the module in action
2. **Review the documentation:**
   - `RESERVATION_MODULE_COMPLETE.md` - Full implementation details
   - `RESERVATION_MODULE_PLAN.md` - Architecture and design
   - `RESERVATION_INTEGRATION.md` - Integration contract
3. **Integrate with your UI** - Use the services in your controllers
4. **Add custom features** - Extend the services as needed

## Support

For questions or issues:
1. Check the Javadoc in the source files
2. Review the demo classes for examples
3. Consult the complete documentation files

---

**The Reservation Module is ready to manage your restaurant reservations!**
