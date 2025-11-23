package com.valinor.repository;

import com.valinor.domain.model.Restaurant;
import com.valinor.exception.EntityValidationException;
import com.valinor.exception.RepositoryException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AbstractCsvRepository base functionality.
 * Uses RestaurantRepository as a concrete implementation for testing.
 */
class AbstractCsvRepositoryTest {
    
    @TempDir
    Path tempDir;
    
    private RestaurantRepository repository;
    private Path testFilePath;
    
    @BeforeEach
    void setUp() throws RepositoryException {
        testFilePath = tempDir.resolve("test_abstract.csv");
        repository = new RestaurantRepository(testFilePath.toString());
    }
    
    @Test
    @DisplayName("Should initialize repository with empty cache")
    void testInitialization() throws RepositoryException {
        assertEquals(0, repository.count());
        assertTrue(repository.findAll().isEmpty());
    }
    
    @Test
    @DisplayName("Should create CSV file with header on initialization")
    void testFileCreation() {
        assertTrue(java.nio.file.Files.exists(testFilePath));
    }
    
    @Test
    @DisplayName("Should handle save operation with ID generation")
    void testSaveWithIdGeneration() throws RepositoryException {
        Restaurant restaurant = new Restaurant("Test", "Location", 
                                               "test@example.com", "555-1234");
        
        Restaurant saved = repository.save(restaurant);
        
        assertNotNull(saved.getRestaurantId());
        assertTrue(saved.getRestaurantId() > 0);
    }
    
    @Test
    @DisplayName("Should validate entity before save")
    void testSaveValidation() {
        Restaurant invalidRestaurant = new Restaurant(null, null, "Location", 
                                                     "test@example.com", "555-1234");
        
        assertThrows(EntityValidationException.class, () -> repository.save(invalidRestaurant));
    }
    
    @Test
    @DisplayName("Should persist entity to file after save")
    void testPersistence() throws RepositoryException {
        Restaurant restaurant = new Restaurant("Test", "Location", 
                                               "test@example.com", "555-1234");
        Restaurant saved = repository.save(restaurant);
        
        // Create new repository instance
        RestaurantRepository newRepo = new RestaurantRepository(testFilePath.toString());
        Optional<Restaurant> found = newRepo.findById(saved.getRestaurantId());
        
        assertTrue(found.isPresent());
        assertEquals(saved.getName(), found.get().getName());
    }
    
    @Test
    @DisplayName("Should handle findById with cache")
    void testFindByIdWithCache() throws RepositoryException {
        Restaurant restaurant = new Restaurant("Test", "Location", 
                                               "test@example.com", "555-1234");
        Restaurant saved = repository.save(restaurant);
        
        // First call loads from cache
        Optional<Restaurant> found1 = repository.findById(saved.getRestaurantId());
        // Second call should also use cache
        Optional<Restaurant> found2 = repository.findById(saved.getRestaurantId());
        
        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertEquals(found1.get().getRestaurantId(), found2.get().getRestaurantId());
    }
    
    @Test
    @DisplayName("Should return all entities from cache")
    void testFindAllFromCache() throws RepositoryException {
        repository.save(new Restaurant("R1", "L1", "r1@example.com", "555-0001"));
        repository.save(new Restaurant("R2", "L2", "r2@example.com", "555-0002"));
        repository.save(new Restaurant("R3", "L3", "r3@example.com", "555-0003"));
        
        List<Restaurant> all = repository.findAll();
        
        assertEquals(3, all.size());
    }
    
    @Test
    @DisplayName("Should update entity and persist changes")
    void testUpdatePersistence() throws RepositoryException {
        Restaurant restaurant = new Restaurant("Original", "Location", 
                                               "test@example.com", "555-1234");
        Restaurant saved = repository.save(restaurant);
        
        saved.setName("Updated");
        repository.update(saved);
        
        // Verify in new repository instance
        RestaurantRepository newRepo = new RestaurantRepository(testFilePath.toString());
        Optional<Restaurant> found = newRepo.findById(saved.getRestaurantId());
        
        assertTrue(found.isPresent());
        assertEquals("Updated", found.get().getName());
    }
    
    @Test
    @DisplayName("Should throw exception when updating non-existent entity")
    void testUpdateNonExistent() {
        Restaurant restaurant = new Restaurant(999L, "Test", "Location", 
                                               "test@example.com", "555-1234");
        
        assertThrows(RepositoryException.class, () -> repository.update(restaurant));
    }
    
