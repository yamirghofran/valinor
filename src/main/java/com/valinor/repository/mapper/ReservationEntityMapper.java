package com.valinor.repository.mapper;

import com.valinor.domain.model.Reservation;
import com.valinor.domain.enums.ReservationStatus;
import com.valinor.exception.EntityValidationException;
import com.valinor.exception.RepositoryException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity mapper for Reservation entities.
 * Handles conversion between Reservation objects and CSV records.
 */
public class ReservationEntityMapper implements EntityMapper<Reservation> {
    
    private static final String[] COLUMN_NAMES = {
        "reservation_id", "customer_id", "restaurant_id", "table_id", "party_size",
        "reservation_datetime", "status", "special_requests", "created_at", "updated_at"
    };
    
    private static final String PRIMARY_KEY_FIELD = "reservation_id";
    
    // ISO 8601 date-time formatter
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Override
    public Map<String, String> toCsvRecord(Reservation entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Reservation entity cannot be null");
        }
        
        validateEntity(entity);
        
        Map<String, String> record = new LinkedHashMap<>();
        record.put("reservation_id", entity.getReservationId() != null ? entity.getReservationId().toString() : "");
        record.put("customer_id", entity.getCustomerId() != null ? entity.getCustomerId().toString() : "");
        record.put("restaurant_id", entity.getRestaurantId() != null ? entity.getRestaurantId().toString() : "");
        record.put("table_id", entity.getTableId() != null ? entity.getTableId().toString() : "");
        record.put("party_size", entity.getPartySize() != null ? entity.getPartySize().toString() : "");
        record.put("reservation_datetime", entity.getReservationDatetime() != null ? 
                   entity.getReservationDatetime().format(DATE_TIME_FORMATTER) : "");
        record.put("status", entity.getStatus() != null ? entity.getStatus().getValue() : "");
        record.put("special_requests", entity.getSpecialRequests() != null ? entity.getSpecialRequests() : "");
        record.put("created_at", entity.getCreatedAt() != null ? 
                   entity.getCreatedAt().format(DATE_TIME_FORMATTER) : "");
        record.put("updated_at", entity.getUpdatedAt() != null ? 
                   entity.getUpdatedAt().format(DATE_TIME_FORMATTER) : "");
        
