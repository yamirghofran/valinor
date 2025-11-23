# Reservation Module Implementation Plan

**Date:** November 19, 2025  
**Status:** Planning Phase - READ-ONLY  
**Module:** Reservation Management System

---

## Executive Summary

This document provides a comprehensive implementation plan for the **Reservation Module**, which will manage all aspects of restaurant reservations including customer profiles, reservation creation/management, conflict detection, and integration with the existing table availability system.

**Key Objectives:**
- Build a complete reservation management system following existing codebase patterns
- Integrate seamlessly with TableAvailabilityService
- Prevent double-booking through robust conflict detection
- Provide customer profile management
- Enable both manual and automatic table assignment

**Estimated Effort:** 14-18 hours for core implementation (Phases 1-3)

---

## Quick Reference

### Files to Create (20 files)

**Phase 1 - Customer Management (7 files):**
1. `CustomerEntityMapper.java`
2. `CustomerRepository.java`
3. `CustomerService.java`
4. `CreateCustomerRequest.java`
5. `UpdateCustomerRequest.java`
6. `CustomerResponse.java`
7. `CustomerException.java`

**Phase 2 - Reservation Repository (6 files):**
8. `ReservationEntityMapper.java`
9. `ReservationRepository.java`
10. `ReservationException.java`
11. `ReservationConflictException.java`
12. `InsufficientCapacityException.java`
13. `reservations.csv`

**Phase 3 - Reservation Service (7 files):**
14. `ReservationService.java`
15. `CreateReservationRequest.java`
16. `UpdateReservationRequest.java`
17. `ReservationResponse.java`
18. `ReservationServiceDemo.java`
19. `CustomerServiceDemo.java`
20. `customers.csv`

**Files to Update (1 file):**
- `TableAvailabilityService.java` - Add ReservationRepository integration

---

## Architecture Overview

```
┌──────────────────────────────────────────────┐
│         Reservation Module                    │
├──────────────────────────────────────────────┤
│                                               │
│  Service Layer:                               │
│  ├─ ReservationService                        │
│  │  ├─ Create/Update/Cancel reservations     │
│  │  ├─ Validate availability                 │
│  │  └─ Auto-assign tables                    │
│  └─ CustomerService                           │
│     └─ Manage customer profiles              │
│                                               │
│  Repository Layer:                            │
│  ├─ ReservationRepository                     │
│  │  ├─ CRUD operations                        │
│  │  └─ Conflict detection                    │
│  └─ CustomerRepository                        │
│     └─ Customer data access                  │
│                                               │
│  Integration:                                 │
│  └─ TableAvailabilityService                  │
│     └─ Check table availability with          │
│        reservation conflicts                  │
└──────────────────────────────────────────────┘
```

---

## Implementation Phases

### Phase 1: Customer Management Foundation
**Priority:** HIGH | **Effort:** 3-4 hours | **Dependencies:** None

Build customer management infrastructure as a foundation for reservations.

**Components:**
- CustomerEntityMapper (CSV ↔ Entity conversion)
- CustomerRepository (Data access)
- CustomerService (Business logic)
- Customer DTOs (Request/Response objects)
- CustomerException
- customers.csv data file
- CustomerServiceDemo

**Why First?** Customers are required for reservations. This phase validates the repository pattern before tackling complex reservation logic.

---

### Phase 2: Reservation Repository
**Priority:** HIGH | **Effort:** 4-5 hours | **Dependencies:** Phase 1

Build the data access layer for reservations with conflict detection.

**Components:**
- ReservationEntityMapper
- ReservationRepository (with `hasConflictingReservation()` method)
- Exception classes (ReservationException, ReservationConflictException, InsufficientCapacityException)
- reservations.csv data file

**Critical Feature:** Conflict detection algorithm to prevent double-booking.

---

### Phase 3: Reservation Service & Business Logic
**Priority:** HIGH | **Effort:** 5-6 hours | **Dependencies:** Phases 1 & 2

Implement business logic for reservation management.

