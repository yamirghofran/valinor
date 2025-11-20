package com.valinor.data.repository;

import com.valinor.data.entity.Customer;
import com.valinor.data.exception.RepositoryException;

import java.util.List;
import java.util.Optional;

/**
 * CSV-based repository for Customer entities.
 * Provides CRUD operations for customer data stored in CSV files.
 */
public class CustomerRepository extends AbstractCsvRepository<Customer, Long> {
    
    /**
     * Constructs a new CustomerRepository.
     * 
     * @param filePath the path to the customers CSV file
     * @throws RepositoryException if initialization fails
     */
    public CustomerRepository(String filePath) throws RepositoryException {
        super(filePath, new CustomerEntityMapper());
    }
    
    /**
     * Finds a customer by email address.
     * 
     * @param email the email address to search for
     * @return optional containing the customer if found
     * @throws RepositoryException if search fails
     */
    public Optional<Customer> findByEmail(String email) throws RepositoryException {
        return findOneByField("email", email);
    }
    
    /**
     * Checks if a customer exists with the given email.
     * 
     * @param email the email address to check
     * @return true if a customer with this email exists
     * @throws RepositoryException if check fails
     */
    public boolean existsByEmail(String email) throws RepositoryException {
        return findByEmail(email).isPresent();
    }
    
    /**
     * Finds all customers with a specific last name.
     * 
     * @param lastName the last name to search for
     * @return list of customers with the specified last name
     * @throws RepositoryException if search fails
     */
    public List<Customer> findByLastName(String lastName) throws RepositoryException {
        return findByField("last_name", lastName);
    }
    
    /**
     * Finds customers by phone number.
     * 
     * @param phone the phone number to search for
     * @return list of customers with the specified phone number
     * @throws RepositoryException if search fails
     */
    public List<Customer> findByPhone(String phone) throws RepositoryException {
        return findByField("phone", phone);
    }
    
    /**
     * Searches for customers by name (first or last name).
     * Performs a case-insensitive partial match.
     * 
     * @param searchTerm the search term
     * @return list of customers matching the search term
     * @throws RepositoryException if search fails
     */
    public List<Customer> searchByName(String searchTerm) throws RepositoryException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        
        return findWhere(customer -> {
            String firstName = customer.getFirstName() != null ? customer.getFirstName().toLowerCase() : "";
            String lastName = customer.getLastName() != null ? customer.getLastName().toLowerCase() : "";
            String fullName = customer.getFullName() != null ? customer.getFullName().toLowerCase() : "";
            
            return firstName.contains(lowerSearchTerm) || 
                   lastName.contains(lowerSearchTerm) ||
                   fullName.contains(lowerSearchTerm);
        });
    }
    
    /**
     * Finds customers with specific allergies.
     * 
     * @param allergy the allergy to search for
     * @return list of customers with the specified allergy
     * @throws RepositoryException if search fails
     */
    public List<Customer> findByAllergy(String allergy) throws RepositoryException {
        if (allergy == null || allergy.trim().isEmpty()) {
            return List.of();
        }
        
        String lowerAllergy = allergy.toLowerCase().trim();
        
        return findWhere(customer -> {
            String allergies = customer.getAllergies();
            return allergies != null && allergies.toLowerCase().contains(lowerAllergy);
        });
    }
    
    /**
     * Finds all customers with allergies recorded.
     * 
     * @return list of customers with allergies
     * @throws RepositoryException if search fails
     */
    public List<Customer> findCustomersWithAllergies() throws RepositoryException {
        return findWhere(customer -> 
            customer.getAllergies() != null && !customer.getAllergies().trim().isEmpty()
        );
    }
    
    /**
     * Finds all customers with notes recorded.
     * 
     * @return list of customers with notes
     * @throws RepositoryException if search fails
     */
    public List<Customer> findCustomersWithNotes() throws RepositoryException {
        return findWhere(customer -> 
            customer.getNotes() != null && !customer.getNotes().trim().isEmpty()
        );
    }
    
    @Override
    protected boolean fieldValueMatches(Customer entity, String fieldName, Object expectedValue) {
        if (entity == null || expectedValue == null) {
            return false;
        }
        
        try {
            switch (fieldName.toLowerCase()) {
                case "customer_id":
                    return expectedValue.equals(entity.getCustomerId());
                case "first_name":
                    return expectedValue.equals(entity.getFirstName());
                case "last_name":
                    return expectedValue.equals(entity.getLastName());
                case "email":
                    return expectedValue.equals(entity.getEmail());
                case "phone":
                    return expectedValue.equals(entity.getPhone());
                case "allergies":
                    return expectedValue.equals(entity.getAllergies());
                case "notes":
                    return expectedValue.equals(entity.getNotes());
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