        return record;
    }
    
    @Override
    public Reservation fromCsvRecord(Map<String, String> record) throws RepositoryException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        validateCsvRecord(record);
        
        Reservation reservation = new Reservation();
        
        try {
            // Parse reservation_id
            String reservationIdStr = record.get("reservation_id");
            if (reservationIdStr != null && !reservationIdStr.trim().isEmpty()) {
                reservation.setReservationId(Long.parseLong(reservationIdStr.trim()));
            }
            
            // Parse customer_id (required)
            String customerIdStr = record.get("customer_id");
            if (customerIdStr != null && !customerIdStr.trim().isEmpty()) {
                reservation.setCustomerId(Long.parseLong(customerIdStr.trim()));
            }
            
            // Parse restaurant_id (required)
            String restaurantIdStr = record.get("restaurant_id");
            if (restaurantIdStr != null && !restaurantIdStr.trim().isEmpty()) {
                reservation.setRestaurantId(Long.parseLong(restaurantIdStr.trim()));
            }
            
            // Parse table_id (required)
            String tableIdStr = record.get("table_id");
            if (tableIdStr != null && !tableIdStr.trim().isEmpty()) {
                reservation.setTableId(Long.parseLong(tableIdStr.trim()));
            }
            
            // Parse party_size (required)
            String partySizeStr = record.get("party_size");
            if (partySizeStr != null && !partySizeStr.trim().isEmpty()) {
                reservation.setPartySize(Integer.parseInt(partySizeStr.trim()));
            }
            
            // Parse reservation_datetime (required)
            String datetimeStr = record.get("reservation_datetime");
            if (datetimeStr != null && !datetimeStr.trim().isEmpty()) {
                reservation.setReservationDatetime(LocalDateTime.parse(datetimeStr.trim(), DATE_TIME_FORMATTER));
            }
            
            // Parse status (required)
            String statusStr = record.get("status");
            if (statusStr != null && !statusStr.trim().isEmpty()) {
                ReservationStatus status = ReservationStatus.fromValue(statusStr.trim());
                if (status != null) {
                    reservation.setStatus(status);
                }
            }
            
            // Parse special_requests (optional)
            String specialRequests = record.get("special_requests");
            if (specialRequests != null && !specialRequests.trim().isEmpty()) {
                reservation.setSpecialRequests(specialRequests);
            }
            
            // Parse created_at
            String createdAtStr = record.get("created_at");
            if (createdAtStr != null && !createdAtStr.trim().isEmpty()) {
                reservation.setCreatedAt(LocalDateTime.parse(createdAtStr.trim(), DATE_TIME_FORMATTER));
            }
            
            // Parse updated_at
            String updatedAtStr = record.get("updated_at");
            if (updatedAtStr != null && !updatedAtStr.trim().isEmpty()) {
                reservation.setUpdatedAt(LocalDateTime.parse(updatedAtStr.trim(), DATE_TIME_FORMATTER));
            }
            
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid number format in reservation record: " + e.getMessage(), e);
        } catch (DateTimeParseException e) {
            throw new EntityValidationException("Invalid datetime format in reservation record: " + e.getMessage(), e);
        }
        
        return reservation;
    }
    
    @Override
    public List<String> getColumnNames() {
        List<String> columnNames = new ArrayList<>();
        columnNames.add("reservation_id");
        columnNames.add("customer_id");
        columnNames.add("restaurant_id");
        columnNames.add("table_id");
        columnNames.add("party_size");
        columnNames.add("reservation_datetime");
        columnNames.add("status");
        columnNames.add("special_requests");
        columnNames.add("created_at");
        columnNames.add("updated_at");
        return columnNames;
    }
    
    @Override
    public String getPrimaryKeyField() {
        return PRIMARY_KEY_FIELD;
    }
    
    @Override
    public Object getPrimaryKey(Reservation entity) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Reservation entity cannot be null");
        }
        return entity.getReservationId();
    }
    
    @Override
    public void setPrimaryKey(Reservation entity, Object id) throws RepositoryException {
        if (entity == null) {
            throw new EntityValidationException("Reservation entity cannot be null");
        }
        
        if (id instanceof Long) {
            entity.setReservationId((Long) id);
        } else if (id instanceof String) {
            try {
                entity.setReservationId(Long.parseLong((String) id));
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid reservation ID format: " + id, e);
            }
        } else {
            throw new EntityValidationException("Reservation ID must be a Long or String");
        }
    }
    
    @Override
    public void validateEntity(Reservation entity) throws EntityValidationException {
        if (entity == null) {
            throw new EntityValidationException("Reservation entity cannot be null");
        }
        
        // Validate customer_id (required)
        if (entity.getCustomerId() == null) {
            throw new EntityValidationException("Reservation customer_id is required");
        }
        
        // Validate restaurant_id (required)
        if (entity.getRestaurantId() == null) {
            throw new EntityValidationException("Reservation restaurant_id is required");
        }
        
        // Validate table_id (required)
        if (entity.getTableId() == null) {
            throw new EntityValidationException("Reservation table_id is required");
        }
        
        // Validate party_size (required and must be > 0)
        if (entity.getPartySize() == null) {
            throw new EntityValidationException("Reservation party_size is required");
        }
        
        if (entity.getPartySize() <= 0) {
            throw new EntityValidationException("Reservation party_size must be greater than 0");
        }
        
        // Validate reservation_datetime (required)
        if (entity.getReservationDatetime() == null) {
            throw new EntityValidationException("Reservation reservation_datetime is required");
        }
        
        // Validate status (required)
        if (entity.getStatus() == null) {
            throw new EntityValidationException("Reservation status is required");
        }
    }
    
    @Override
    public void validateCsvRecord(Map<String, String> record) throws EntityValidationException {
        if (record == null) {
            throw new EntityValidationException("CSV record cannot be null");
        }
        
        // Validate customer_id (required)
        String customerId = record.get("customer_id");
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new EntityValidationException("Reservation customer_id is required in CSV record");
        }
        
        try {
            Long.parseLong(customerId.trim());
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid customer_id format in CSV record: " + customerId, e);
        }
        
        // Validate restaurant_id (required)
        String restaurantId = record.get("restaurant_id");
        if (restaurantId == null || restaurantId.trim().isEmpty()) {
            throw new EntityValidationException("Reservation restaurant_id is required in CSV record");
        }
        
        try {
            Long.parseLong(restaurantId.trim());
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid restaurant_id format in CSV record: " + restaurantId, e);
        }
        
        // Validate table_id (required)
        String tableId = record.get("table_id");
        if (tableId == null || tableId.trim().isEmpty()) {
            throw new EntityValidationException("Reservation table_id is required in CSV record");
        }
        
        try {
            Long.parseLong(tableId.trim());
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid table_id format in CSV record: " + tableId, e);
        }
        
        // Validate party_size (required and must be > 0)
        String partySize = record.get("party_size");
        if (partySize == null || partySize.trim().isEmpty()) {
            throw new EntityValidationException("Reservation party_size is required in CSV record");
        }
        
        try {
            int size = Integer.parseInt(partySize.trim());
            if (size <= 0) {
                throw new EntityValidationException("Reservation party_size must be greater than 0: " + size);
            }
        } catch (NumberFormatException e) {
            throw new EntityValidationException("Invalid party_size format in CSV record: " + partySize, e);
        }
        
        // Validate reservation_datetime (required)
        String datetime = record.get("reservation_datetime");
        if (datetime == null || datetime.trim().isEmpty()) {
            throw new EntityValidationException("Reservation reservation_datetime is required in CSV record");
        }
        
        try {
            LocalDateTime.parse(datetime.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new EntityValidationException("Invalid reservation_datetime format in CSV record: " + datetime, e);
        }
        
        // Validate status (required)
        String status = record.get("status");
        if (status == null || status.trim().isEmpty()) {
            throw new EntityValidationException("Reservation status is required in CSV record");
        }
        
        ReservationStatus reservationStatus = ReservationStatus.fromValue(status.trim());
        if (reservationStatus == null) {
            throw new EntityValidationException("Invalid reservation status in CSV record: " + status);
        }
        
        // Validate reservation_id format if present
        String reservationId = record.get("reservation_id");
        if (reservationId != null && !reservationId.trim().isEmpty()) {
            try {
                Long.parseLong(reservationId.trim());
            } catch (NumberFormatException e) {
                throw new EntityValidationException("Invalid reservation_id format in CSV record: " + reservationId, e);
            }
        }
        
        // Validate datetime formats if present
        String createdAt = record.get("created_at");
        if (createdAt != null && !createdAt.trim().isEmpty()) {
            try {
                LocalDateTime.parse(createdAt.trim(), DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new EntityValidationException("Invalid created_at format in CSV record: " + createdAt, e);
            }
        }
        
        String updatedAt = record.get("updated_at");
        if (updatedAt != null && !updatedAt.trim().isEmpty()) {
            try {
                LocalDateTime.parse(updatedAt.trim(), DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new EntityValidationException("Invalid updated_at format in CSV record: " + updatedAt, e);
            }
        }
    }
}