**Components:**
- ReservationService (orchestrates all reservation operations)
- Reservation DTOs
- Integration with TableAvailabilityService
- Demo classes

**Key Features:**
- Create reservation with specific table
- Create reservation with auto-assignment
- Update/cancel reservations
- Status management (confirmed, completed, no-show, cancelled)
- Suggest alternative tables/times

---

### Phase 4: Integration & Testing
**Priority:** MEDIUM | **Effort:** 2-3 hours | **Dependencies:** Phases 1-3

Complete integration with existing modules and comprehensive testing.

**Tasks:**
- Update TableAvailabilityService to check reservation conflicts
- Integration testing
- Documentation updates

---

## Key Components Specification

### 1. Conflict Detection Algorithm

**Critical Method:** `ReservationRepository.hasConflictingReservation()`

```java
/**
 * Checks if a table has any conflicting reservations at the requested time.
 * 
 * Algorithm:
 * 1. Get all reservations for the table on the requested date
 * 2. For each non-cancelled reservation:
 *    - Calculate existing reservation time window
 *    - Check for overlap with requested time window
 *    - Return true if any overlap found
 * 3. Return false if no conflicts
 * 
 * Overlap detection: (StartA < EndB) AND (EndA > StartB)
 */
public boolean hasConflictingReservation(
    Long tableId, 
    LocalDateTime requestedTime, 
    int durationMinutes
) throws RepositoryException
```

---

### 2. Auto-Assignment Logic

**Method:** `ReservationService.createReservationWithAutoAssignment()`

```
Flow:
1. Use TableAvailabilityService.getOptimalTable()
   ├─ Get all active tables with sufficient capacity
   ├─ Filter out tables with reservation conflicts
   ├─ Sort by capacity (ascending)
   └─ Return smallest suitable table
2. Assign table to reservation request
3. Create reservation with assigned table
```

---

### 3. CSV Data Schemas

**customers.csv:**
```csv
customer_id,first_name,last_name,email,phone,allergies,notes
1,John,Doe,john.doe@example.com,555-0100,Peanuts,VIP customer
2,Jane,Smith,jane.smith@example.com,555-0101,,
```

**reservations.csv:**
```csv
reservation_id,customer_id,restaurant_id,table_id,party_size,reservation_datetime,status,special_requests,created_at,updated_at
1,1,1,5,4,2025-11-20T19:00:00,confirmed,Birthday,2025-11-15T10:00:00,2025-11-15T10:00:00
2,2,1,3,2,2025-11-20T20:00:00,confirmed,,2025-11-15T11:00:00,2025-11-15T11:00:00
```

---

### 4. Validation Rules

**Customer Validation:**
- Email must be unique and valid format
- First name and last name required (non-empty)
- Phone number required
- Allergies and notes are optional

**Reservation Validation:**
- Customer must exist
- Restaurant must exist
- Table must exist and be active
- Party size must be > 0 and ≤ table capacity
- Reservation time must be in the future (for new reservations)
- No conflicting reservations on the same table
- Status must be valid ReservationStatus value

---

### 5. Error Handling

| Error Condition | Exception Type | Message |
|----------------|----------------|---------|
| Customer not found | ReservationException | "Customer not found with ID: {id}" |
| Table not available | ReservationException | "Table is not active" |
| Capacity exceeded | InsufficientCapacityException | "Table capacity ({cap}) insufficient for party size ({size})" |
| Time conflict | ReservationConflictException | "Table already reserved at the requested time" |
| Duplicate email | CustomerException | "Email already exists: {email}" |

---

## Integration with Existing Code

### TableAvailabilityService Updates

**Current State:** Has TODO comments for reservation integration

**Required Changes:**
1. Add `ReservationRepository` as dependency
2. Update constructor to inject ReservationRepository
3. Implement conflict checking in `isTableAvailable()`
4. Filter conflicting tables in `getAvailableTables()`