    @Test
    @DisplayName("Should throw exception when updating entity without ID")
    void testUpdateWithoutId() {
        Restaurant restaurant = new Restaurant(null, "Test", "Location", 
                                               "test@example.com", "555-1234");
        
        assertThrows(RepositoryException.class, () -> repository.update(restaurant));
    }
    
    @Test
    @DisplayName("Should delete entity and persist changes")
    void testDeletePersistence() throws RepositoryException {
        Restaurant restaurant = new Restaurant("Test", "Location", 
                                               "test@example.com", "555-1234");
        Restaurant saved = repository.save(restaurant);
        
        boolean deleted = repository.deleteById(saved.getRestaurantId());
        
        assertTrue(deleted);
        
        // Verify in new repository instance
        RestaurantRepository newRepo = new RestaurantRepository(testFilePath.toString());
        assertFalse(newRepo.existsById(saved.getRestaurantId()));
    }
    
    @Test
    @DisplayName("Should return false when deleting non-existent entity")
    void testDeleteNonExistent() throws RepositoryException {
        boolean deleted = repository.deleteById(999L);
        assertFalse(deleted);
    }
    
    @Test
    @DisplayName("Should check existence correctly")
    void testExistsById() throws RepositoryException {
        Restaurant restaurant = new Restaurant("Test", "Location", 
                                               "test@example.com", "555-1234");
        Restaurant saved = repository.save(restaurant);
        
        assertTrue(repository.existsById(saved.getRestaurantId()));
        assertFalse(repository.existsById(999L));
    }
    
    @Test
    @DisplayName("Should count entities correctly")
    void testCount() throws RepositoryException {
        assertEquals(0, repository.count());
        
        repository.save(new Restaurant("R1", "L1", "r1@example.com", "555-0001"));
        assertEquals(1, repository.count());
        
        repository.save(new Restaurant("R2", "L2", "r2@example.com", "555-0002"));
        assertEquals(2, repository.count());
    }
    
    @Test
    @DisplayName("Should find entities by single field")
    void testFindByField() throws RepositoryException {
        repository.save(new Restaurant("Pizza", "New York", "pizza@example.com", "555-0001"));
        repository.save(new Restaurant("Burger", "New York", "burger@example.com", "555-0002"));
        repository.save(new Restaurant("Sushi", "Boston", "sushi@example.com", "555-0003"));
        
        List<Restaurant> found = repository.findByField("location", "New York");
        
        assertEquals(2, found.size());
    }
    
    @Test
    @DisplayName("Should find entities by multiple fields")
    void testFindByFields() throws RepositoryException {
        repository.save(new Restaurant("Pizza", "New York", "pizza@example.com", "555-0001"));
        repository.save(new Restaurant("Burger", "New York", "burger@example.com", "555-0002"));
        
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("location", "New York");
        criteria.put("name", "Pizza");
        
        List<Restaurant> found = repository.findByFields(criteria);
        
        assertEquals(1, found.size());
        assertEquals("Pizza", found.get(0).getName());
    }
    
