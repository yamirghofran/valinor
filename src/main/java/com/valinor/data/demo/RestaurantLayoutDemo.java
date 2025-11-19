package com.valinor.data.demo;

import com.valinor.data.entity.Restaurant;
import com.valinor.data.entity.Section;
import com.valinor.data.entity.Table;
import com.valinor.data.exception.RepositoryException;
import com.valinor.data.repository.RestaurantRepository;
import com.valinor.data.repository.SectionRepository;
import com.valinor.data.repository.TableRepository;

import java.util.List;
import java.util.Optional;

/**
 * Comprehensive demonstration of the Restaurant Layout Management system.
 * Demonstrates creating a complete restaurant layout with sections and tables.
 */
public class RestaurantLayoutDemo {
    
    public static void main(String[] args) {
        try {
            // Initialize repositories
            RestaurantRepository restaurantRepo = new RestaurantRepository("data/restaurants.csv");
            SectionRepository sectionRepo = new SectionRepository("data/sections.csv");
            TableRepository tableRepo = new TableRepository("data/tables.csv");
            
            System.out.println("=== RESTAURANT LAYOUT MANAGEMENT DEMO ===\n");
            
            // Step 1: Create or get a restaurant
            System.out.println("--- Step 1: Create Restaurant ---");
            Restaurant restaurant = new Restaurant("The Grand Restaurant", "123 Main Street, Springfield", 
                                                     "contact@grandrestaurant.com", "555-0123");
            restaurant = restaurantRepo.save(restaurant);
            System.out.println("Created restaurant: " + restaurant);
            System.out.println("Restaurant ID: " + restaurant.getRestaurantId() + "\n");
            
            // Step 2: Create sections for the restaurant
            System.out.println("--- Step 2: Create Sections ---");
            
            Section patio = new Section(restaurant.getRestaurantId(), "Patio");
            patio.setNotes("Outdoor seating area");
            patio = sectionRepo.save(patio);
            System.out.println("Created section: " + patio);
            
            Section bar = new Section(restaurant.getRestaurantId(), "Bar");
            bar.setNotes("Bar counter and high-tops");
            bar = sectionRepo.save(bar);
            System.out.println("Created section: " + bar);
            
            Section diningRoom = new Section(restaurant.getRestaurantId(), "Dining Room");
            diningRoom.setNotes("Main indoor dining area");
            diningRoom = sectionRepo.save(diningRoom);
            System.out.println("Created section: " + diningRoom);
            System.out.println();
            
            // Step 3: Add tables to each section
            System.out.println("--- Step 3: Add Tables to Sections ---");
            
            // Patio tables
            System.out.println("\nPatio Tables:");
            for (int i = 1; i <= 8; i++) {
                Table table = new Table(patio.getSectionId(), "P" + i, 4);
                table = tableRepo.save(table);
                System.out.println("  Created: " + table.getTableNumber() + " (capacity: " + table.getCapacity() + ")");
            }
            
            // Bar tables
            System.out.println("\nBar Tables:");
            for (int i = 1; i <= 6; i++) {
                int capacity = (i <= 3) ? 2 : 4; // First 3 are 2-tops, rest are 4-tops
                Table table = new Table(bar.getSectionId(), "B" + i, capacity);
                table = tableRepo.save(table);
                System.out.println("  Created: " + table.getTableNumber() + " (capacity: " + table.getCapacity() + ")");
            }
            
            // Dining room tables
            System.out.println("\nDining Room Tables:");
            for (int i = 1; i <= 15; i++) {
                int capacity = (i % 3 == 0) ? 6 : 4; // Every 3rd table is a 6-top
                Table table = new Table(diningRoom.getSectionId(), "D" + i, capacity);
                table = tableRepo.save(table);
                System.out.println("  Created: " + table.getTableNumber() + " (capacity: " + table.getCapacity() + ")");
            }
            System.out.println();
            
            // Step 4: Query the layout
            System.out.println("--- Step 4: Query Restaurant Layout ---");
            
            List<Section> sections = sectionRepo.findByRestaurantId(restaurant.getRestaurantId());
            System.out.println("Restaurant: " + restaurant.getName());
            System.out.println("Total sections: " + sections.size());
            
            for (Section section : sections) {
                List<Table> tables = tableRepo.findBySectionId(section.getSectionId());
                System.out.println("\n  Section: " + section.getName());
                System.out.println("  Notes: " + section.getNotes());
                System.out.println("  Tables: " + tables.size());
                System.out.println("  Active tables: " + tableRepo.countActiveBySectionId(section.getSectionId()));
            }
            System.out.println();
            
            // Step 5: Test custom queries
            System.out.println("--- Step 5: Test Custom Queries ---");
            
            // Find a specific table
            Optional<Table> specificTable = tableRepo.findOneBySectionIdAndTableNumber(patio.getSectionId(), "P1");
            System.out.println("Found table P1 in Patio: " + specificTable.orElse(null));
            
            // Find tables by capacity
            List<Table> sixTops = tableRepo.findByCapacity(6);
            System.out.println("\nAll 6-person tables: " + sixTops.size());
            for (Table table : sixTops) {
                System.out.println("  " + table.getTableNumber() + " (Section ID: " + table.getSectionId() + ")");
            }
            
            // Find tables with minimum capacity
            List<Table> largeEnoughTables = tableRepo.findByMinCapacity(4);
            System.out.println("\nTables with capacity >= 4: " + largeEnoughTables.size());
            
            // Step 6: Update table properties
            System.out.println("\n--- Step 6: Update Table Properties ---");
            if (specificTable.isPresent()) {
                Table table = specificTable.get();
                System.out.println("Original capacity of P1: " + table.getCapacity());
                table.setCapacity(6);
                table = tableRepo.update(table);
                System.out.println("Updated capacity of P1: " + table.getCapacity());
            }
            
            // Step 7: Deactivate a table
            System.out.println("\n--- Step 7: Deactivate a Table ---");
            Optional<Table> tableToDeactivate = tableRepo.findOneBySectionIdAndTableNumber(bar.getSectionId(), "B1");
            if (tableToDeactivate.isPresent()) {
                Table table = tableToDeactivate.get();
                System.out.println("Table B1 active status before: " + table.isActive());
                table = tableRepo.deactivateTable(table.getTableId());
                System.out.println("Table B1 active status after: " + table.isActive());
                
                long activeTables = tableRepo.countActiveBySectionId(bar.getSectionId());
                System.out.println("Active tables in Bar section: " + activeTables);
            }
            
            // Step 8: Query available tables
            System.out.println("\n--- Step 8: Query Available Tables ---");
            List<Table> availableTables = tableRepo.findAvailableTablesBySectionId(bar.getSectionId());
            System.out.println("Available tables in Bar section: " + availableTables.size());
            for (Table table : availableTables) {
                System.out.println("  " + table.getTableNumber() + " (capacity: " + table.getCapacity() + ")");
            }
            
            // Step 9: Display final layout summary
            System.out.println("\n--- Step 9: Final Layout Summary ---");
            System.out.println("Restaurant: " + restaurant.getName());
            System.out.println("Total sections: " + sectionRepo.countByRestaurantId(restaurant.getRestaurantId()));
            System.out.println("Total tables: " + tableRepo.count());
            System.out.println("Total active tables: " + tableRepo.findAllActiveTables().size());
            System.out.println("Total inactive tables: " + tableRepo.findAllInactiveTables().size());
            
            System.out.println("\n=== DEMO COMPLETED SUCCESSFULLY! ===");
            
        } catch (RepositoryException e) {
            System.err.println("Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

