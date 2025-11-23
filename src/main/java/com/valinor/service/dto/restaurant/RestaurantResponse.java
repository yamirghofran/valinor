package com.valinor.service.dto.restaurant;

import com.valinor.domain.model.Restaurant;

/**
 * Data Transfer Object for restaurant responses.
 */
public class RestaurantResponse {
    
    private Long restaurantId;
    private String name;
    private String location;
    private String contactEmail;
    private String contactPhone;
    
    /**
     * Default constructor.
     */
    public RestaurantResponse() {
    }
    
    /**
     * Creates a RestaurantResponse from a Restaurant entity.
     * 
     * @param restaurant the restaurant entity
     * @return the restaurant response DTO
     */
    public static RestaurantResponse fromRestaurant(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }
        
        RestaurantResponse response = new RestaurantResponse();
        response.setRestaurantId(restaurant.getRestaurantId());
        response.setName(restaurant.getName());
        response.setLocation(restaurant.getLocation());
        response.setContactEmail(restaurant.getContactEmail());
        response.setContactPhone(restaurant.getContactPhone());
        return response;
    }
    
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
