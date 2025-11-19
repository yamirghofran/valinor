# Reservation Module Integration Contract

## Overview

This document defines the integration contract between the **Restaurant Layout Management Module** and the future **Reservation Management Module**. The layout module provides table availability services that the reservation module will consume.

## Architecture

```
┌─────────────────────────────────────┐
│   Reservation Management Module     │
│  (Future Implementation)             │
└──────────────┬──────────────────────┘
               │
               │ Uses
               ▼
┌─────────────────────────────────────┐
│  TableAvailabilityService            │
│  (Layout Module)                     │
└──────────────┬──────────────────────┘
               │
               │ Accesses
               ▼
┌─────────────────────────────────────┐
│  TableRepository & SectionRepository │
└─────────────────────────────────────┘
```

## Integration Points

### 1. Table Availability Checking

**Service:** `TableAvailabilityService`

**Current Methods:**

#### `isTableAvailable(Long tableId, LocalDateTime requestedDateTime)`
Checks if a specific table is available at a given time.

**Current Checks:**
- ✅ Table exists
- ✅ Table is active (`table.isActive() == true`)

**Future Integration (TODO):**
- ⏳ Table has no conflicting reservations at the requested time

**Usage:**
```java
TableAvailabilityService availabilityService = new TableAvailabilityService(
    tableRepository, sectionRepository);

boolean available = availabilityService.isTableAvailable(
    tableId, 
    LocalDateTime.of(2025, 11, 20, 19, 0) // 7:00 PM
);
```

---

#### `getAvailableTables(Long restaurantId, LocalDateTime requestedDateTime, Integer partySize)`
Gets all available tables for a party size at a specific time.

**Current Filters:**
- ✅ Table is active
- ✅ Table capacity >= party size

**Future Integration (TODO):**
- ⏳ No conflicting reservations

**Returns:** `List<Table>` sorted by capacity

**Usage:**
```java
List<Table> tables = availabilityService.getAvailableTables(
    restaurantId,
    LocalDateTime.of(2025, 11, 20, 19, 0),
    4 // Party of 4
);
```

---

### 2. Table Assignment Validation

#### `validateTableAssignment(Long tableId, LocalDateTime requestedDateTime, Integer partySize)`
Validates if a table can be assigned to a reservation.

**Current Validations:**
- ✅ Table exists
- ✅ Table is active
- ✅ Table capacity >= party size

**Future Integration (TODO):**
- ⏳ No conflicting reservations

**Returns:** `boolean`

**Usage:**
```java
boolean valid = availabilityService.validateTableAssignment(
    tableId,
    requestedDateTime,
    partySize
);

if (!valid) {
    throw new ReservationException("Cannot assign table: invalid or unavailable");
}
```

---

### 3. Optimal Table Selection

#### `getOptimalTable(Long restaurantId, LocalDateTime requestedDateTime, Integer partySize)`
Finds the best table for a party (smallest table that fits).

**Algorithm:**
1. Get all available tables for party size
2. Sort by capacity (ascending)
3. Return smallest suitable table

**Returns:** `Optional<Table>`

**Usage:**
```java
Optional<Table> optimalTable = availabilityService.getOptimalTable(
    restaurantId,
    requestedDateTime,
    6
);

if (optimalTable.isPresent()) {
    // Assign table to reservation
    createReservation(optimalTable.get().getTableId(), ...);
}
```

---

### 4. Alternative Table Suggestions

#### `suggestAlternativeTables(Long tableId, LocalDateTime requestedDateTime, Integer partySize)`
Suggests alternatives when the requested table is unavailable.

**Priority:**
1. Tables in the same section (with similar capacity)
2. Tables in other sections

**Returns:** `List<Table>`

**Usage:**
```java
List<Table> alternatives = availabilityService.suggestAlternativeTables(
    requestedTableId,
    requestedDateTime,
    partySize
);

// Offer alternatives to customer
```

---

### 5. Capacity Analysis

