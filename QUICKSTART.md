# Valinor Restaurant Reservation System - Quick Start Guide

## 🚀 Running the Application

### Option 1: Using the Demo Script (Recommended)

The easiest way to run demos:

```bash
# Make the script executable (first time only)
chmod +x run-demo.sh

# Run a demo
./run-demo.sh customer      # Customer management
./run-demo.sh reservation   # Reservation system
./run-demo.sh user          # User authentication
./run-demo.sh layout        # Restaurant layout
./run-demo.sh table         # Table availability
```

### Option 2: Using Maven Directly

```bash
# Customer Service Demo
mvn exec:java -Dexec.mainClass="com.valinor.data.demo.CustomerServiceDemo"

# Reservation Service Demo
mvn exec:java -Dexec.mainClass="com.valinor.data.demo.ReservationServiceDemo"

# User Management Demo
mvn exec:java -Dexec.mainClass="com.valinor.data.demo.UserManagementDemo"

# Restaurant Layout Demo
mvn exec:java -Dexec.mainClass="com.valinor.restauraunt.management.demo.RestaurantLayoutServiceDemo"

# Table Availability Demo
mvn exec:java -Dexec.mainClass="com.valinor.restauraunt.management.demo.TableAvailabilityDemo"
```

### Option 3: Using Java Directly (Requires Building First)

```bash
# First, compile the project
mvn clean package

# Then run with full classpath
java -cp "target/classes:target/dependency/*" com.valinor.data.demo.CustomerServiceDemo
```

## 📦 Building the Project

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Create JAR package
mvn package
```

## 🎯 What Each Demo Shows

### Customer Service Demo (`./run-demo.sh customer`)
- Create, read, update, delete customers
- Search by name, email, phone
- Track customer allergies
- Email uniqueness validation

### Reservation Service Demo (`./run-demo.sh reservation`)
- Create reservations with specific tables
- Auto-assign optimal tables
- Conflict detection (prevents double-booking)
- Update/cancel/complete reservations
- Party size validation

### User Management Demo (`./run-demo.sh user`)
- User registration and login
- Password hashing (BCrypt)
- Session token management
- Role-based access control
- Permission checking

### Restaurant Layout Demo (`./run-demo.sh layout`)
- View restaurant sections and tables
- Create and manage sections
- Add/update/remove tables
- Capacity management

### Table Availability Demo (`./run-demo.sh table`)
- Check table availability by time
- Find optimal tables for party size
- Section-based availability
- Reservation conflict checking

## 📁 Data Files

All data is stored in CSV files in the `data/` directory:
- `customers.csv` - Customer profiles
- `reservations.csv` - Reservation records
- `users.csv` - User accounts
- `restaurants.csv` - Restaurant information
- `sections.csv` - Restaurant sections
- `tables.csv` - Table inventory
- `user_sessions.csv` - Active sessions

**Backups**: Each file has a `.backup` version created automatically on updates.

## 🔧 Troubleshooting

### "Could not find or load main class"
**Solution**: Use Maven or the demo script instead of running `java` directly:
```bash
./run-demo.sh customer
```

### "NoClassDefFoundError: org/slf4j/LoggerFactory"
**Solution**: Dependencies are missing. Use Maven:
```bash
mvn exec:java -Dexec.mainClass="com.valinor.data.demo.CustomerServiceDemo"
```

### "No available tables" error in reservation demo
**Expected behavior**: The demo data has all tables booked. This shows the conflict detection is working correctly.

### CSV parsing warnings
**Expected behavior**: The system skips header rows gracefully. These warnings don't affect functionality.

## 📚 Documentation

- `SCHEMA.md` - Database schema and entity relationships
- `RESERVATION_MODULE_COMPLETE.md` - Reservation system details
- `RESERVATION_QUICKSTART.md` - Reservation-specific guide
- `USERS_MODULE_COMPLETE.md` - User management details
- `README.md` - Project overview

## 💡 Quick Examples

### Create a Customer
```java
CustomerService customerService = new CustomerService(new CustomerRepository("data/customers.csv"));

CreateCustomerRequest request = new CreateCustomerRequest(
    "John", "Doe", "john@example.com", "555-1234", "Peanuts", "VIP customer"
);

CustomerResponse customer = customerService.createCustomer(request);
```

### Create a Reservation
```java
ReservationService reservationService = new ReservationService(
    reservationRepo, customerRepo, restaurantRepo, tableRepo, sectionRepo
);

CreateReservationRequest request = new CreateReservationRequest(
    1L,  // customerId
    1L,  // restaurantId
    2L,  // tableId
    4,   // partySize
    LocalDateTime.of(2025, 11, 22, 19, 0)  // reservationTime
);

ReservationResponse reservation = reservationService.createReservation(request);
```

### Auto-Assign Table
```java
CreateReservationRequest request = new CreateReservationRequest(
    1L,  // customerId
    1L,  // restaurantId
    4,   // partySize
    LocalDateTime.of(2025, 11, 22, 19, 0)
);

// System automatically finds the best available table
ReservationResponse reservation = reservationService.createReservationWithAutoAssignment(request);
```

## 🎓 Learning Path

1. **Start with Customer Demo** - Understand basic CRUD operations
2. **Try User Demo** - Learn authentication and sessions
3. **Explore Layout Demo** - See restaurant structure
4. **Run Table Demo** - Understand availability logic
5. **Master Reservation Demo** - See the complete system in action

## 🆘 Getting Help

If you encounter issues:
1. Check that Maven is installed: `mvn --version`
2. Ensure Java 11+ is installed: `java --version`
3. Verify you're in the project root directory
4. Try `mvn clean compile` to rebuild
5. Check the demo script has execute permissions: `ls -l run-demo.sh`

---

**Happy coding! 🎉**
