package com.valinor.demo;

import com.valinor.service.dto.customer.CreateCustomerRequest;
import com.valinor.service.dto.customer.CustomerResponse;
import com.valinor.service.dto.customer.UpdateCustomerRequest;
import com.valinor.domain.model.Customer;
import com.valinor.exception.CustomerException;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.CustomerRepository;
import com.valinor.service.customer.CustomerService;

import java.util.List;
import java.util.Optional;

/**
 * Demonstration of the CustomerService functionality.
 * Shows how to create, read, update, and delete customers.
 */
public class CustomerServiceDemo {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Customer Service Demo ===\n");
            
            // Initialize repository and service
            String filePath = "data/customers.csv";
            CustomerRepository customerRepository = new CustomerRepository(filePath);
            CustomerService customerService = new CustomerService(customerRepository);
            
            // Demo 1: List all existing customers
            System.out.println("1. Listing all existing customers:");
            System.out.println("-----------------------------------");
            List<Customer> allCustomers = customerService.getAllCustomers();
            for (Customer customer : allCustomers) {
                System.out.println("  - " + customer.getFullName() + " (" + customer.getEmail() + ")");
                if (customer.getAllergies() != null && !customer.getAllergies().isEmpty()) {
                    System.out.println("    Allergies: " + customer.getAllergies());
                }
            }
            System.out.println();
            
            // Demo 2: Create a new customer
            System.out.println("2. Creating a new customer:");
            System.out.println("-----------------------------------");
            CreateCustomerRequest createRequest = new CreateCustomerRequest(
                "Alice",
                "Williams",
                "alice.williams@example.com",
                "555-0103",
                "Shellfish",
                "Vegetarian preferences"
            );
            
            Customer newCustomer = customerService.createCustomer(createRequest);
            System.out.println("  Created: " + newCustomer.getFullName());
            System.out.println("  ID: " + newCustomer.getCustomerId());
            System.out.println("  Email: " + newCustomer.getEmail());
            System.out.println("  Allergies: " + newCustomer.getAllergies());
            System.out.println();
            
            // Demo 3: Get customer by ID
            System.out.println("3. Retrieving customer by ID:");
            System.out.println("-----------------------------------");
            Optional<Customer> foundCustomer = customerService.getCustomerById(newCustomer.getCustomerId());
            if (foundCustomer.isPresent()) {
                System.out.println("  Found: " + foundCustomer.get().getFullName());
            }
            System.out.println();
            
            // Demo 4: Get customer by email
            System.out.println("4. Retrieving customer by email:");
            System.out.println("-----------------------------------");
            Optional<Customer> customerByEmail = customerService.getCustomerByEmail("john.doe@example.com");
            if (customerByEmail.isPresent()) {
                Customer john = customerByEmail.get();
                System.out.println("  Found: " + john.getFullName());
                System.out.println("  Phone: " + john.getPhone());
                if (john.getNotes() != null && !john.getNotes().isEmpty()) {
                    System.out.println("  Notes: " + john.getNotes());
                }
            }
            System.out.println();
            
            // Demo 5: Search customers by name
            System.out.println("5. Searching customers by name (\"Smith\"):");
            System.out.println("-----------------------------------");
            List<Customer> searchResults = customerService.searchCustomers("Smith");
            for (Customer customer : searchResults) {
                System.out.println("  - " + customer.getFullName() + " (" + customer.getEmail() + ")");
            }
            System.out.println();
            
            // Demo 6: Update a customer
            System.out.println("6. Updating a customer:");
            System.out.println("-----------------------------------");
            UpdateCustomerRequest updateRequest = new UpdateCustomerRequest();
            updateRequest.setPhone("555-9999");
            updateRequest.setNotes("Updated: Prefers quiet tables");
            
            Customer updatedCustomer = customerService.updateCustomer(newCustomer.getCustomerId(), updateRequest);
            System.out.println("  Updated: " + updatedCustomer.getFullName());
            System.out.println("  New Phone: " + updatedCustomer.getPhone());
            System.out.println("  New Notes: " + updatedCustomer.getNotes());
            System.out.println();
            
            // Demo 7: Get customers with allergies
            System.out.println("7. Listing customers with allergies:");
            System.out.println("-----------------------------------");
            List<Customer> customersWithAllergies = customerService.getCustomersWithAllergies();
            for (Customer customer : customersWithAllergies) {
                System.out.println("  - " + customer.getFullName() + ": " + customer.getAllergies());
            }
            System.out.println();
            
            // Demo 8: Convert to response DTOs
            System.out.println("8. Converting to response DTOs:");
            System.out.println("-----------------------------------");
            List<CustomerResponse> responses = customerService.getAllCustomersAsResponses();
            System.out.println("  Total customers: " + responses.size());
            for (CustomerResponse response : responses) {
                System.out.println("  - " + response.getFullName() + " (ID: " + response.getCustomerId() + ")");
            }
            System.out.println();
            
            // Demo 9: Delete a customer
            System.out.println("9. Deleting a customer:");
            System.out.println("-----------------------------------");
            customerService.deleteCustomer(newCustomer.getCustomerId());
            System.out.println("  Deleted customer ID: " + newCustomer.getCustomerId());
            
            // Verify deletion
            Optional<Customer> deletedCustomer = customerService.getCustomerById(newCustomer.getCustomerId());
            System.out.println("  Customer still exists: " + deletedCustomer.isPresent());
            System.out.println();
            
            // Demo 10: Error handling - duplicate email
            System.out.println("10. Error handling - duplicate email:");
            System.out.println("-----------------------------------");
            try {
                CreateCustomerRequest duplicateRequest = new CreateCustomerRequest(
                    "Test",
                    "User",
                    "john.doe@example.com", // Duplicate email
                    "555-0000"
                );
                customerService.createCustomer(duplicateRequest);
            } catch (CustomerException e) {
                System.out.println("  Expected error: " + e.getMessage());
            }
            System.out.println();
            
            System.out.println("=== Demo Complete ===");
            
        } catch (RepositoryException e) {
            System.err.println("Repository error: " + e.getMessage());
            e.printStackTrace();
        } catch (CustomerException e) {
            System.err.println("Customer service error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
