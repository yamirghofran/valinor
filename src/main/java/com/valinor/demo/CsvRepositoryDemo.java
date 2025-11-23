package com.valinor.demo;

import com.valinor.domain.model.Restaurant;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;

/**
 * Simple demonstration of the CSV repository system.
 */
public class CsvRepositoryDemo {
    
    public static void main(String[] args) {
        try {
            // Initialize the repository
            RestaurantRepository repository = new RestaurantRepository("data/restaurants.csv");
            
            System.out.println("=== CSV Repository Demo ===");
            
            // Create some sample restaurants
            Restaurant restaurant1 = new Restaurant("The Grand Restaurant", "123 Main Street, Springfield", 
                                                      "contact@grandrestaurant.com", "555-0123");
            Restaurant restaurant2 = new Restaurant("Cozy Corner Cafe", "456 Oak Avenue", 
                                                      "info@cozycorner.com", "555-0456");
            
            // Save restaurants
            System.out.println("\n--- Saving restaurants ---");
            restaurant1 = repository.save(restaurant1);
            System.out.println("Saved: " + restaurant1);
            
            restaurant2 = repository.save(restaurant2);
            System.out.println("Saved: " + restaurant2);
            
            // Find all restaurants
            System.out.println("\n--- Finding all restaurants ---");
            List<Restaurant> allRestaurants = repository.findAll();
            System.out.println("Total restaurants: " + allRestaurants.size());
            for (Restaurant restaurant : allRestaurants) {
                System.out.println("  " + restaurant);
            }
            
            // Find by ID
            System.out.println("\n--- Finding restaurant by ID ---");
            Optional<Restaurant> foundRestaurant = repository.findById(1L);
            System.out.println("Found restaurant with ID 1: " + foundRestaurant.orElse(null));
            
            // Find by name
            System.out.println("\n--- Finding restaurants by name ---");
            List<Restaurant> grandRestaurants = repository.findByName("The Grand Restaurant");
            System.out.println("Restaurants named 'The Grand Restaurant': " + grandRestaurants.size());
            
            // Find by email
            System.out.println("\n--- Finding restaurant by email ---");
            Optional<Restaurant> byEmail = repository.findOneByContactEmail("contact@grandrestaurant.com");
            System.out.println("Restaurant by email: " + byEmail.orElse(null));
            
            // Update a restaurant
            System.out.println("\n--- Updating restaurant ---");
            if (foundRestaurant.isPresent()) {
                Restaurant toUpdate = foundRestaurant.get();
                toUpdate.setLocation("123 Main Street, Updated Location");
                Restaurant updated = repository.update(toUpdate);
                System.out.println("Updated: " + updated);
            }
            
            // Count restaurants
            System.out.println("\n--- Count ---");
            System.out.println("Total restaurants in database: " + repository.count());
            
            System.out.println("\n=== Demo completed successfully! ===");
            
        } catch (RepositoryException e) {
            System.err.println("Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}