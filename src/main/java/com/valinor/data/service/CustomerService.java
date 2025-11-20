package com.valinor.data.service;

import com.valinor.data.dto.CreateCustomerRequest;
import com.valinor.data.dto.CustomerResponse;
import com.valinor.data.dto.UpdateCustomerRequest;
import com.valinor.data.entity.Customer;
import com.valinor.data.exception.CustomerException;
import com.valinor.data.exception.RepositoryException;
import com.valinor.data.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for managing customer profiles.
 * Provides business logic for customer CRUD operations and validation.
 */
public class CustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    
    private final CustomerRepository customerRepository;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    /**
     * Constructs a new CustomerService.
     * 
     * @param customerRepository the customer repository
     */
    public CustomerService(CustomerRepository customerRepository) {
        if (customerRepository == null) {
            throw new IllegalArgumentException("CustomerRepository cannot be null");
        }
        this.customerRepository = customerRepository;
    }
    
    /**
     * Creates a new customer.
     * 
     * @param request the customer creation request
     * @return the created customer
     * @throws CustomerException if creation fails or validation fails
     */
    public Customer createCustomer(CreateCustomerRequest request) throws CustomerException {
        if (request == null) {
            throw new IllegalArgumentException("Customer create request cannot be null");
        }
        
        try {
            // Validate request
            validateCreateRequest(request);
            
            // Check for duplicate email
            if (customerRepository.existsByEmail(request.getEmail())) {
                throw new CustomerException("Email already exists: " + request.getEmail());
            }
            
            // Create customer entity
            Customer customer = new Customer(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone()
            );
            
            customer.setAllergies(request.getAllergies());
            customer.setNotes(request.getNotes());
            
            // Save customer
            customer = customerRepository.save(customer);
            
            logger.info("Created new customer: {} {} (ID: {})", 
                       customer.getFirstName(), customer.getLastName(), customer.getCustomerId());
            return customer;
            
        } catch (CustomerException e) {
            throw e;
        } catch (RepositoryException e) {
            logger.error("Repository error during customer creation", e);
            throw new CustomerException("Failed to create customer", e);
        }
    }
    
    /**
     * Updates an existing customer.
     * 
     * @param customerId the customer ID to update
     * @param request the update request
     * @return the updated customer
     * @throws CustomerException if update fails
     */
    public Customer updateCustomer(Long customerId, UpdateCustomerRequest request) throws CustomerException {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        
        if (request == null) {
            throw new IllegalArgumentException("Customer update request cannot be null");
        }
        
        if (!request.hasUpdates()) {
            throw new CustomerException("No fields to update");
        }
        
        try {
            // Find existing customer
            Optional<Customer> customerOpt = customerRepository.findById(customerId);
            if (!customerOpt.isPresent()) {
                throw new CustomerException("Customer not found with ID: " + customerId);
            }
            
            Customer customer = customerOpt.get();
            
            // Validate and apply updates
            if (request.getEmail() != null) {
                validateEmail(request.getEmail());
                
                // Check for email duplicates (excluding current customer)
                Optional<Customer> existingCustomer = customerRepository.findByEmail(request.getEmail());
                if (existingCustomer.isPresent() && !existingCustomer.get().getCustomerId().equals(customerId)) {
                    throw new CustomerException("Email already exists: " + request.getEmail());
                }
                
                customer.setEmail(request.getEmail());
            }
            
            if (request.getFirstName() != null) {
                validateNotEmpty(request.getFirstName(), "First name");
                customer.setFirstName(request.getFirstName());
            }
            
            if (request.getLastName() != null) {
                validateNotEmpty(request.getLastName(), "Last name");
                customer.setLastName(request.getLastName());
            }
            
            if (request.getPhone() != null) {
                validateNotEmpty(request.getPhone(), "Phone");
                customer.setPhone(request.getPhone());
            }
            
            if (request.getAllergies() != null) {
                customer.setAllergies(request.getAllergies());
            }
            
            if (request.getNotes() != null) {
                customer.setNotes(request.getNotes());
            }
            
            // Update customer
            customer = customerRepository.update(customer);
            
            logger.info("Updated customer: {} {} (ID: {})", 
                       customer.getFirstName(), customer.getLastName(), customer.getCustomerId());
            return customer;
            
        } catch (CustomerException e) {
            throw e;
        } catch (RepositoryException e) {
            logger.error("Repository error during customer update", e);
            throw new CustomerException("Failed to update customer", e);
        }
    }
    
    /**
     * Deletes a customer.
     * 
     * @param customerId the customer ID to delete
     * @throws CustomerException if deletion fails
     */
    public void deleteCustomer(Long customerId) throws CustomerException {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        
        try {
            boolean deleted = customerRepository.deleteById(customerId);
            if (!deleted) {
                throw new CustomerException("Customer not found with ID: " + customerId);
            }
            
            logger.info("Deleted customer with ID: {}", customerId);
            
        } catch (RepositoryException e) {
            logger.error("Repository error during customer deletion", e);
            throw new CustomerException("Failed to delete customer", e);
        }
    }
    
    /**
     * Gets a customer by ID.
     * 
     * @param customerId the customer ID
     * @return optional containing the customer if found
     * @throws CustomerException if retrieval fails
     */
    public Optional<Customer> getCustomerById(Long customerId) throws CustomerException {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        
        try {
            return customerRepository.findById(customerId);
        } catch (RepositoryException e) {
            logger.error("Repository error during customer retrieval", e);
            throw new CustomerException("Failed to retrieve customer", e);
        }
    }
    
    /**
     * Gets a customer by email address.
     * 
     * @param email the email address
     * @return optional containing the customer if found
     * @throws CustomerException if retrieval fails
     */
    public Optional<Customer> getCustomerByEmail(String email) throws CustomerException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        try {
            return customerRepository.findByEmail(email);
        } catch (RepositoryException e) {
            logger.error("Repository error during customer retrieval", e);
            throw new CustomerException("Failed to retrieve customer", e);
        }
    }
    
    /**
     * Searches for customers by name.
     * Performs a case-insensitive partial match on first or last name.
     * 
     * @param searchTerm the search term
     * @return list of matching customers
     * @throws CustomerException if search fails
     */
    public List<Customer> searchCustomers(String searchTerm) throws CustomerException {
        try {
            return customerRepository.searchByName(searchTerm);
        } catch (RepositoryException e) {
            logger.error("Repository error during customer search", e);
            throw new CustomerException("Failed to search customers", e);
        }
    }
    
    /**
     * Gets all customers.
     * 
     * @return list of all customers
     * @throws CustomerException if retrieval fails
     */
    public List<Customer> getAllCustomers() throws CustomerException {
        try {
            return customerRepository.findAll();
        } catch (RepositoryException e) {
            logger.error("Repository error during customers retrieval", e);
            throw new CustomerException("Failed to retrieve customers", e);
        }
    }
    
    /**
     * Gets all customers as response DTOs.
     * 
     * @return list of customer responses
     * @throws CustomerException if retrieval fails
     */
    public List<CustomerResponse> getAllCustomersAsResponses() throws CustomerException {
        List<Customer> customers = getAllCustomers();
        return customers.stream()
                       .map(CustomerResponse::fromCustomer)
                       .collect(Collectors.toList());
    }
    
    /**
     * Gets customers with allergies.
     * 
     * @return list of customers who have allergies recorded
     * @throws CustomerException if retrieval fails
     */
    public List<Customer> getCustomersWithAllergies() throws CustomerException {
        try {
            return customerRepository.findCustomersWithAllergies();
        } catch (RepositoryException e) {
            logger.error("Repository error during customers retrieval", e);
            throw new CustomerException("Failed to retrieve customers with allergies", e);
        }
    }
    
    /**
     * Finds customers by last name.
     * 
     * @param lastName the last name to search for
     * @return list of customers with matching last name
     * @throws CustomerException if search fails
     */
    public List<Customer> getCustomersByLastName(String lastName) throws CustomerException {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        
        try {
            return customerRepository.findByLastName(lastName);
        } catch (RepositoryException e) {
            logger.error("Repository error during customer search", e);
            throw new CustomerException("Failed to search customers by last name", e);
        }
    }
    
    /**
     * Converts a customer to a response DTO.
     * 
     * @param customer the customer entity
     * @return the customer response DTO
     */
    public CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.fromCustomer(customer);
    }
    
    /**
     * Converts a list of customers to response DTOs.
     * 
     * @param customers the list of customer entities
     * @return the list of customer response DTOs
     */
    public List<CustomerResponse> toResponses(List<Customer> customers) {
        return customers.stream()
                       .map(CustomerResponse::fromCustomer)
                       .collect(Collectors.toList());
    }
    
    /**
     * Validates a customer creation request.
     * 
     * @param request the request to validate
     * @throws CustomerException if validation fails
     */
    private void validateCreateRequest(CreateCustomerRequest request) throws CustomerException {
        // Validate first name
        validateNotEmpty(request.getFirstName(), "First name");
        
        // Validate last name
        validateNotEmpty(request.getLastName(), "Last name");
        
        // Validate email
        validateEmail(request.getEmail());
        
        // Validate phone
        validateNotEmpty(request.getPhone(), "Phone");
    }
    
    /**
     * Validates an email address.
     * 
     * @param email the email to validate
     * @throws CustomerException if validation fails
     */
    private void validateEmail(String email) throws CustomerException {
        if (email == null || email.trim().isEmpty()) {
            throw new CustomerException("Email is required");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new CustomerException("Invalid email format: " + email);
        }
    }
    
    /**
     * Validates that a string is not empty.
     * 
     * @param value the value to validate
     * @param fieldName the field name for error messages
     * @throws CustomerException if validation fails
     */
    private void validateNotEmpty(String value, String fieldName) throws CustomerException {
        if (value == null || value.trim().isEmpty()) {
            throw new CustomerException(fieldName + " is required");
        }
    }
}
