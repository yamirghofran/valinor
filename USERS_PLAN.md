# Users Module Implementation Plan

**Authors:** Yousef Amirghofran, Lea Aboujaoude, Boris Gans, Matthew Porteous, Anze Zgonc  
**Created:** Wed Nov 19 2025  
**Status:** In Progress

---

## Overview

This document outlines the implementation plan for the users module in the Valinor Restaurant Reservation System. The module supports multi-user access with role-based permissions and authentication.

### User Types

Based on SCHEMA.md requirements:

1. **Restaurant Managers** - Full CRUD on layout, reservations, and customer history
2. **Front-of-House Staff** - Quick access to reservations, create walk-ins, update statuses
3. **Customers** - Indirect users via booking requests (existing Customer entity)

---

## Module Structure

### 1. Entity Layer (`src/main/java/com/valinor/data/entity/`)

#### 1.1 User.java - Base User Entity

**Fields:**
- `Long userId` (PK)
- `String username` (unique, required)
- `String passwordHash` (required) - BCrypt hashed password
- `String email` (unique, required)
- `String firstName` (required)
- `String lastName` (required)
- `String phone` (optional)
- `UserRole role` (ENUM, required)
- `Long restaurantId` (FK - nullable for system admins)
- `Boolean isActive` (required, default: true)
- `LocalDateTime createdAt` (required)
- `LocalDateTime lastLogin` (optional)

**Relationships:**
- Belongs to: Restaurant (optional - for restaurant-specific users)
- Has one: UserRole (enum)
- Has many: UserPermissions (through role)

#### 1.2 UserRole.java - Enum for User Roles

**Enum values:**
- `SYSTEM_ADMIN` - Full system access (future use)
- `RESTAURANT_MANAGER` - Full CRUD on restaurant data
- `FRONT_OF_HOUSE_STAFF` - Limited access: reservations, customers
- `CUSTOMER` - External users (maps to existing Customer entity)

**Each role has:**
- `String displayName`
- `String description`
- `Set<Permission> defaultPermissions`

#### 1.3 Permission.java - Enum for Permissions

**Enum values:**

Restaurant Management:
- `MANAGE_RESTAURANT_SETTINGS`
- `VIEW_RESTAURANT_SETTINGS`

Layout Management:
- `CREATE_SECTION`, `UPDATE_SECTION`, `DELETE_SECTION`, `VIEW_SECTIONS`
- `CREATE_TABLE`, `UPDATE_TABLE`, `DELETE_TABLE`, `VIEW_TABLES`

Reservation Management:
- `CREATE_RESERVATION`, `UPDATE_RESERVATION`, `CANCEL_RESERVATION`
- `VIEW_RESERVATIONS`, `VIEW_ALL_RESERVATIONS`
- `ASSIGN_TABLE`, `UPDATE_RESERVATION_STATUS`

Customer Management:
- `CREATE_CUSTOMER`, `UPDATE_CUSTOMER`, `DELETE_CUSTOMER`
- `VIEW_CUSTOMERS`, `VIEW_CUSTOMER_HISTORY`

User Management:
- `CREATE_USER`, `UPDATE_USER`, `DELETE_USER`
- `VIEW_USERS`, `ASSIGN_ROLES`

#### 1.4 UserSession.java - Session Tracking Entity

**Fields:**
- `Long sessionId` (PK)
- `Long userId` (FK)
- `String sessionToken` (unique)
- `LocalDateTime createdAt`
- `LocalDateTime expiresAt`
- `String ipAddress` (optional)
- `Boolean isActive`

---

### 2. Repository Layer (`src/main/java/com/valinor/data/repository/`)

#### 2.1 UserRepository.java

**Extends:** `AbstractCsvRepository<User, Long>`

**Custom Methods:**
- `Optional<User> findByUsername(String username)`
- `Optional<User> findByEmail(String email)`
- `List<User> findByRole(UserRole role)`
- `List<User> findByRestaurantId(Long restaurantId)`
- `List<User> findActiveUsers()`
- `List<User> findByRestaurantIdAndRole(Long restaurantId, UserRole role)`
- `boolean existsByUsername(String username)`
- `boolean existsByEmail(String email)`

#### 2.2 UserEntityMapper.java

**Implements:** `EntityMapper<User>`

