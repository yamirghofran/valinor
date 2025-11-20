package com.valinor.data.dto;

import java.util.Objects;

/**
 * Data Transfer Object for updating an existing customer.
 * All fields are optional - only provided fields will be updated.
 */
public class UpdateCustomerRequest {
    
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String allergies;
    private String notes;
    
    /**
     * Default constructor.
     */
    public UpdateCustomerRequest() {
    }
    
    /**
     * Checks if this request has any updates.
     * 
     * @return true if at least one field is set
     */
    public boolean hasUpdates() {
        return firstName != null || lastName != null || email != null ||
               phone != null || allergies != null || notes != null;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateCustomerRequest that = (UpdateCustomerRequest) o;
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
        return "UpdateCustomerRequest{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", allergies='" + allergies + '\'' +
               ", notes='" + notes + '\'' +
               '}';
    }
}
