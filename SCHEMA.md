# Database Schema Proposal — Restaurant Reservation System

**Authors:** Yousef Amirghofran, Lea Aboujaoude, Boris Gans, Matthew Porteous, Anze Zgonc
**Topic:** Database schema and entity relationships for the restaurant reservation system.

---

# Overview

The system manages:

- Restaurant layout (sections, tables, capacities)
- Customer profiles
- Reservations (time, party size, table assignment, status)
- Calendar and time-slot availability

This document defines the database schema and entity relationships required to support these modules.

---

# Core Entities

## 1. Restaurant
Represents a restaurant using the system.

**Fields**
- `restaurant_id` (PK)
- `name`
- `location`
- `contact_email`
- `contact_phone`

**Relationships**
- One-to-many: Sections
- One-to-many: Tables (through sections)
- One-to-many: Reservations

---

## 2. Section
A physical subdivision of the restaurant (such as patio, bar, dining room).

**Fields**
- `section_id` (PK)
- `restaurant_id` (FK)
- `name`
- `num_tables` (optional)
- `notes` (optional)

**Relationships**
- One-to-many: Tables

---

## 3. Table
Individual tables within a section.

**Fields**
- `table_id` (PK)
- `section_id` (FK)
- `table_number`
- `capacity`
- `is_active` (boolean)

**Relationships**
- One-to-many: Reservations

---

## 4. Customer
Customer profile information.

**Fields**
- `customer_id` (PK)
- `restaurant_id` (FK)
- `first_name`
- `last_name`
- `email`
- `phone`
- `allergies` (optional)
- `notes` (optional)

**Relationships**
- Belongs to: Restaurant
- One-to-many: Reservations

---

## 5. Reservation
A booking made by a customer for a specific table and time.

**Fields**
- `reservation_id` (PK)
- `customer_id` (FK)
- `restaurant_id` (FK)
- `table_id` (FK)
- `party_size`
- `reservation_datetime`
- `status` (ENUM: `confirmed`, `completed`, `no_show`, `cancelled`)
- `special_requests` (optional)
- `created_at`
- `updated_at`

**Relationships**
- Belongs to: Restaurant
- Belongs to: Customer
- Belongs to: Table

---

## 6. Calendar / Availability (Optional)
Optionally implemented as:
- A generated query/view, or
- A dedicated `time_slot` table

If implemented as a table:

**Fields**
- `slot_id` (PK)
- `restaurant_id` (FK)
- `date`
- `start_time`
- `end_time`
- `is_available`

---

# Entity Relationship Diagram (Text Representation)

```
Restaurant
 ├── Sections
 │     └── Tables
 │            └── Reservations
 └── Reservations
Customer
 └── Reservations
```

---

# Module Breakdown

## Reservation Management
Responsible for creating, modifying, retrieving, and cancelling reservations.

Uses:
- Reservation
- Table
- Customer
- Restaurant

---

## Restaurant Layout Management
Handles creation and configuration of the restaurant's physical layout.

Uses:
- Section
- Table
- Restaurant

---

## Customer Management
Stores customer profiles and relevant booking notes.

Uses:
- Customer
- Reservation

---

# User Roles and Requirements

## Restaurant Managers
- Full CRUD on layout settings
- Modify reservation rules and schedules
- Access customer history

## Front-of-House Staff
- Quick access to daily reservations
- Create walk-ins
- Update reservation statuses

## Customers (Indirect Users)
- Minimal interaction via booking requests
- Receive confirmations and updates

---

# Multi-User Requirements

## Concurrency
- Prevent double-booking
- Transaction-safe operations
- Real-time updates for all staff

## Authentication
- Unique user accounts
- Secure password hashing
- Role-based authorization

## Real-Time Interaction
- Live table availability
- Instant updates on reservation changes

---

# Summary of Proposed Schema

| Entity       | Key Fields                            | Relationships                            |
|--------------|----------------------------------------|-------------------------------------------|
| Restaurant   | name, location, contact info           | 1→many sections, tables, reservations     |
| Section      | name                                   | 1→many tables                             |
| Table        | table_number, capacity                 | 1→many reservations                       |
| Customer     | name, contact                          | 1→many reservations                       |
| Reservation  | time, party size, status, table_id     | belongs to restaurant, table, customer    |

---

If needed, I can also generate:

- SQL schema
- Mermaid ERD diagram
- GitHub Issue template version
- A visual ERD illustration