**Example:**
```java
public boolean isTableAvailable(Long tableId, LocalDateTime requestedDateTime) {
    // ... existing checks ...
    
    // ADD: Check for reservation conflicts
    boolean hasConflict = reservationRepository.hasConflictingReservation(
        tableId, requestedDateTime, 120);
    if (hasConflict) {
        return false;
    }
    
    return true;
}
```

---

## Data Flow Examples

### Creating a Reservation

```
User → CreateReservationRequest
  ↓
ReservationService
  ├─ Validate request
  ├─ Check customer exists
  ├─ Check restaurant exists
  ├─ Check table exists & active
  ├─ Validate capacity
  ├─ Check for conflicts (ReservationRepository)
  └─ Save reservation
  ↓
ReservationResponse → User
```

### Auto-Assignment Flow

```
User → CreateReservationRequest (no tableId)
  ↓
ReservationService.createReservationWithAutoAssignment()
  ├─ TableAvailabilityService.getOptimalTable()
  │  ├─ Get suitable tables
  │  ├─ Filter conflicts (uses ReservationRepository)
  │  └─ Return smallest table
  ├─ Assign table to request
  └─ Create reservation
  ↓
ReservationResponse → User
```

---

## Testing Strategy

### Unit Tests
- CustomerRepository CRUD operations
- ReservationRepository conflict detection
- CustomerService validation logic
- ReservationService business rules

### Integration Tests
- Full reservation creation flow
- Double-booking prevention
- Table availability with reservations
- Cascade deletions

### Test Data
Provided in CSV format for both customers and reservations.

---

## Success Criteria

The module is complete when:

✅ CustomerService manages customer profiles  
✅ ReservationService creates/updates/cancels reservations  
✅ Conflict detection prevents double-booking  
✅ Auto-assignment finds optimal tables  
✅ TableAvailabilityService integrates with ReservationRepository  
✅ All demo classes run successfully  
✅ CSV persistence works correctly  
✅ Error handling is comprehensive  
✅ Code follows existing patterns  

---

## File Structure

```
src/main/java/com/valinor/data/
├── entity/
│   ├── Customer.java              [EXISTS]
│   ├── Reservation.java           [EXISTS]
│   └── ReservationStatus.java     [EXISTS]
├── repository/
│   ├── CustomerEntityMapper.java  [CREATE]
│   ├── CustomerRepository.java    [CREATE]
│   ├── ReservationEntityMapper.java [CREATE]
│   └── ReservationRepository.java [CREATE]
├── service/
│   ├── CustomerService.java       [CREATE]
│   └── ReservationService.java    [CREATE]
├── dto/
│   ├── CreateCustomerRequest.java [CREATE]
│   ├── UpdateCustomerRequest.java [CREATE]
│   ├── CustomerResponse.java      [CREATE]
│   ├── CreateReservationRequest.java [CREATE]
│   ├── UpdateReservationRequest.java [CREATE]
│   └── ReservationResponse.java   [CREATE]
├── exception/
│   ├── CustomerException.java     [CREATE]
│   ├── ReservationException.java  [CREATE]
│   ├── ReservationConflictException.java [CREATE]
│   └── InsufficientCapacityException.java [CREATE]
└── demo/
    ├── CustomerServiceDemo.java   [CREATE]
    └── ReservationServiceDemo.java [CREATE]

data/
├── customers.csv                  [CREATE]
└── reservations.csv               [CREATE]
```

---

## Next Steps

1. **Review this plan** - Ensure all stakeholders agree
2. **Begin Phase 1** - Customer Management
3. **Test Phase 1** - Validate repository pattern
4. **Proceed to Phase 2** - Reservation Repository
5. **Continue sequentially** through remaining phases

---

## Additional Resources

- See `RESERVATION_INTEGRATION.md` for integration contract details
- See `SCHEMA.md` for entity relationship definitions
- See existing `UserService.java` and `RestaurantLayoutService.java` for pattern examples

---

**Document Version:** 1.0  
**Last Updated:** November 19, 2025  
**Status:** Ready for Implementation
