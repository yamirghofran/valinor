@echo off
REM Valinor Restaurant Reservation System - JAR Runner (Windows)
REM This script runs the executable JAR file

echo ==========================================
echo Valinor Restaurant Reservation System
echo ==========================================
echo.

REM Check if JAR file exists
set JAR_FILE=target\valinor-restaurant-system.jar

if not exist "%JAR_FILE%" (
    echo Error: JAR file not found at %JAR_FILE%
    echo.
    echo Please build the JAR file first by running:
    echo   mvn clean package
    echo.
    pause
    exit /b 1
)

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 11 or higher
    pause
    exit /b 1
)

echo Starting application...
echo.

REM Run using Maven JavaFX plugin (recommended method)
mvn javafx:run

if errorlevel 1 (
    echo.
    echo Error: Failed to start the application
    pause
)
