package com.valinor.repository;

import com.valinor.domain.model.Customer;
import com.valinor.exception.EntityValidationException;
import com.valinor.exception.RepositoryException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CustomerRepository.
 * Tests all CRUD operations and query methods.
 */
class CustomerRepositoryTest {
    
    @TempDir
    Path tempDir;
    
    private CustomerRepository repository;
    private Path testFilePath;
    
    @BeforeEach
    void setUp() throws RepositoryException {
        testFilePath = tempDir.resolve("test_customers.csv");
        repository = new CustomerRepository(testFilePath.toString());
    }
    
    @Test
    @DisplayName("Should create repository successfully")
    void testRepositoryCreation() {
        assertNotNull(repository);
        assertEquals(testFilePath.toString(), repository.getFilePath());
    }
    
    @Test
    @DisplayName("Should save new customer without ID")
    void testSaveNewCustomer() throws RepositoryException {
        Customer customer = new Customer("John", "Doe", "john@example.com", "555-1234");
        customer.setRestaurantId(1L);
        
        Customer saved = repository.save(customer);
        
        assertNotNull(saved);
        assertNotNull(saved.getCustomerId());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
    }
    
    @Test
    @DisplayName("Should save customer with ID")
    void testSaveCustomerWithId() throws RepositoryException {
        Customer customer = new Customer(100L, "Jane", "Smith", "jane@example.com", 
                                        "555-5678", null, null, 1L);
        
        Customer saved = repository.save(customer);
        
        assertNotNull(saved);
        assertEquals(100L, saved.getCustomerId());
    }
    
    @Test
    @DisplayName("Should throw exception when saving invalid customer")
    void testSaveInvalidCustomer() {
        Customer customer = new Customer(null, null, "john@example.com", "555-1234");
        
        assertThrows(EntityValidationException.class, () -> repository.save(customer));
    }
    
    @Test
    @DisplayName("Should find customer by ID")
    void testFindById() throws RepositoryException {
        Customer customer = new Customer("John", "Doe", "john@example.com", "555-1234");
        customer.setRestaurantId(1L);
        Customer saved = repository.save(customer);
        
        Optional<Customer> found = repository.findById(saved.getCustomerId());
        
        assertTrue(found.isPresent());
        assertEquals(saved.getCustomerId(), found.get().getCustomerId());
        assertEquals("John", found.get().getFirstName());
    }
    
    @Test
    @DisplayName("Should return empty optional when customer not found")
    void testFindByIdNotFound() throws RepositoryException {
        Optional<Customer> found = repository.findById(999L);
        
        assertFalse(found.isPresent());
    }
    
    @Test
    @DisplayName("Should find all customers")
    void testFindAll() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-0001");
        c1.setRestaurantId(1L);
        Customer c2 = new Customer("Jane", "Smith", "jane@example.com", "555-0002");
        c2.setRestaurantId(1L);
        
        repository.save(c1);
        repository.save(c2);
        
        List<Customer> all = repository.findAll();
        
