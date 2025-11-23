package com.valinor.domain.model;

import java.util.Objects;

/**
 * Represents customer profile information.
 */
public class Customer {
    
    private Long customerId;
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
    public Customer() {
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @param email customer's email
     * @param phone customer's phone number
     */
    public Customer(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
    
    /**
     * Full constructor.
     * 
     * @param customerId customer ID
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @param email customer's email
     * @param phone customer's phone number
     * @param allergies customer's allergies (optional)
     * @param notes additional notes about customer (optional)
     * @param restaurantId the restaurant ID this customer belongs to
     */
    public Customer(Long customerId, String firstName, String lastName, String email, String phone, String allergies, String notes, Long restaurantId) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.allergies = allergies;
        this.notes = notes;
        this.restaurantId = restaurantId;
    }
    
    /**
     * Gets customer ID.
     * 
     * @return the customer ID
     */
    public Long getCustomerId() {
        return customerId;
    }
    
    /**
     * Sets customer ID.
     * 
     * @param customerId the customer ID to set
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    /**
     * Gets customer's first name.
     * 
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Sets customer's first name.
     * 
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Gets customer's last name.
     * 
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Sets customer's last name.
     * 
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Gets customer's full name (first + last name).
     * 
     * @return the full name
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return "";
        }
    }
    
    /**
     * Gets customer's email.
     * 
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets customer's email.
     * 
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets customer's phone number.
     * 
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Sets customer's phone number.
     * 
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * Gets customer's allergies.
     * 
     * @return the allergies, or null if not specified
     */
    public String getAllergies() {
        return allergies;
    }
    
    /**
     * Sets customer's allergies.
     * 
     * @param allergies the allergies to set, or null if not specified
     */
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    
    /**
     * Gets additional notes about customer.
     * 
     * @return the notes, or null if not specified
     */
    public String getNotes() {
        return notes;
    }
    
    /**
     * Sets additional notes about customer.
     * 
     * @param notes the notes to set, or null if not specified
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    /**
     * Gets the restaurant ID this customer belongs to.
     * 
     * @return the restaurant ID
     */
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    /**
     * Sets the restaurant ID this customer belongs to.
     * 
     * @param restaurantId the restaurant ID to set
     */
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId) &&
               Objects.equals(firstName, customer.firstName) &&
               Objects.equals(lastName, customer.lastName) &&
               Objects.equals(email, customer.email) &&
               Objects.equals(phone, customer.phone) &&
               Objects.equals(allergies, customer.allergies) &&
               Objects.equals(notes, customer.notes) &&
               Objects.equals(restaurantId, customer.restaurantId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customerId, firstName, lastName, email, phone, allergies, notes, restaurantId);
    }
    
    @Override
    public String toString() {
        return "Customer{" +
               "customerId=" + customerId +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", allergies='" + allergies + '\'' +
               ", notes='" + notes + '\'' +
               ", restaurantId=" + restaurantId +
               '}';
    }
}