**CSV Columns:**
- `user_id`, `username`, `password_hash`, `email`
- `first_name`, `last_name`, `phone`, `role`
- `restaurant_id`, `is_active`, `created_at`, `last_login`

**Handles:**
- Date/time formatting (ISO-8601)
- Enum serialization (role)
- Null handling for optional fields
- Password hash validation

#### 2.3 UserSessionRepository.java

**Extends:** `AbstractCsvRepository<UserSession, Long>`

**Custom Methods:**
- `Optional<UserSession> findBySessionToken(String token)`
- `List<UserSession> findByUserId(Long userId)`
- `List<UserSession> findActiveSessions()`
- `void expireSession(String token)`
- `void expireAllUserSessions(Long userId)`
- `void cleanupExpiredSessions()`

#### 2.4 UserSessionEntityMapper.java

**Implements:** `EntityMapper<UserSession>`

**CSV Columns:**
- `session_id`, `user_id`, `session_token`
- `created_at`, `expires_at`, `ip_address`, `is_active`

---

### 3. Service Layer (`src/main/java/com/valinor/data/service/`)

#### 3.1 UserService.java

**Business logic for user management**

**Methods:**
- `User createUser(UserCreateRequest request) throws UserServiceException`
- `User updateUser(Long userId, UserUpdateRequest request) throws UserServiceException`
- `void deleteUser(Long userId) throws UserServiceException`
- `Optional<User> getUserById(Long userId)`
- `Optional<User> getUserByUsername(String username)`
- `List<User> getUsersByRestaurant(Long restaurantId)`
- `List<User> getUsersByRole(UserRole role)`
- `void activateUser(Long userId) throws UserServiceException`
- `void deactivateUser(Long userId) throws UserServiceException`
- `void changePassword(Long userId, String oldPassword, String newPassword) throws UserServiceException`
- `void resetPassword(Long userId, String newPassword) throws UserServiceException`

**Responsibilities:**
- Password hashing (BCrypt)
- Username/email uniqueness validation
- Role assignment validation
- Restaurant association validation
- Audit logging

#### 3.2 AuthenticationService.java

**Handles user authentication and session management**

**Methods:**
- `UserSession login(String username, String password) throws AuthenticationException`
- `void logout(String sessionToken) throws AuthenticationException`
- `Optional<User> validateSession(String sessionToken)`
- `UserSession refreshSession(String sessionToken) throws AuthenticationException`
- `void logoutAllSessions(Long userId)`
- `boolean verifyPassword(String plainPassword, String hashedPassword)`
- `String hashPassword(String plainPassword)`

**Responsibilities:**
- Password verification
- Session token generation (UUID-based)
- Session expiration management
- Brute force protection (future)
- Audit logging for auth events

#### 3.3 AuthorizationService.java

**Handles permission checking**

**Methods:**
- `boolean hasPermission(User user, Permission permission)`
- `boolean hasAnyPermission(User user, Permission... permissions)`
- `boolean hasAllPermissions(User user, Permission... permissions)`
- `Set<Permission> getUserPermissions(User user)`
- `void checkPermission(User user, Permission permission) throws AuthorizationException`
- `boolean canAccessRestaurant(User user, Long restaurantId)`
- `boolean canManageUser(User manager, User targetUser)`

**Responsibilities:**
- Role-based permission checking
- Restaurant-scoped authorization
- Hierarchical permission validation

---

### 4. Exception Layer (`src/main/java/com/valinor/data/exception/`)

#### 4.1 UserServiceException.java
**Extends:** `RepositoryException`  
**Used for:** User creation/update failures, validation errors, business rule violations

#### 4.2 AuthenticationException.java
**Extends:** `UserServiceException`  
**Used for:** Invalid credentials, account locked/disabled, session expired

#### 4.3 AuthorizationException.java
**Extends:** `UserServiceException`  
**Used for:** Insufficient permissions, access denied, cross-restaurant access attempts

#### 4.4 DuplicateUserException.java
**Extends:** `UserServiceException`  
**Used for:** Duplicate username, duplicate email

---

### 5. DTO Layer (`src/main/java/com/valinor/data/dto/`)

#### 5.1 UserCreateRequest.java

**Fields:**
- `String username` (required)
- `String password` (required)
- `String email` (required)
- `String firstName` (required)
- `String lastName` (required)
- `String phone` (optional)
- `UserRole role` (required)
- `Long restaurantId` (optional)

