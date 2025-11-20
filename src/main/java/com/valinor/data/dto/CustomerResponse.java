package com.valinor.data.dto;

import com.valinor.data.entity.Customer;

import java.util.Objects;

/**
 * Data Transfer Object for customer responses.
 * Contains customer information to be returned to clients.
 */
public class CustomerResponse {
    
    private Long customerId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String allergies;
    private String notes;
    
    /**
     * Default constructor.
     */
    public CustomerResponse() {
    }
    
    /**
     * Full constructor.
     * 
     * @param customerId customer ID
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @param fullName customer's full name
     * @param email customer's email
     * @param phone customer's phone
     * @param allergies customer's allergies
     * @param notes additional notes
     */
    public CustomerResponse(Long customerId, String firstName, String lastName, String fullName,
                           String email, String phone, String allergies, String notes) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.allergies = allergies;
        this.notes = notes;
    }
    
    /**
     * Creates a CustomerResponse from a Customer entity.
     * 
     * @param customer the customer entity
     * @return the customer response DTO
     */
    public static CustomerResponse fromCustomer(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        return new CustomerResponse(
            customer.getCustomerId(),
            customer.getFirstName(),
            customer.getLastName(),
            customer.getFullName(),
            customer.getEmail(),
            customer.getPhone(),
            customer.getAllergies(),
            customer.getNotes()
        );
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAllergies() {
        return allergies;
    }
    
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerResponse that = (CustomerResponse) o;
        return Objects.equals(customerId, that.customerId) &&
               Objects.equals(firstName, that.firstName) &&
               Objects.equals(lastName, that.lastName) &&
               Objects.equals(fullName, that.fullName) &&
               Objects.equals(email, that.email) &&
               Objects.equals(phone, that.phone) &&
               Objects.equals(allergies, that.allergies) &&
               Objects.equals(notes, that.notes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customerId, firstName, lastName, fullName, email, phone, allergies, notes);
    }
    
    @Override
    public String toString() {
        return "CustomerResponse{" +
               "customerId=" + customerId +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", fullName='" + fullName + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", allergies='" + allergies + '\'' +
               ", notes='" + notes + '\'' +
               '}';
    }
}
