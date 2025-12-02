#!/bin/bash

# Valinor Restaurant Reservation System - JAR Runner
# This script runs the executable JAR file

echo "=========================================="
echo "Valinor Restaurant Reservation System"
echo "=========================================="
echo ""

# Check if JAR file exists
JAR_FILE="target/valinor-restaurant-system.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found at $JAR_FILE"
    echo ""
    echo "Please build the JAR file first by running:"
    echo "  mvn clean package"
    echo ""
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')

if [ -z "$JAVA_VERSION" ]; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 11 or higher"
    exit 1
fi

if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "Warning: Java version $JAVA_VERSION detected. Java 11 or higher is recommended."
fi

echo "Starting application..."
echo ""

# Run using Maven JavaFX plugin (recommended method)
mvn javafx:run
