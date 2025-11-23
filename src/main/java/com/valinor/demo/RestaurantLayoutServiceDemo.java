package com.valinor.demo;

import com.valinor.domain.model.Restaurant;
import com.valinor.domain.model.Section;
import com.valinor.domain.model.Table;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.RestaurantRepository;
import com.valinor.repository.SectionRepository;
import com.valinor.repository.TableRepository;
import com.valinor.service.dto.restaurant.*;
import com.valinor.exception.LayoutValidationException;
import com.valinor.service.restaurant.RestaurantLayoutService;

import java.util.List;

/**
 * Demonstration of the Restaurant Layout Service layer.
 * Shows high-level operations using the service layer instead of direct repository access.
 */
public class RestaurantLayoutServiceDemo {
    
    public static void main(String[] args) {
        try {
            // Initialize repositories
            RestaurantRepository restaurantRepo = new RestaurantRepository("data/restaurants.csv");
            SectionRepository sectionRepo = new SectionRepository("data/sections.csv");
            TableRepository tableRepo = new TableRepository("data/tables.csv");
            
            // Initialize service layer
            RestaurantLayoutService layoutService = new RestaurantLayoutService(
                restaurantRepo, sectionRepo, tableRepo
            );
            
            System.out.println("=== RESTAURANT LAYOUT SERVICE DEMO ===\n");
            
            // Step 1: Create a restaurant
            System.out.println("--- Step 1: Create Restaurant ---");
            Restaurant restaurant = new Restaurant("Bella Vista", "456 Ocean Drive, Miami", 
                                                     "info@bellavista.com", "305-555-0199");
            restaurant = restaurantRepo.save(restaurant);
            System.out.println("Created: " + restaurant.getName() + " (ID: " + restaurant.getRestaurantId() + ")\n");
            
            // Step 2: Create sections using service layer
            System.out.println("--- Step 2: Create Sections using Service Layer ---");
            
            CreateSectionRequest patioRequest = new CreateSectionRequest(
                restaurant.getRestaurantId(), "Terrace");
            patioRequest.setNotes("Outdoor terrace with ocean view");
            Section terrace = layoutService.createSection(patioRequest);
            System.out.println("Created section: " + terrace.getName() + " (ID: " + terrace.getSectionId() + ")");
            
            CreateSectionRequest indoorRequest = new CreateSectionRequest(
                restaurant.getRestaurantId(), "Main Dining");
            indoorRequest.setNotes("Indoor dining area with piano");
            Section mainDining = layoutService.createSection(indoorRequest);
            System.out.println("Created section: " + mainDining.getName() + " (ID: " + mainDining.getSectionId() + ")");
            
            CreateSectionRequest privateRequest = new CreateSectionRequest(
                restaurant.getRestaurantId(), "Private Room");
            privateRequest.setNotes("Private dining room for events");
            Section privateRoom = layoutService.createSection(privateRequest);
            System.out.println("Created section: " + privateRoom.getName() + " (ID: " + privateRoom.getSectionId() + ")\n");
            
            // Step 3: Add tables using service layer
            System.out.println("--- Step 3: Add Tables using Service Layer ---");
            
            // Terrace tables
            System.out.println("\nTerrace Tables:");
            for (int i = 1; i <= 6; i++) {
                CreateTableRequest tableRequest = new CreateTableRequest(
                    terrace.getSectionId(), "T" + i, 4);
                Table table = layoutService.createTable(tableRequest);
                System.out.println("  Created: " + table.getTableNumber() + " (capacity: " + table.getCapacity() + ")");
            }
            
            // Main Dining tables
            System.out.println("\nMain Dining Tables:");
            for (int i = 1; i <= 10; i++) {
                int capacity = (i % 4 == 0) ? 8 : 6; // Every 4th table is an 8-top
                CreateTableRequest tableRequest = new CreateTableRequest(
                    mainDining.getSectionId(), "M" + i, capacity);
                Table table = layoutService.createTable(tableRequest);
                System.out.println("  Created: " + table.getTableNumber() + " (capacity: " + table.getCapacity() + ")");
            }
            
            // Private Room - one large table
            System.out.println("\nPrivate Room:");
            CreateTableRequest privateTableRequest = new CreateTableRequest(
                privateRoom.getSectionId(), "PR1", 12);
            Table privateTable = layoutService.createTable(privateTableRequest);
            System.out.println("  Created: " + privateTable.getTableNumber() + " (capacity: " + privateTable.getCapacity() + ")\n");
            
            // Step 4: Get complete restaurant layout
            System.out.println("--- Step 4: Get Complete Restaurant Layout ---");
            RestaurantLayoutDTO layout = layoutService.getRestaurantLayout(restaurant.getRestaurantId());
            System.out.println("Restaurant: " + layout.getName());
            System.out.println("Location: " + layout.getLocation());
            System.out.println("Sections: " + layout.getSectionCount());
            System.out.println("Total Tables: " + layout.getTotalTableCount());
            System.out.println("Total Capacity: " + layout.getTotalCapacity() + " seats");
            
            System.out.println("\nSection Details:");
            for (SectionWithTablesDTO section : layout.getSections()) {
                System.out.println("  - " + section.getName() + ": " + 
                                 section.getTableCount() + " tables, " +
                                 section.getTotalCapacity() + " seats");
            }
            System.out.println();
            
            // Step 5: Update a table
            System.out.println("--- Step 5: Update Table Capacity ---");
            SectionWithTablesDTO terraceSection = layoutService.getSectionWithTables(terrace.getSectionId());
            Table firstTerraceTable = terraceSection.getTables().get(0);
            
            System.out.println("Original capacity of " + firstTerraceTable.getTableNumber() + ": " + 
                             firstTerraceTable.getCapacity());
            
            UpdateTableRequest updateRequest = new UpdateTableRequest();
            updateRequest.setCapacity(6);
            Table updated = layoutService.updateTable(firstTerraceTable.getTableId(), updateRequest);
            
            System.out.println("Updated capacity of " + updated.getTableNumber() + ": " + 
                             updated.getCapacity() + "\n");
            
            // Step 6: Deactivate a table
            System.out.println("--- Step 6: Deactivate a Table ---");
            Table tableToDeactivate = terraceSection.getTables().get(1);
            System.out.println("Deactivating table: " + tableToDeactivate.getTableNumber());
            
            Table deactivated = layoutService.deactivateTable(tableToDeactivate.getTableId());
            System.out.println("Table " + deactivated.getTableNumber() + " active: " + deactivated.isActive());
            
            // Check active tables
            List<Table> activeTables = layoutService.getActiveTables(terrace.getSectionId());
            System.out.println("Active tables in " + terrace.getName() + ": " + activeTables.size() + "\n");
            
            // Step 7: Validate layout
            System.out.println("--- Step 7: Validate Restaurant Layout ---");
            List<String> issues = layoutService.validateLayout(restaurant.getRestaurantId());
            if (issues.isEmpty()) {
                System.out.println("✓ Layout is valid - no issues found");
            } else {
                System.out.println("Layout validation found " + issues.size() + " issue(s):");
                for (String issue : issues) {
                    System.out.println("  - " + issue);
                }
            }
            System.out.println();
            
            // Step 8: Update section
            System.out.println("--- Step 8: Update Section Information ---");
            UpdateSectionRequest sectionUpdate = new UpdateSectionRequest();
            sectionUpdate.setNotes("Outdoor terrace with stunning ocean views - reservation recommended");
            Section updatedSection = layoutService.updateSection(terrace.getSectionId(), sectionUpdate);
            System.out.println("Updated notes for " + updatedSection.getName());
            System.out.println("New notes: " + updatedSection.getNotes() + "\n");
            
            // Step 9: Test validation - try to create duplicate table number
            System.out.println("--- Step 9: Test Validation (Duplicate Table Number) ---");
            try {
                CreateTableRequest duplicateRequest = new CreateTableRequest(
                    terrace.getSectionId(), "T1", 4);
                layoutService.createTable(duplicateRequest);
                System.out.println("ERROR: Should have thrown validation exception!");
            } catch (LayoutValidationException e) {
                System.out.println("✓ Validation correctly prevented duplicate: " + e.getMessage());
            }
            System.out.println();
            
            // Step 10: Test deletion with cascade
            System.out.println("--- Step 10: Delete Section with Cascade ---");
            
            // Create a test section with tables
            CreateSectionRequest testRequest = new CreateSectionRequest(
                restaurant.getRestaurantId(), "Test Section");
            Section testSection = layoutService.createSection(testRequest);
            
            CreateTableRequest testTableRequest = new CreateTableRequest(
                testSection.getSectionId(), "TEST1", 2);
            layoutService.createTable(testTableRequest);
            
            System.out.println("Created test section with 1 table");
            
            // Try to delete without cascade (should fail)
            try {
                layoutService.deleteSection(testSection.getSectionId(), false);
                System.out.println("ERROR: Should have thrown validation exception!");
            } catch (LayoutValidationException e) {
                System.out.println("✓ Correctly prevented deletion: " + e.getMessage());
            }
            
            // Delete with cascade
            int deletedTables = layoutService.deleteSection(testSection.getSectionId(), true);
            System.out.println("✓ Deleted test section with " + deletedTables + " table(s)\n");
            
            // Step 11: Final layout summary
            System.out.println("--- Step 11: Final Layout Summary ---");
            layout = layoutService.getRestaurantLayout(restaurant.getRestaurantId());
            System.out.println("Restaurant: " + layout.getName());
            System.out.println("Total Sections: " + layout.getSectionCount());
            System.out.println("Total Tables: " + layout.getTotalTableCount());
            System.out.println("Active Tables: " + layout.getTotalActiveTableCount());
            System.out.println("Total Seating Capacity: " + layout.getTotalCapacity() + " seats");
            
            System.out.println("\n=== SERVICE LAYER DEMO COMPLETED SUCCESSFULLY! ===");
            
        } catch (RepositoryException | LayoutValidationException e) {
            System.err.println("Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