        assertEquals(2, all.size());
    }
    
    @Test
    @DisplayName("Should update existing customer")
    void testUpdate() throws RepositoryException {
        Customer customer = new Customer("John", "Doe", "john@example.com", "555-1234");
        customer.setRestaurantId(1L);
        Customer saved = repository.save(customer);
        
        saved.setFirstName("Johnny");
        saved.setEmail("johnny@example.com");
        Customer updated = repository.update(saved);
        
        assertEquals("Johnny", updated.getFirstName());
        assertEquals("johnny@example.com", updated.getEmail());
        
        // Verify persistence
        Optional<Customer> found = repository.findById(saved.getCustomerId());
        assertTrue(found.isPresent());
        assertEquals("Johnny", found.get().getFirstName());
    }
    
    @Test
    @DisplayName("Should throw exception when updating non-existent customer")
    void testUpdateNonExistent() {
        Customer customer = new Customer(999L, "John", "Doe", "john@example.com", 
                                        "555-1234", null, null, 1L);
        
        assertThrows(RepositoryException.class, () -> repository.update(customer));
    }
    
    @Test
    @DisplayName("Should delete customer by ID")
    void testDeleteById() throws RepositoryException {
        Customer customer = new Customer("John", "Doe", "john@example.com", "555-1234");
        customer.setRestaurantId(1L);
        Customer saved = repository.save(customer);
        
        boolean deleted = repository.deleteById(saved.getCustomerId());
        
        assertTrue(deleted);
        assertFalse(repository.findById(saved.getCustomerId()).isPresent());
    }
    
    @Test
    @DisplayName("Should return false when deleting non-existent customer")
    void testDeleteByIdNotFound() throws RepositoryException {
        boolean deleted = repository.deleteById(999L);
        
        assertFalse(deleted);
    }
    
    @Test
    @DisplayName("Should check if customer exists by ID")
    void testExistsById() throws RepositoryException {
        Customer customer = new Customer("John", "Doe", "john@example.com", "555-1234");
        customer.setRestaurantId(1L);
        Customer saved = repository.save(customer);
        
        assertTrue(repository.existsById(saved.getCustomerId()));
        assertFalse(repository.existsById(999L));
    }
    
    @Test
    @DisplayName("Should count customers")
    void testCount() throws RepositoryException {
        assertEquals(0, repository.count());
        
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-0001");
        c1.setRestaurantId(1L);
        repository.save(c1);
        assertEquals(1, repository.count());
        
        Customer c2 = new Customer("Jane", "Smith", "jane@example.com", "555-0002");
        c2.setRestaurantId(1L);
        repository.save(c2);
        assertEquals(2, repository.count());
    }
    
    @Test
    @DisplayName("Should find customer by email")
    void testFindByEmail() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-0001");
        c1.setRestaurantId(1L);
        repository.save(c1);
        
        Optional<Customer> found = repository.findByEmail("john@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("john@example.com", found.get().getEmail());
    }
    
    @Test
    @DisplayName("Should return empty when finding by non-existent email")
    void testFindByEmailNotFound() throws RepositoryException {
        Optional<Customer> found = repository.findByEmail("nonexistent@example.com");
        
        assertFalse(found.isPresent());
    }
    
    @Test
    @DisplayName("Should check if email exists")
    void testExistsByEmail() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-0001");
        c1.setRestaurantId(1L);
        repository.save(c1);
        
        assertTrue(repository.existsByEmail("john@example.com"));
        assertFalse(repository.existsByEmail("nonexistent@example.com"));
    }
    
    @Test
    @DisplayName("Should find customers by last name")
    void testFindByLastName() throws RepositoryException {
        Customer c1 = new Customer("John", "Smith", "john@example.com", "555-0001");
        c1.setRestaurantId(1L);
        Customer c2 = new Customer("Jane", "Smith", "jane@example.com", "555-0002");
        c2.setRestaurantId(1L);
        Customer c3 = new Customer("Bob", "Jones", "bob@example.com", "555-0003");
        c3.setRestaurantId(1L);
        
        repository.save(c1);
        repository.save(c2);
        repository.save(c3);
        
        List<Customer> found = repository.findByLastName("Smith");
        
        assertEquals(2, found.size());
    }
    
    @Test
    @DisplayName("Should find customers by phone")
    void testFindByPhone() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-1234");
        c1.setRestaurantId(1L);
        repository.save(c1);
        
        List<Customer> found = repository.findByPhone("555-1234");
        
        assertEquals(1, found.size());
        assertEquals("555-1234", found.get(0).getPhone());
    }
    
    @Test
    @DisplayName("Should find customers by restaurant ID")
    void testFindByRestaurantId() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-0001");
        c1.setRestaurantId(1L);
        Customer c2 = new Customer("Jane", "Smith", "jane@example.com", "555-0002");
        c2.setRestaurantId(1L);
        Customer c3 = new Customer("Bob", "Jones", "bob@example.com", "555-0003");
        c3.setRestaurantId(2L);
        
        repository.save(c1);
        repository.save(c2);
        repository.save(c3);
        
        List<Customer> found = repository.findByRestaurantId(1L);
        
        assertEquals(2, found.size());
    }
    
    @Test
    @DisplayName("Should search customers by name")
    void testSearchByName() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-0001");
        c1.setRestaurantId(1L);
        Customer c2 = new Customer("Jane", "Smith", "jane@example.com", "555-0002");
        c2.setRestaurantId(1L);
        Customer c3 = new Customer("Johnny", "Jones", "johnny@example.com", "555-0003");
        c3.setRestaurantId(1L);
        
        repository.save(c1);
        repository.save(c2);
        repository.save(c3);
        
        List<Customer> found = repository.searchByName("john");
        
        assertEquals(2, found.size());
    }
    
    @Test
    @DisplayName("Should return all customers when search term is empty")
    void testSearchByNameEmpty() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-0001");
        c1.setRestaurantId(1L);
        repository.save(c1);
        
        List<Customer> found = repository.searchByName("");
        
        assertEquals(1, found.size());
    }
    
    @Test
    @DisplayName("Should find customers by allergy")
    void testFindByAllergy() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-0001");
        c1.setAllergies("Peanuts, Shellfish");
        c1.setRestaurantId(1L);
        Customer c2 = new Customer("Jane", "Smith", "jane@example.com", "555-0002");
        c2.setAllergies("Dairy");
        c2.setRestaurantId(1L);
        Customer c3 = new Customer("Bob", "Jones", "bob@example.com", "555-0003");
        c3.setRestaurantId(1L);
        
        repository.save(c1);
        repository.save(c2);
        repository.save(c3);
        
        List<Customer> found = repository.findByAllergy("peanuts");
        
        assertEquals(1, found.size());
        assertEquals("John", found.get(0).getFirstName());
    }
    
    @Test
    @DisplayName("Should return empty list when searching for allergy with empty string")
    void testFindByAllergyEmpty() throws RepositoryException {
        List<Customer> found = repository.findByAllergy("");
        
        assertTrue(found.isEmpty());
    }
    
    @Test
    @DisplayName("Should find customers with allergies")
    void testFindCustomersWithAllergies() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-0001");
        c1.setAllergies("Peanuts");
        c1.setRestaurantId(1L);
        Customer c2 = new Customer("Jane", "Smith", "jane@example.com", "555-0002");
        c2.setRestaurantId(1L);
        
        repository.save(c1);
        repository.save(c2);
        
        List<Customer> found = repository.findCustomersWithAllergies();
        
        assertEquals(1, found.size());
        assertEquals("John", found.get(0).getFirstName());
    }
    
    @Test
    @DisplayName("Should find customers with notes")
    void testFindCustomersWithNotes() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-0001");
        c1.setNotes("VIP customer");
        c1.setRestaurantId(1L);
        Customer c2 = new Customer("Jane", "Smith", "jane@example.com", "555-0002");
        c2.setRestaurantId(1L);
        
        repository.save(c1);
        repository.save(c2);
        
        List<Customer> found = repository.findCustomersWithNotes();
        
        assertEquals(1, found.size());
        assertEquals("John", found.get(0).getFirstName());
    }
    
    @Test
    @DisplayName("Should save all customers")
    void testSaveAll() throws RepositoryException {
        List<Customer> customers = Arrays.asList(
            new Customer(null, "John", "Doe", "john@example.com", "555-0001", null, null, 1L),
            new Customer(null, "Jane", "Smith", "jane@example.com", "555-0002", null, null, 1L),
            new Customer(null, "Bob", "Jones", "bob@example.com", "555-0003", null, null, 1L)
        );
        
        List<Customer> saved = repository.saveAll(customers);
        
        assertEquals(3, saved.size());
        assertEquals(3, repository.count());
        
        // Verify all have IDs
        for (Customer c : saved) {
            assertNotNull(c.getCustomerId());
        }
    }
    
    @Test
    @DisplayName("Should delete all by IDs")
    void testDeleteAllById() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-0001");
        c1.setRestaurantId(1L);
        Customer c2 = new Customer("Jane", "Smith", "jane@example.com", "555-0002");
        c2.setRestaurantId(1L);
        Customer c3 = new Customer("Bob", "Jones", "bob@example.com", "555-0003");
        c3.setRestaurantId(1L);
        
        c1 = repository.save(c1);
        c2 = repository.save(c2);
        c3 = repository.save(c3);
        
        List<Long> idsToDelete = Arrays.asList(c1.getCustomerId(), c2.getCustomerId());
        int deletedCount = repository.deleteAllById(idsToDelete);
        
        assertEquals(2, deletedCount);
        assertEquals(1, repository.count());
        assertTrue(repository.existsById(c3.getCustomerId()));
    }
    
    @Test
    @DisplayName("Should persist data across repository instances")
    void testPersistenceAcrossInstances() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-1234");
        c1.setRestaurantId(1L);
        c1 = repository.save(c1);
        Long savedId = c1.getCustomerId();
        
        // Create new repository instance pointing to same file
        CustomerRepository newRepository = new CustomerRepository(testFilePath.toString());
        
        Optional<Customer> found = newRepository.findById(savedId);
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
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
                    Customer c = new Customer("Customer" + index, "Last" + index, 
                                            "c" + index + "@example.com", "555-000" + index);
                    c.setRestaurantId(1L);
                    repository.save(c);
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
    @DisplayName("Should test field value matching for customer_id")
    void testFieldValueMatchingCustomerId() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-1234");
        c1.setRestaurantId(1L);
        c1 = repository.save(c1);
        
        List<Customer> found = repository.findByField("customer_id", c1.getCustomerId());
        assertEquals(1, found.size());
    }
    
    @Test
    @DisplayName("Should test field value matching for email")
    void testFieldValueMatchingEmail() throws RepositoryException {
        Customer c1 = new Customer("John", "Doe", "john@example.com", "555-1234");
        c1.setRestaurantId(1L);
        repository.save(c1);
        
        List<Customer> found = repository.findByField("email", "john@example.com");
        assertEquals(1, found.size());
    }
}
