# Valinor Restaurant Reservation System

A comprehensive JavaFX-based restaurant management application for handling reservations, customers, tables, and users with role-based access control.

## Project Structure & Organization

<img width="1300" height="610" alt="CleanShot 2025-10-09 at 09 58 06@2x" src="https://github.com/user-attachments/assets/b885c302-9549-43b0-914f-65f7738b810c" />



## Building the Project

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package
```

## Running the UI Application

### Option 1: Using Maven (Recommended)

```bash
mvn javafx:run
```

### Option 2: Using Java directly

```bash
# First, build the project
mvn clean package

# Then run the main class
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp target/restaurant-reservation-system-1.0.0.jar \
     com.valinor.ui.RestaurantApp
```

### Option 3: From your IDE

Run the main class: `com.valinor.ui.RestaurantApp`

Make sure your IDE has JavaFX configured:
- **IntelliJ IDEA**: Add VM options: `--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`
- **Eclipse**: Add JavaFX library to the build path

## Default Login Credentials

Check the `data/users.csv` file for available users, or use:

```
Username: test_user
Password: Password123
```