#### `getAvailableCapacity(Long restaurantId, LocalDateTime requestedDateTime)`
Returns total available seating capacity.

**Returns:** `int` (total seats)

**Usage:**
```java
int capacity = availabilityService.getAvailableCapacity(
    restaurantId,
    LocalDateTime.now()
);

System.out.println("Restaurant can seat " + capacity + " more people");
```

---

## Future Integration Requirements

### When Implementing Reservation Module

The reservation module **MUST** implement the following to complete the integration:

#### 1. Create ReservationRepository

```java
public interface ReservationRepository extends CsvRepository<Reservation, Long> {
    
    /**
     * Checks if a table has any reservations that conflict with the requested time.
     * 
     * @param tableId the table ID
     * @param startTime the reservation start time
     * @param durationMinutes the reservation duration
     * @return true if there are conflicting reservations
     */
    boolean hasConflictingReservation(Long tableId, LocalDateTime startTime, int durationMinutes);
    
    /**
     * Gets all reservations for a table at a specific date.
     * 
     * @param tableId the table ID
     * @param date the date
     * @return list of reservations
     */
    List<Reservation> findByTableIdAndDate(Long tableId, LocalDate date);
    
    /**
     * Gets all active reservations for a restaurant at a specific time.
     * 
     * @param restaurantId the restaurant ID
     * @param dateTime the date and time
     * @return list of active reservations
     */
    List<Reservation> findActiveReservations(Long restaurantId, LocalDateTime dateTime);
}
```

#### 2. Update TableAvailabilityService

Inject `ReservationRepository` into the service:

```java
public class TableAvailabilityService {
    
    private final TableRepository tableRepository;
    private final SectionRepository sectionRepository;
    private final ReservationRepository reservationRepository; // ADD THIS
    
    public TableAvailabilityService(TableRepository tableRepository,
                                     SectionRepository sectionRepository,
                                     ReservationRepository reservationRepository) { // UPDATE CONSTRUCTOR
        this.tableRepository = tableRepository;
        this.sectionRepository = sectionRepository;
        this.reservationRepository = reservationRepository; // INJECT
    }
    
    // Uncomment all TODO sections that check for reservation conflicts
}
```

#### 3. Implement Conflict Detection

Add logic to check for overlapping reservations:

```java
private boolean hasConflictingReservation(Long tableId, LocalDateTime requestedTime, 
                                          int durationMinutes) {
    LocalDateTime endTime = requestedTime.plusMinutes(durationMinutes);
    
    List<Reservation> tableReservations = reservationRepository
        .findByTableIdAndDate(tableId, requestedTime.toLocalDate());
    
    for (Reservation reservation : tableReservations) {
        LocalDateTime resStart = reservation.getReservationTime();
        LocalDateTime resEnd = resStart.plusMinutes(reservation.getDurationMinutes());
        
        // Check for overlap
        if (requestedTime.isBefore(resEnd) && endTime.isAfter(resStart)) {
            return true; // Conflict found
        }
    }
    
    return false;
}
```

---

## Data Flow Example

### Creating a Reservation (Future Implementation)

```
1. Customer requests reservation
   ├─ Restaurant ID: 1
   ├─ DateTime: 2025-11-20 19:00
   ├─ Party Size: 4
   └─ Optional: Preferred table/section

2. Reservation Service validates request
   └─ TableAvailabilityService.getAvailableTables(1, 2025-11-20 19:00, 4)
       ├─ Gets all sections for restaurant
       ├─ Gets active tables with capacity >= 4
       └─ Filters out tables with conflicting reservations [FUTURE]

3. Reservation Service selects optimal table
   └─ TableAvailabilityService.getOptimalTable(...)
       └─ Returns smallest suitable table

4. Reservation Service validates assignment
   └─ TableAvailabilityService.validateTableAssignment(tableId, dateTime, 4)
       ├─ Checks table is active ✅
       ├─ Checks capacity ✅
       └─ Checks no conflicts [FUTURE] ⏳

5. Create reservation in ReservationRepository
   └─ Repository saves reservation to CSV

6. Return confirmation to customer
```