**Validation:**
- Username: 3-50 chars, alphanumeric + underscore
- Password: min 8 chars, complexity rules
- Email: valid format
- Role: valid enum value

#### 5.2 UserUpdateRequest.java

**Fields:**
- `String email` (optional)
- `String firstName` (optional)
- `String lastName` (optional)
- `String phone` (optional)
- `UserRole role` (optional)
- `Long restaurantId` (optional)
- `Boolean isActive` (optional)

**Note:** Username cannot be changed

#### 5.3 UserResponse.java

**Fields:**
- `Long userId`, `String username`, `String email`
- `String firstName`, `String lastName`, `String phone`
- `UserRole role`, `Long restaurantId`, `Boolean isActive`
- `LocalDateTime createdAt`, `LocalDateTime lastLogin`

**Note:** Password hash excluded for security

#### 5.4 LoginRequest.java

**Fields:**
- `String username` (required)
- `String password` (required)
- `String ipAddress` (optional)

#### 5.5 LoginResponse.java

**Fields:**
- `String sessionToken`
- `UserResponse user`
- `LocalDateTime expiresAt`
- `Set<Permission> permissions`

---

### 6. Utility Layer (`src/main/java/com/valinor/data/util/`)

#### 6.1 PasswordHasher.java

**Methods:**
- `String hash(String plainPassword)`
- `boolean verify(String plainPassword, String hashedPassword)`

**Implementation:**
- Uses BCrypt algorithm
- Configurable work factor (default: 12)
- Salt generation handled by BCrypt

#### 6.2 SessionTokenGenerator.java

**Methods:**
- `String generateToken()`
- `boolean isValidToken(String token)`

**Implementation:**
- UUID-based tokens
- URL-safe encoding
- Cryptographically secure random

#### 6.3 PermissionMapper.java

**Methods:**
- `Set<Permission> getPermissionsForRole(UserRole role)`
- `boolean roleHasPermission(UserRole role, Permission permission)`

**Implementation:**
- Static mapping of roles to permissions
- Follows principle of least privilege

---

### 7. Configuration (`src/main/java/com/valinor/data/config/`)

#### 7.1 UserConfig.java

**Configuration constants:**
- `SESSION_EXPIRY_HOURS` (default: 24)
- `PASSWORD_MIN_LENGTH` (default: 8)
- `PASSWORD_REQUIRE_UPPERCASE` (default: true)
- `PASSWORD_REQUIRE_LOWERCASE` (default: true)
- `PASSWORD_REQUIRE_DIGIT` (default: true)
- `PASSWORD_REQUIRE_SPECIAL` (default: false)
- `BCRYPT_WORK_FACTOR` (default: 12)
- `MAX_LOGIN_ATTEMPTS` (default: 5)
- `LOCKOUT_DURATION_MINUTES` (default: 30)

---

### 8. CSV Data Files

#### 8.1 data/users.csv
```csv
user_id,username,password_hash,email,first_name,last_name,phone,role,restaurant_id,is_active,created_at,last_login
```

#### 8.2 data/user_sessions.csv
```csv
session_id,user_id,session_token,created_at,expires_at,ip_address,is_active
```

---

### 9. Demo/Testing (`src/main/java/com/valinor/data/demo/`)

#### 9.1 UserManagementDemo.java

**Demonstrates:**
- Creating users with different roles
- User authentication
- Permission checking
- Session management
- Password changes
- User deactivation

---

## Role-Permission Mapping

### RESTAURANT_MANAGER
All permissions including:
- Full restaurant settings management
- Full layout management (sections, tables)
- Full reservation management
- Full customer management
- User management (within their restaurant)

### FRONT_OF_HOUSE_STAFF
Limited permissions:
- `VIEW_RESTAURANT_SETTINGS`
- `VIEW_SECTIONS`, `VIEW_TABLES`
- `CREATE_RESERVATION`, `UPDATE_RESERVATION`, `CANCEL_RESERVATION`
- `VIEW_RESERVATIONS`, `ASSIGN_TABLE`, `UPDATE_RESERVATION_STATUS`
- `CREATE_CUSTOMER`, `UPDATE_CUSTOMER`, `VIEW_CUSTOMERS`, `VIEW_CUSTOMER_HISTORY`

