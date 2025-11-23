package com.valinor.repository;

import com.valinor.domain.model.Restaurant;
import com.valinor.exception.EntityValidationException;
import com.valinor.exception.RepositoryException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RestaurantRepository.
 * Tests all CRUD operations and query methods.
 */
class RestaurantRepositoryTest {
    
    @TempDir
    Path tempDir;
    
    private RestaurantRepository repository;
    private Path testFilePath;
    
    @BeforeEach
    void setUp() throws RepositoryException {
        testFilePath = tempDir.resolve("test_restaurants.csv");
        repository = new RestaurantRepository(testFilePath.toString());
    }
    
    @AfterEach
    void tearDown() throws RepositoryException {
        if (repository != null) {
            // Clean up test file
            try {
                repository.deleteAllById(repository.findAll().stream()
                    .map(Restaurant::getRestaurantId)
                    .filter(Objects::nonNull)
                    .toList());
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }
    
    @Test
    @DisplayName("Should create repository successfully")
    void testRepositoryCreation() {
        assertNotNull(repository);
        assertEquals(testFilePath.toString(), repository.getFilePath());
    }
    
    @Test
    @DisplayName("Should save new restaurant without ID")
    void testSaveNewRestaurant() throws RepositoryException {
        Restaurant restaurant = new Restaurant("Test Restaurant", "New York", 
                                               "test@restaurant.com", "555-1234");
        
        Restaurant saved = repository.save(restaurant);
        
        assertNotNull(saved);
        assertNotNull(saved.getRestaurantId());
        assertEquals("Test Restaurant", saved.getName());
        assertEquals("New York", saved.getLocation());
    }
    
    @Test
    @DisplayName("Should save restaurant with ID")
    void testSaveRestaurantWithId() throws RepositoryException {
        Restaurant restaurant = new Restaurant(100L, "Test Restaurant", "New York", 
                                               "test@restaurant.com", "555-1234");
        
        Restaurant saved = repository.save(restaurant);
        
        assertNotNull(saved);
        assertEquals(100L, saved.getRestaurantId());
    }
    
    @Test
    @DisplayName("Should throw exception when saving invalid restaurant")
    void testSaveInvalidRestaurant() {
        Restaurant restaurant = new Restaurant(null, null, "Location", 
                                               "test@example.com", "555-1234");
        
        assertThrows(EntityValidationException.class, () -> repository.save(restaurant));
    }
    
    @Test
    @DisplayName("Should find restaurant by ID")
    void testFindById() throws RepositoryException {
        Restaurant restaurant = new Restaurant("Test Restaurant", "New York", 
                                               "test@restaurant.com", "555-1234");
        Restaurant saved = repository.save(restaurant);
        
        Optional<Restaurant> found = repository.findById(saved.getRestaurantId());
        
        assertTrue(found.isPresent());
        assertEquals(saved.getRestaurantId(), found.get().getRestaurantId());
        assertEquals("Test Restaurant", found.get().getName());
    }
    
    @Test
    @DisplayName("Should return empty optional when restaurant not found")
    void testFindByIdNotFound() throws RepositoryException {
        Optional<Restaurant> found = repository.findById(999L);
        
        assertFalse(found.isPresent());
    }
    
    @Test
    @DisplayName("Should find all restaurants")
    void testFindAll() throws RepositoryException {
        Restaurant r1 = new Restaurant("Restaurant 1", "New York", 
                                       "r1@example.com", "555-0001");
        Restaurant r2 = new Restaurant("Restaurant 2", "Boston", 
                                       "r2@example.com", "555-0002");
        
        repository.save(r1);
        repository.save(r2);
        
        List<Restaurant> all = repository.findAll();
        
        assertEquals(2, all.size());
    }
    
    @Test
    @DisplayName("Should return empty list when no restaurants exist")
    void testFindAllEmpty() throws RepositoryException {
        List<Restaurant> all = repository.findAll();
        
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }
    
    @Test
    @DisplayName("Should update existing restaurant")
    void testUpdate() throws RepositoryException {
        Restaurant restaurant = new Restaurant("Original Name", "New York", 
                                               "test@restaurant.com", "555-1234");
        Restaurant saved = repository.save(restaurant);
        
        saved.setName("Updated Name");
        saved.setLocation("Boston");
        Restaurant updated = repository.update(saved);
        
        assertEquals("Updated Name", updated.getName());
        assertEquals("Boston", updated.getLocation());
        
        // Verify persistence
        Optional<Restaurant> found = repository.findById(saved.getRestaurantId());
        assertTrue(found.isPresent());
        assertEquals("Updated Name", found.get().getName());
    }
    
    @Test
    @DisplayName("Should throw exception when updating non-existent restaurant")
    void testUpdateNonExistent() {
        Restaurant restaurant = new Restaurant(999L, "Test", "Location", 
                                               "test@example.com", "555-1234");
        
        assertThrows(RepositoryException.class, () -> repository.update(restaurant));
    }
    
    @Test
    @DisplayName("Should throw exception when updating restaurant without ID")
    void testUpdateWithoutId() {
        Restaurant restaurant = new Restaurant(null, "Test", "Location", 
                                               "test@example.com", "555-1234");
        
        assertThrows(RepositoryException.class, () -> repository.update(restaurant));
    }
    
    @Test
    @DisplayName("Should delete restaurant by ID")
    void testDeleteById() throws RepositoryException {
        Restaurant restaurant = new Restaurant("Test Restaurant", "New York", 
                                               "test@restaurant.com", "555-1234");
        Restaurant saved = repository.save(restaurant);
        
        boolean deleted = repository.deleteById(saved.getRestaurantId());
        
        assertTrue(deleted);
        assertFalse(repository.findById(saved.getRestaurantId()).isPresent());
    }
    
    @Test
    @DisplayName("Should return false when deleting non-existent restaurant")
    void testDeleteByIdNotFound() throws RepositoryException {
        boolean deleted = repository.deleteById(999L);
        
        assertFalse(deleted);
    }
    
    @Test
    @DisplayName("Should check if restaurant exists by ID")
    void testExistsById() throws RepositoryException {
        Restaurant restaurant = new Restaurant("Test Restaurant", "New York", 
                                               "test@restaurant.com", "555-1234");
        Restaurant saved = repository.save(restaurant);
        
        assertTrue(repository.existsById(saved.getRestaurantId()));
        assertFalse(repository.existsById(999L));
    }
    
    @Test
    @DisplayName("Should count restaurants")
    void testCount() throws RepositoryException {
        assertEquals(0, repository.count());
        
        repository.save(new Restaurant("Restaurant 1", "New York", 
                                       "r1@example.com", "555-0001"));
        repository.save(new Restaurant("Restaurant 2", "Boston", 
                                       "r2@example.com", "555-0002"));
        
        assertEquals(2, repository.count());
    }
    
    @Test
    @DisplayName("Should find restaurants by name")
    void testFindByName() throws RepositoryException {
        Restaurant r1 = new Restaurant("Pizza Place", "New York", 
                                       "pizza@example.com", "555-0001");
        Restaurant r2 = new Restaurant("Burger Joint", "Boston", 
                                       "burger@example.com", "555-0002");
        
        repository.save(r1);
        repository.save(r2);
        
        List<Restaurant> found = repository.findByName("Pizza Place");
        
        assertEquals(1, found.size());
        assertEquals("Pizza Place", found.get(0).getName());
    }
    
    @Test
    @DisplayName("Should find restaurants by location")
    void testFindByLocation() throws RepositoryException {
        Restaurant r1 = new Restaurant("Restaurant 1", "New York", 
                                       "r1@example.com", "555-0001");
        Restaurant r2 = new Restaurant("Restaurant 2", "New York", 
                                       "r2@example.com", "555-0002");
        Restaurant r3 = new Restaurant("Restaurant 3", "Boston", 
                                       "r3@example.com", "555-0003");
        
        repository.save(r1);
        repository.save(r2);
        repository.save(r3);
        
        List<Restaurant> found = repository.findByLocation("New York");
        
        assertEquals(2, found.size());
    }
    
    @Test
    @DisplayName("Should find restaurant by contact email")
    void testFindByContactEmail() throws RepositoryException {
        Restaurant r1 = new Restaurant("Restaurant 1", "New York", 
                                       "unique@example.com", "555-0001");
        repository.save(r1);
        
        List<Restaurant> found = repository.findByContactEmail("unique@example.com");
        
        assertEquals(1, found.size());
        assertEquals("unique@example.com", found.get(0).getContactEmail());
    }
    
    @Test
    @DisplayName("Should find one restaurant by contact email")
    void testFindOneByContactEmail() throws RepositoryException {
        Restaurant r1 = new Restaurant("Restaurant 1", "New York", 
                                       "unique@example.com", "555-0001");
        repository.save(r1);
        
        Optional<Restaurant> found = repository.findOneByContactEmail("unique@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("unique@example.com", found.get().getContactEmail());
    }
    
    @Test
    @DisplayName("Should return empty when finding by non-existent email")
    void testFindOneByContactEmailNotFound() throws RepositoryException {
        Optional<Restaurant> found = repository.findOneByContactEmail("nonexistent@example.com");
        
        assertFalse(found.isPresent());
    }
    
    @Test
    @DisplayName("Should find restaurants by field")
    void testFindByField() throws RepositoryException {
        Restaurant r1 = new Restaurant("Test Restaurant", "New York", 
                                       "test@example.com", "555-1234");
        repository.save(r1);
        
        List<Restaurant> found = repository.findByField("contact_phone", "555-1234");
        
        assertEquals(1, found.size());
        assertEquals("555-1234", found.get(0).getContactPhone());
    }
    
    @Test
    @DisplayName("Should find restaurants by multiple fields")
    void testFindByFields() throws RepositoryException {
        Restaurant r1 = new Restaurant("Restaurant 1", "New York", 
                                       "r1@example.com", "555-0001");
        Restaurant r2 = new Restaurant("Restaurant 2", "New York", 
                                       "r2@example.com", "555-0002");
        Restaurant r3 = new Restaurant("Restaurant 3", "Boston", 
                                       "r3@example.com", "555-0003");
        
        repository.save(r1);
        repository.save(r2);
        repository.save(r3);
        
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("location", "New York");
        
        List<Restaurant> found = repository.findByFields(criteria);
        
        assertEquals(2, found.size());
    }
    
    @Test
    @DisplayName("Should find restaurants with predicate")
    void testFindWhere() throws RepositoryException {
        Restaurant r1 = new Restaurant("Pizza Place", "New York", 
                                       "pizza@example.com", "555-0001");
        Restaurant r2 = new Restaurant("Burger Joint", "Boston", 
                                       "burger@example.com", "555-0002");
        
        repository.save(r1);
        repository.save(r2);
        
        List<Restaurant> found = repository.findWhere(r -> r.getName().contains("Pizza"));
        
        assertEquals(1, found.size());
        assertEquals("Pizza Place", found.get(0).getName());
    }
    
    @Test
    @DisplayName("Should save all restaurants")
    void testSaveAll() throws RepositoryException {
        List<Restaurant> restaurants = Arrays.asList(
            new Restaurant("Restaurant 1", "New York", "r1@example.com", "555-0001"),
            new Restaurant("Restaurant 2", "Boston", "r2@example.com", "555-0002"),
            new Restaurant("Restaurant 3", "Chicago", "r3@example.com", "555-0003")
        );
        
        List<Restaurant> saved = repository.saveAll(restaurants);
        
        assertEquals(3, saved.size());
        assertEquals(3, repository.count());
        
        // Verify all have IDs
        for (Restaurant r : saved) {
            assertNotNull(r.getRestaurantId());
        }
    }
    
    @Test
    @DisplayName("Should delete all by IDs")
    void testDeleteAllById() throws RepositoryException {
        Restaurant r1 = repository.save(new Restaurant("Restaurant 1", "New York", 
                                                       "r1@example.com", "555-0001"));
        Restaurant r2 = repository.save(new Restaurant("Restaurant 2", "Boston", 
                                                       "r2@example.com", "555-0002"));
        Restaurant r3 = repository.save(new Restaurant("Restaurant 3", "Chicago", 
                                                       "r3@example.com", "555-0003"));
        
        List<Long> idsToDelete = Arrays.asList(r1.getRestaurantId(), r2.getRestaurantId());
        int deletedCount = repository.deleteAllById(idsToDelete);
        
        assertEquals(2, deletedCount);
        assertEquals(1, repository.count());
        assertTrue(repository.existsById(r3.getRestaurantId()));
    }
    
    @Test
    @DisplayName("Should delete by fields")
    void testDeleteByFields() throws RepositoryException {
        repository.save(new Restaurant("Restaurant 1", "New York", 
                                      "r1@example.com", "555-0001"));
        repository.save(new Restaurant("Restaurant 2", "New York", 
                                      "r2@example.com", "555-0002"));
        repository.save(new Restaurant("Restaurant 3", "Boston", 
                                      "r3@example.com", "555-0003"));
        
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("location", "New York");
        
        int deletedCount = repository.deleteByFields(criteria);
        
        assertEquals(2, deletedCount);
        assertEquals(1, repository.count());
    }
    
    @Test
    @DisplayName("Should reload data from file")
    void testReload() throws RepositoryException {
        Restaurant r1 = repository.save(new Restaurant("Restaurant 1", "New York", 
                                                       "r1@example.com", "555-0001"));
        
        // Reload
        repository.reload();
        
        // Data should still be there
        Optional<Restaurant> found = repository.findById(r1.getRestaurantId());
        assertTrue(found.isPresent());
    }
    
    @Test
    @DisplayName("Should handle concurrent saves")
    void testConcurrentSaves() throws InterruptedException {
        Thread[] threads = new Thread[5];
        boolean[] success = new boolean[5];
        
        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    Restaurant r = new Restaurant("Restaurant " + index, "Location " + index, 
                                                 "r" + index + "@example.com", "555-000" + index);
                    repository.save(r);
                    success[index] = true;
                } catch (RepositoryException e) {
                    success[index] = false;
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // All saves should succeed
        for (boolean s : success) {
            assertTrue(s);
        }
    }
    
    @Test
    @DisplayName("Should generate sequential IDs")
    void testSequentialIdGeneration() throws RepositoryException {
        Restaurant r1 = repository.save(new Restaurant("Restaurant 1", "New York", 
                                                       "r1@example.com", "555-0001"));
        Restaurant r2 = repository.save(new Restaurant("Restaurant 2", "Boston", 
                                                       "r2@example.com", "555-0002"));
        
        assertNotNull(r1.getRestaurantId());
        assertNotNull(r2.getRestaurantId());
        assertTrue(r2.getRestaurantId() > r1.getRestaurantId());
    }
    
    @Test
    @DisplayName("Should persist data across repository instances")
    void testPersistenceAcrossInstances() throws RepositoryException {
        Restaurant r1 = repository.save(new Restaurant("Test Restaurant", "New York", 
                                                       "test@example.com", "555-1234"));
        Long savedId = r1.getRestaurantId();
        
        // Create new repository instance pointing to same file
        RestaurantRepository newRepository = new RestaurantRepository(testFilePath.toString());
        
        Optional<Restaurant> found = newRepository.findById(savedId);
        assertTrue(found.isPresent());
        assertEquals("Test Restaurant", found.get().getName());
    }
    
    @Test
    @DisplayName("Should handle empty field values")
    void testFieldValueMatching() throws RepositoryException {
        Restaurant r1 = new Restaurant("Test", "Location", 
                                      "test@example.com", "555-1234");
        repository.save(r1);
        
        List<Restaurant> found = repository.findByField("name", "Test");
        assertEquals(1, found.size());
        
        List<Restaurant> notFound = repository.findByField("name", "NonExistent");
        assertTrue(notFound.isEmpty());
    }
}