---

## Testing Requirements

### Integration Tests Required

When implementing the reservation module, create integration tests for:

1. **No Double Booking:** Verify two reservations cannot overlap on same table
2. **Capacity Validation:** Ensure party size doesn't exceed table capacity
3. **Active Table Enforcement:** Inactive tables should not be available
4. **Concurrent Reservations:** Test thread-safe reservation creation
5. **Cascade Updates:** Layout changes should update/invalidate reservations

### Test Scenarios

```java
@Test
public void testTableAvailability_WithConflictingReservation() {
    // Create reservation at 7:00 PM for 2 hours
    Reservation existing = createReservation(tableId, 
        LocalDateTime.of(2025, 11, 20, 19, 0), 
        120);
    
    // Try to check availability at 8:00 PM (should conflict)
    boolean available = availabilityService.isTableAvailable(
        tableId,
        LocalDateTime.of(2025, 11, 20, 20, 0)
    );
    
    assertFalse(available, "Table should not be available due to existing reservation");
}

@Test
public void testTableAvailability_NoConflict() {
    // Create reservation at 7:00 PM for 2 hours
    Reservation existing = createReservation(tableId,
        LocalDateTime.of(2025, 11, 20, 19, 0),
        120);
    
    // Check availability at 9:30 PM (after existing reservation ends)
    boolean available = availabilityService.isTableAvailable(
        tableId,
        LocalDateTime.of(2025, 11, 20, 21, 30)
    );
    
    assertTrue(available, "Table should be available after existing reservation ends");
}
```

---

## Error Handling

### Exception Types

The reservation module should handle:

| Exception | When to Throw |
|-----------|---------------|
| `LayoutValidationException` | Invalid table/section assignment |
| `ReservationConflictException` | Table already reserved |
| `InsufficientCapacityException` | Party size exceeds table capacity |
| `RepositoryException` | Data access failures |

### Example

```java
try {
    boolean valid = availabilityService.validateTableAssignment(tableId, dateTime, partySize);
    if (!valid) {
        throw new ReservationConflictException(
            "Table " + tableId + " is not available at " + dateTime
        );
    }
    // Proceed with reservation
} catch (RepositoryException e) {
    logger.error("Failed to validate table assignment", e);
    throw new ReservationException("System error: cannot validate availability", e);
}
```

---

## Performance Considerations

### Indexing Strategy

When implementing reservations:

1. **Index by table_id + date:** Fast lookup for table availability
2. **Index by restaurant_id + datetime:** Quick capacity queries
3. **Index by status:** Filter active vs completed reservations

### Caching Strategy

Consider caching:
- Available tables for next 2 hours (refresh every 5 minutes)
- Restaurant capacity snapshots
- Table status (active/inactive)

### Concurrency

The `AbstractCsvRepository` already provides:
- ✅ `ConcurrentHashMap` for in-memory cache
- ✅ `synchronized` blocks for file writes
- ✅ Atomic file operations with backups

The reservation module should:
- Use optimistic locking for reservation creation
- Implement retry logic for concurrent bookings
- Validate availability immediately before committing reservation

---

## Summary Checklist

When implementing the Reservation Module:

- [ ] Create `Reservation` entity with all required fields
- [ ] Implement `ReservationRepository` with conflict detection
- [ ] Inject `ReservationRepository` into `TableAvailabilityService`
- [ ] Uncomment all TODO sections in `TableAvailabilityService`
- [ ] Implement conflict detection logic
- [ ] Add reservation status management (pending, confirmed, completed, cancelled)
- [ ] Create integration tests for all scenarios
- [ ] Add concurrency tests for simultaneous bookings
- [ ] Implement cascade logic for layout changes
- [ ] Document any changes to this contract

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2025-11-19 | Initial contract definition |

---

## Contact

For questions about this integration contract, refer to:
- `TableAvailabilityService.java` - Service implementation
- `SCHEMA.md` - Entity relationships
- Project documentation