### CUSTOMER (future integration)
Minimal permissions:
- `CREATE_RESERVATION` (own reservations only)
- `VIEW_RESERVATIONS` (own reservations only)
- `UPDATE_CUSTOMER` (own profile only)

---

## Security Considerations

### 1. Password Storage
- BCrypt hashing with salt
- Never store plain text passwords
- Password complexity requirements

### 2. Session Management
- UUID-based session tokens
- Configurable expiration
- Secure token generation
- Session invalidation on logout

### 3. Authorization
- Restaurant-scoped access control
- Role-based permissions
- Permission checks before all operations

### 4. Audit Trail (future enhancement)
- Log all authentication attempts
- Log permission denials
- Track user actions

---

## Dependencies

### Add to pom.xml

```xml
<!-- Password hashing -->
<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>
```

---

## Implementation Order

### ✅ Phase 1: Core Entities & Enums
- [ ] Permission.java
- [ ] UserRole.java
- [ ] User.java
- [ ] UserSession.java

### Phase 2: Exceptions
- [ ] UserServiceException.java
- [ ] AuthenticationException.java
- [ ] AuthorizationException.java
- [ ] DuplicateUserException.java

### Phase 3: Utilities
- [ ] PasswordHasher.java
- [ ] SessionTokenGenerator.java
- [ ] PermissionMapper.java
- [ ] UserConfig.java

### Phase 4: Repository Layer
- [ ] UserEntityMapper.java
- [ ] UserRepository.java
- [ ] UserSessionEntityMapper.java
- [ ] UserSessionRepository.java

### Phase 5: DTOs
- [ ] UserCreateRequest.java
- [ ] UserUpdateRequest.java
- [ ] UserResponse.java
- [ ] LoginRequest.java
- [ ] LoginResponse.java

### Phase 6: Service Layer
- [ ] AuthorizationService.java
- [ ] AuthenticationService.java
- [ ] UserService.java

### Phase 7: Demo & Testing
- [ ] UserManagementDemo.java
- [ ] Unit tests (if required)

---

## File Structure

```
src/main/java/com/valinor/data/
├── entity/
│   ├── User.java
│   ├── UserRole.java
│   ├── Permission.java
│   └── UserSession.java
├── repository/
│   ├── UserRepository.java
│   ├── UserEntityMapper.java
│   ├── UserSessionRepository.java
│   └── UserSessionEntityMapper.java
├── service/
│   ├── UserService.java
│   ├── AuthenticationService.java
│   └── AuthorizationService.java
├── exception/
│   ├── UserServiceException.java
│   ├── AuthenticationException.java
│   ├── AuthorizationException.java
│   └── DuplicateUserException.java
├── dto/
│   ├── UserCreateRequest.java
│   ├── UserUpdateRequest.java
│   ├── UserResponse.java
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── util/
│   ├── PasswordHasher.java
│   ├── SessionTokenGenerator.java
│   └── PermissionMapper.java
├── config/
│   └── UserConfig.java
└── demo/
    └── UserManagementDemo.java

data/
├── users.csv
└── user_sessions.csv

src/main/resources/data/
├── users.csv
└── user_sessions.csv
```

---

## Integration Points

### 1. With Restaurant Entity
- Users belong to restaurants (FK relationship)
- Restaurant managers can only manage their restaurant's data

### 2. With Customer Entity
- Future: Link Customer to User for self-service booking
- Customer profile can be associated with User account

### 3. With Reservation Entity
- Authorization checks before reservation CRUD
- Staff can see all reservations, customers only their own

### 4. With Section/Table Entities
- Only managers can modify layout
- Staff can view for reservation assignment

---

## Testing Strategy

### 1. Unit Tests
- PasswordHasher verification
- Permission mapping correctness
- Entity validation
- Repository CRUD operations

### 2. Integration Tests
- User creation workflow
- Authentication flow
- Authorization checks
- Session management

### 3. Demo Application
- UserManagementDemo showing all features
- Sample data for different roles
- Common use cases demonstrated

---

## Notes

- This module follows existing codebase patterns (CSV-based storage, repository pattern)
- All entities use the same architectural style as Restaurant, Customer, etc.
- Security best practices implemented (BCrypt, session tokens, RBAC)
- Designed for future scalability and database migration