    @Test
    @DisplayName("Should find one entity by field")
    void testFindOneByField() throws RepositoryException {
        repository.save(new Restaurant("Unique", "Location", 
                                      "unique@example.com", "555-1234"));
        
        Optional<Restaurant> found = repository.findOneByField("contact_email", "unique@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("unique@example.com", found.get().getContactEmail());
    }
    
    @Test
    @DisplayName("Should find entities with predicate")
    void testFindWhere() throws RepositoryException {
        repository.save(new Restaurant("Pizza Place", "New York", "pizza@example.com", "555-0001"));
        repository.save(new Restaurant("Burger Joint", "Boston", "burger@example.com", "555-0002"));
        
        Predicate<Restaurant> predicate = r -> r.getName().contains("Pizza");
        List<Restaurant> found = repository.findWhere(predicate);
        
        assertEquals(1, found.size());
        assertEquals("Pizza Place", found.get(0).getName());
    }
    
    @Test
    @DisplayName("Should save all entities in batch")
    void testSaveAll() throws RepositoryException {
        List<Restaurant> restaurants = Arrays.asList(
            new Restaurant("R1", "L1", "r1@example.com", "555-0001"),
            new Restaurant("R2", "L2", "r2@example.com", "555-0002"),
            new Restaurant("R3", "L3", "r3@example.com", "555-0003")
        );
        
        List<Restaurant> saved = repository.saveAll(restaurants);
        
        assertEquals(3, saved.size());
        for (Restaurant r : saved) {
            assertNotNull(r.getRestaurantId());
        }
    }
    
    @Test
    @DisplayName("Should validate all entities in saveAll")
    void testSaveAllValidation() {
        List<Restaurant> restaurants = Arrays.asList(
            new Restaurant("R1", "L1", "r1@example.com", "555-0001"),
            new Restaurant(null, null, "L2", "r2@example.com", "555-0002") // Invalid
        );
        
        assertThrows(EntityValidationException.class, () -> repository.saveAll(restaurants));
    }
    
    @Test
    @DisplayName("Should delete all by IDs")
    void testDeleteAllById() throws RepositoryException {
        Restaurant r1 = repository.save(new Restaurant("R1", "L1", "r1@example.com", "555-0001"));
        Restaurant r2 = repository.save(new Restaurant("R2", "L2", "r2@example.com", "555-0002"));
        Restaurant r3 = repository.save(new Restaurant("R3", "L3", "r3@example.com", "555-0003"));
        
        List<Long> idsToDelete = Arrays.asList(r1.getRestaurantId(), r2.getRestaurantId());
        int deleted = repository.deleteAllById(idsToDelete);
        
        assertEquals(2, deleted);
        assertEquals(1, repository.count());
        assertTrue(repository.existsById(r3.getRestaurantId()));
    }
    
    @Test
    @DisplayName("Should delete by fields criteria")
    void testDeleteByFields() throws RepositoryException {
        repository.save(new Restaurant("R1", "New York", "r1@example.com", "555-0001"));
        repository.save(new Restaurant("R2", "New York", "r2@example.com", "555-0002"));
        repository.save(new Restaurant("R3", "Boston", "r3@example.com", "555-0003"));
        
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("location", "New York");
        
        int deleted = repository.deleteByFields(criteria);
        
        assertEquals(2, deleted);
        assertEquals(1, repository.count());
    }
    
    @Test
    @DisplayName("Should reload data from file")
    void testReload() throws RepositoryException {
        Restaurant r1 = repository.save(new Restaurant("R1", "L1", "r1@example.com", "555-0001"));
        Long savedId = r1.getRestaurantId();
        
        repository.reload();
        
        Optional<Restaurant> found = repository.findById(savedId);
        assertTrue(found.isPresent());
        assertEquals("R1", found.get().getName());
    }
    
    @Test
    @DisplayName("Should get correct file path")
    void testGetFilePath() {
        assertEquals(testFilePath.toString(), repository.getFilePath());
    }
    
    @Test
    @DisplayName("Should handle concurrent operations")
    void testConcurrentOperations() throws InterruptedException {
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        boolean[] success = new boolean[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    // Mix of operations
                    Restaurant r = new Restaurant("R" + index, "L" + index, 
                                                 "r" + index + "@example.com", "555-000" + index);
                    Restaurant saved = repository.save(r);
                    repository.findById(saved.getRestaurantId());
                    repository.count();
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
        
        // All operations should succeed
        for (boolean s : success) {
            assertTrue(s);
        }
    }
    
    @Test
    @DisplayName("Should maintain ID sequence after reload")
    void testIdSequenceAfterReload() throws RepositoryException {
        Restaurant r1 = repository.save(new Restaurant("R1", "L1", "r1@example.com", "555-0001"));
        Long firstId = r1.getRestaurantId();
        
        repository.reload();
        
        Restaurant r2 = repository.save(new Restaurant("R2", "L2", "r2@example.com", "555-0002"));
        assertTrue(r2.getRestaurantId() > firstId);
    }
    
    @Test
    @DisplayName("Should handle empty criteria in findByFields")
    void testFindByFieldsEmptyCriteria() throws RepositoryException {
        repository.save(new Restaurant("R1", "L1", "r1@example.com", "555-0001"));
        repository.save(new Restaurant("R2", "L2", "r2@example.com", "555-0002"));
        
        Map<String, Object> emptyCriteria = new HashMap<>();
        List<Restaurant> found = repository.findByFields(emptyCriteria);
        
        assertEquals(2, found.size());
    }
    
    @Test
    @DisplayName("Should handle predicate that matches nothing")
    void testFindWhereNoMatch() throws RepositoryException {
        repository.save(new Restaurant("R1", "L1", "r1@example.com", "555-0001"));
        
        List<Restaurant> found = repository.findWhere(r -> r.getName().equals("NonExistent"));
        
        assertTrue(found.isEmpty());
    }
    
    @Test
    @DisplayName("Should handle predicate that matches all")
    void testFindWhereMatchAll() throws RepositoryException {
        repository.save(new Restaurant("R1", "L1", "r1@example.com", "555-0001"));
        repository.save(new Restaurant("R2", "L2", "r2@example.com", "555-0002"));
        
        List<Restaurant> found = repository.findWhere(r -> true);
        
        assertEquals(2, found.size());
    }
}
