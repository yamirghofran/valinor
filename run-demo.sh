#!/bin/bash

# Valinor Demo Runner Script
# Usage: ./run-demo.sh [demo-name]
# Available demos: customer, reservation, user, layout, table

DEMO_NAME=$1

if [ -z "$DEMO_NAME" ]; then
    echo "Usage: ./run-demo.sh [demo-name]"
    echo ""
    echo "Available demos:"
    echo "  customer    - Customer Service Demo (CRUD operations)"
    echo "  reservation - Reservation Service Demo (booking system)"
    echo "  user        - User Management Demo (authentication)"
    echo "  layout      - Restaurant Layout Demo (sections/tables)"
    echo "  table       - Table Availability Demo (availability checks)"
    echo ""
    echo "Example: ./run-demo.sh customer"
    exit 1
fi

case $DEMO_NAME in
    customer)
        CLASS="com.valinor.demo.CustomerServiceDemo"
        ;;
    reservation)
        CLASS="com.valinor.demo.ReservationServiceDemo"
        ;;
    user)
        CLASS="com.valinor.demo.UserManagementDemo"
        ;;
    layout)
        CLASS="com.valinor.demo.RestaurantLayoutServiceDemo"
        ;;
    table)
        CLASS="com.valinor.demo.TableAvailabilityDemo"
        ;;
    *)
        echo "Error: Unknown demo '$DEMO_NAME'"
        echo "Run './run-demo.sh' without arguments to see available demos"
        exit 1
        ;;
esac

echo "Running $DEMO_NAME demo..."
echo "=========================================="
mvn -q exec:java -Dexec.mainClass="$CLASS"
