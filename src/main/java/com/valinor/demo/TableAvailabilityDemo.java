package com.valinor.demo;

import com.valinor.domain.model.Restaurant;
import com.valinor.domain.model.Section;
import com.valinor.domain.model.Table;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.RestaurantRepository;
import com.valinor.repository.SectionRepository;
import com.valinor.repository.TableRepository;
import com.valinor.service.restaurant.TableAvailabilityService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Demonstration of the TableAvailabilityService.
 * Shows how to check table availability and prepare for reservation integration.
 */
public class TableAvailabilityDemo {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("TABLE AVAILABILITY SERVICE DEMONSTRATION");
        System.out.println("Integration Point for Future Reservation Module");
        System.out.println("=".repeat(80));
        System.out.println();
        
        try {
            // Initialize repositories
            RestaurantRepository restaurantRepo = new RestaurantRepository("data/restaurants.csv");
            SectionRepository sectionRepo = new SectionRepository("data/sections.csv");
            TableRepository tableRepo = new TableRepository("data/tables.csv");
            
            // Initialize availability service
            TableAvailabilityService availabilityService = new TableAvailabilityService(
                    tableRepo, sectionRepo);
            
            // Setup: Create test data if needed
            setupTestData(restaurantRepo, sectionRepo, tableRepo);
            
            // Get the restaurant
            List<Restaurant> restaurants = restaurantRepo.findAll();
            if (restaurants.isEmpty()) {
                System.out.println("❌ No restaurants found. Please run RestaurantLayoutDemo first.");
                return;
            }
            
            Restaurant restaurant = restaurants.get(0);
            Long restaurantId = restaurant.getRestaurantId();
            
            System.out.println("🏪 Restaurant: " + restaurant.getName());
            System.out.println();
            
            // Demo 1: Check specific table availability
            demonstrateTableAvailability(availabilityService, tableRepo, restaurantId);
            
            // Demo 2: Find available tables for party size
            demonstrateAvailableTableSearch(availabilityService, restaurantId);
            
            // Demo 3: Validate table assignments
            demonstrateTableAssignmentValidation(availabilityService, tableRepo, restaurantId);
            
            // Demo 4: Find optimal table
            demonstrateOptimalTableSelection(availabilityService, restaurantId);
            
            // Demo 5: Check capacity
            demonstrateCapacityCheck(availabilityService, restaurantId);
            
            // Demo 6: Suggest alternatives
            demonstrateAlternativeSuggestions(availabilityService, tableRepo, restaurantId);
            
            System.out.println("=".repeat(80));
            System.out.println("✅ ALL DEMONSTRATIONS COMPLETED SUCCESSFULLY");
            System.out.println("=".repeat(80));
            System.out.println();
            System.out.println("📋 NEXT STEPS:");
            System.out.println("   1. Implement Reservation entity");
            System.out.println("   2. Implement ReservationRepository with conflict detection");
            System.out.println("   3. Inject ReservationRepository into TableAvailabilityService");
            System.out.println("   4. Uncomment TODO sections for reservation conflict checking");
            System.out.println();
            System.out.println("📖 See RESERVATION_INTEGRATION.md for complete integration contract");
            
        } catch (RepositoryException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void setupTestData(RestaurantRepository restaurantRepo,
                                       SectionRepository sectionRepo,
                                       TableRepository tableRepo) throws RepositoryException {
        // Ensure we have test data
        List<Restaurant> restaurants = restaurantRepo.findAll();
        if (restaurants.isEmpty()) {
            System.out.println("⚠️  No test data found. Creating sample restaurant layout...");
            
            // Create restaurant
            Restaurant restaurant = new Restaurant();
            restaurant.setName("Demo Restaurant");
            restaurant.setLocation("123 Main St");
            restaurant.setContactPhone("555-1234");
            restaurant = restaurantRepo.save(restaurant);
            
            // Create main dining section
            Section mainDining = new Section();
            mainDining.setRestaurantId(restaurant.getRestaurantId());
            mainDining.setName("Main Dining");
            mainDining = sectionRepo.save(mainDining);
            
            // Create tables
            for (int i = 1; i <= 10; i++) {
                Table table = new Table();
                table.setSectionId(mainDining.getSectionId());
                table.setTableNumber("T" + i);
                table.setCapacity(i <= 5 ? 2 : 4); // Tables 1-5: capacity 2, Tables 6-10: capacity 4
                table.setIsActive(true);
                tableRepo.save(table);
            }
            
            // Create one inactive table
            Table inactiveTable = new Table();
            inactiveTable.setSectionId(mainDining.getSectionId());
            inactiveTable.setTableNumber("T99");
            inactiveTable.setCapacity(6);
            inactiveTable.setIsActive(false);
            tableRepo.save(inactiveTable);
            
            System.out.println("✅ Test data created");
            System.out.println();
        }
    }
    
    private static void demonstrateTableAvailability(TableAvailabilityService service,
                                                      TableRepository tableRepo,
                                                      Long restaurantId) throws RepositoryException {
        System.out.println("─".repeat(80));
        System.out.println("DEMO 1: CHECK SPECIFIC TABLE AVAILABILITY");
        System.out.println("─".repeat(80));
        
        LocalDateTime requestedTime = LocalDateTime.of(2025, 11, 20, 19, 0); // 7:00 PM
        
        List<Table> allTables = tableRepo.findAll();
        
        if (allTables.size() >= 2) {
            // Check active table
            Table activeTable = allTables.stream()
                    .filter(Table::isActive)
                    .findFirst()
                    .orElse(null);
            
            if (activeTable != null) {
                boolean available = service.isTableAvailable(activeTable.getTableId(), requestedTime);
                System.out.println("📍 Table: " + activeTable.getTableNumber());
                System.out.println("   Status: " + (activeTable.isActive() ? "Active" : "Inactive"));
                System.out.println("   Capacity: " + activeTable.getCapacity());
                System.out.println("   Available at " + requestedTime + ": " + 
                                 (available ? "✅ YES" : "❌ NO"));
                System.out.println();
            }
            
            // Check inactive table
            Table inactiveTable = allTables.stream()
                    .filter(t -> !t.isActive())
                    .findFirst()
                    .orElse(null);
            
            if (inactiveTable != null) {
                boolean available = service.isTableAvailable(inactiveTable.getTableId(), requestedTime);
                System.out.println("📍 Table: " + inactiveTable.getTableNumber());
                System.out.println("   Status: " + (inactiveTable.isActive() ? "Active" : "Inactive"));
                System.out.println("   Capacity: " + inactiveTable.getCapacity());
                System.out.println("   Available at " + requestedTime + ": " + 
                                 (available ? "✅ YES" : "❌ NO"));
                System.out.println("   ⚠️  Inactive tables are not available");
                System.out.println();
            }
        }
        
        System.out.println("💡 NOTE: Once reservation module is implemented, this will also check");
        System.out.println("   for conflicting reservations at the requested time.");
        System.out.println();
    }
    
    private static void demonstrateAvailableTableSearch(TableAvailabilityService service,
                                                         Long restaurantId) throws RepositoryException {
        System.out.println("─".repeat(80));
        System.out.println("DEMO 2: FIND AVAILABLE TABLES FOR PARTY SIZE");
        System.out.println("─".repeat(80));
        
        LocalDateTime requestedTime = LocalDateTime.of(2025, 11, 20, 19, 30);
        
        // Search for different party sizes
        int[] partySizes = {2, 4, 6};
        
        for (int partySize : partySizes) {
            List<Table> available = service.getAvailableTables(restaurantId, requestedTime, partySize);
            
            System.out.println("🔍 Party Size: " + partySize);
            System.out.println("   Requested Time: " + requestedTime);
            System.out.println("   Available Tables: " + available.size());
            
            if (!available.isEmpty()) {
                System.out.println("   Tables:");
                for (Table table : available) {
                    System.out.println("      • " + table.getTableNumber() + 
                                     " (Capacity: " + table.getCapacity() + ")");
                }
            } else {
                System.out.println("   ⚠️  No tables available for this party size");
            }
            System.out.println();
        }
        
        System.out.println("💡 NOTE: Currently filters by active status and capacity.");
        System.out.println("   Future: Will also filter out tables with conflicting reservations.");
        System.out.println();
    }
    
    private static void demonstrateTableAssignmentValidation(TableAvailabilityService service,
                                                              TableRepository tableRepo,
                                                              Long restaurantId) throws RepositoryException {
        System.out.println("─".repeat(80));
        System.out.println("DEMO 3: VALIDATE TABLE ASSIGNMENTS");
        System.out.println("─".repeat(80));
        
        LocalDateTime requestedTime = LocalDateTime.of(2025, 11, 20, 20, 0);
        
        List<Table> allTables = tableRepo.findAll();
        
        if (!allTables.isEmpty()) {
            Table table = allTables.get(0);
            
            // Valid assignment
            int validPartySize = 2;
            boolean valid = service.validateTableAssignment(table.getTableId(), requestedTime, validPartySize);
            System.out.println("✓ Validation Test 1: Valid Assignment");
            System.out.println("   Table: " + table.getTableNumber());
            System.out.println("   Capacity: " + table.getCapacity());
            System.out.println("   Party Size: " + validPartySize);
            System.out.println("   Result: " + (valid ? "✅ VALID" : "❌ INVALID"));
            System.out.println();
            
            // Invalid assignment - party too large
            int invalidPartySize = table.getCapacity() + 10;
            valid = service.validateTableAssignment(table.getTableId(), requestedTime, invalidPartySize);
            System.out.println("✓ Validation Test 2: Party Too Large");
            System.out.println("   Table: " + table.getTableNumber());
            System.out.println("   Capacity: " + table.getCapacity());
            System.out.println("   Party Size: " + invalidPartySize);
            System.out.println("   Result: " + (valid ? "✅ VALID" : "❌ INVALID (Expected)"));
            System.out.println();
            
            // Check inactive table
            Table inactiveTable = allTables.stream()
                    .filter(t -> !t.isActive())
                    .findFirst()
                    .orElse(null);
            
            if (inactiveTable != null) {
                valid = service.validateTableAssignment(inactiveTable.getTableId(), requestedTime, 2);
                System.out.println("✓ Validation Test 3: Inactive Table");
                System.out.println("   Table: " + inactiveTable.getTableNumber());
                System.out.println("   Status: Inactive");
                System.out.println("   Party Size: 2");
                System.out.println("   Result: " + (valid ? "✅ VALID" : "❌ INVALID (Expected)"));
                System.out.println();
            }
        }
        
        System.out.println("💡 NOTE: Future implementation will also validate no conflicting reservations.");
        System.out.println();
    }
    
    private static void demonstrateOptimalTableSelection(TableAvailabilityService service,
                                                          Long restaurantId) throws RepositoryException {
        System.out.println("─".repeat(80));
        System.out.println("DEMO 4: FIND OPTIMAL TABLE (BEST FIT)");
        System.out.println("─".repeat(80));
        
        LocalDateTime requestedTime = LocalDateTime.of(2025, 11, 20, 18, 0);
        
        int[] partySizes = {2, 4, 6};
        
        for (int partySize : partySizes) {
            Optional<Table> optimal = service.getOptimalTable(restaurantId, requestedTime, partySize);
            
            System.out.println("🎯 Party Size: " + partySize);
            if (optimal.isPresent()) {
                Table table = optimal.get();
                System.out.println("   Optimal Table: " + table.getTableNumber());
                System.out.println("   Capacity: " + table.getCapacity());
                System.out.println("   ✅ Best fit: smallest table that accommodates party");
            } else {
                System.out.println("   ❌ No suitable table found");
            }
            System.out.println();
        }
        
        System.out.println("💡 NOTE: Algorithm selects smallest table that can accommodate party");
        System.out.println("   to optimize table utilization and preserve larger tables for bigger parties.");
        System.out.println();
    }
    
    private static void demonstrateCapacityCheck(TableAvailabilityService service,
                                                  Long restaurantId) throws RepositoryException {
        System.out.println("─".repeat(80));
        System.out.println("DEMO 5: CHECK AVAILABLE CAPACITY");
        System.out.println("─".repeat(80));
        
        LocalDateTime[] times = {
            LocalDateTime.of(2025, 11, 20, 12, 0), // Lunch
            LocalDateTime.of(2025, 11, 20, 18, 0), // Dinner
            LocalDateTime.of(2025, 11, 20, 21, 0)  // Late
        };
        
        for (LocalDateTime time : times) {
            int capacity = service.getAvailableCapacity(restaurantId, time);
            System.out.println("⏰ Time: " + time);
            System.out.println("   Available Capacity: " + capacity + " seats");
            System.out.println();
        }
        
        System.out.println("💡 NOTE: This shows total capacity of all available tables.");
        System.out.println("   Future: Capacity will decrease based on active reservations.");
        System.out.println();
    }
    
    private static void demonstrateAlternativeSuggestions(TableAvailabilityService service,
                                                           TableRepository tableRepo,
                                                           Long restaurantId) throws RepositoryException {
        System.out.println("─".repeat(80));
        System.out.println("DEMO 6: SUGGEST ALTERNATIVE TABLES");
        System.out.println("─".repeat(80));
        
        LocalDateTime requestedTime = LocalDateTime.of(2025, 11, 20, 19, 0);
        
        List<Table> allTables = tableRepo.findAll();
        if (!allTables.isEmpty()) {
            Table requestedTable = allTables.stream()
                    .filter(Table::isActive)
                    .findFirst()
                    .orElse(null);
            
            if (requestedTable != null) {
                int partySize = 2;
                
                System.out.println("📍 Customer requested: " + requestedTable.getTableNumber());
                System.out.println("   (Simulating: Table not available)");
                System.out.println();
                
                List<Table> alternatives = service.suggestAlternativeTables(
                        requestedTable.getTableId(), requestedTime, partySize);
                
                System.out.println("🔄 Suggested Alternatives:");
                if (!alternatives.isEmpty()) {
                    System.out.println("   Found " + alternatives.size() + " alternative(s):");
                    for (int i = 0; i < Math.min(5, alternatives.size()); i++) {
                        Table alt = alternatives.get(i);
                        System.out.println("      " + (i + 1) + ". " + alt.getTableNumber() + 
                                         " (Capacity: " + alt.getCapacity() + ")");
                    }
                } else {
                    System.out.println("   ❌ No alternatives available");
                }
                System.out.println();
                
                System.out.println("💡 NOTE: Algorithm prioritizes tables in the same section,");
                System.out.println("   then suggests tables in other sections.");
            }
        }
        System.out.println();
    }
}

