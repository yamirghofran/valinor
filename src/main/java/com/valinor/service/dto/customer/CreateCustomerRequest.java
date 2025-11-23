package com.valinor.service.dto.customer;

import java.util.Objects;

/**
 * Data Transfer Object for creating a new customer.
 * Contains all required and optional fields for customer creation.
 */
public class CreateCustomerRequest {
    
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String allergies;
    private String notes;
    private Long restaurantId;
    
    /**
     * Default constructor.
     */
    public CreateCustomerRequest() {
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @param email customer's email address
     * @param phone customer's phone number
     */
    public CreateCustomerRequest(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
    
    /**
     * Full constructor.
     * 
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @param email customer's email address
     * @param phone customer's phone number
     * @param allergies customer's allergies (optional)
     * @param notes additional notes (optional)
     */
    public CreateCustomerRequest(String firstName, String lastName, String email, String phone, 
                                  String allergies, String notes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.allergies = allergies;
        this.notes = notes;
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
    
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateCustomerRequest that = (CreateCustomerRequest) o;
        return Objects.equals(firstName, that.firstName) &&
               Objects.equals(lastName, that.lastName) &&
               Objects.equals(email, that.email) &&
               Objects.equals(phone, that.phone) &&
               Objects.equals(allergies, that.allergies) &&
               Objects.equals(notes, that.notes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, phone, allergies, notes);
    }
    
    @Override
    public String toString() {
        return "CreateCustomerRequest{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", allergies='" + allergies + '\'' +
               ", notes='" + notes + '\'' +
               '}';
    }
}
