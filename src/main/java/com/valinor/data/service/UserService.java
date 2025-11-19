package com.valinor.data.service;

import com.valinor.data.config.UserConfig;
import com.valinor.data.dto.UserCreateRequest;
import com.valinor.data.dto.UserResponse;
import com.valinor.data.dto.UserUpdateRequest;
import com.valinor.data.entity.User;
import com.valinor.data.entity.UserRole;
import com.valinor.data.exception.DuplicateUserException;
import com.valinor.data.exception.RepositoryException;
import com.valinor.data.exception.UserServiceException;
import com.valinor.data.repository.UserRepository;
import com.valinor.data.util.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for managing user accounts.
 * Provides business logic for user CRUD operations, password management,
 * and account activation/deactivation.
 */
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final Pattern usernamePattern;
    private final Pattern emailPattern;
    
    /**
     * Constructs a new UserService.
     * 
     * @param userRepository the user repository
     */
    public UserService(UserRepository userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null");
        }
        
        this.userRepository = userRepository;
        this.usernamePattern = Pattern.compile(UserConfig.USERNAME_PATTERN);
        this.emailPattern = Pattern.compile(UserConfig.EMAIL_PATTERN);
    }
    
    /**
     * Creates a new user account.
     * 
     * @param request the user creation request
     * @return the created user
     * @throws UserServiceException if creation fails
     * @throws DuplicateUserException if username or email already exists
     */
    public User createUser(UserCreateRequest request) throws UserServiceException {
        if (request == null) {
            throw new IllegalArgumentException("User create request cannot be null");
        }
        
        try {
            // Validate request
            validateCreateRequest(request);
            
            // Check for duplicates
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateUserException("Username already exists: " + request.getUsername());
            }
            
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateUserException("Email already exists: " + request.getEmail());
            }
            
            // Hash password
            String passwordHash = PasswordHasher.hash(request.getPassword());
            
            // Create user entity
            User user = new User(
                request.getUsername(),
                passwordHash,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getRole()
            );
            
            user.setPhone(request.getPhone());
            user.setRestaurantId(request.getRestaurantId());
            user.setIsActive(true);
            user.setCreatedAt(LocalDateTime.now());
            
            // Save user
            user = userRepository.save(user);
            
            logger.info("Created new user: {} (ID: {})", user.getUsername(), user.getUserId());
            return user;
            
        } catch (DuplicateUserException e) {
            throw e;
        } catch (RepositoryException e) {
            logger.error("Repository error during user creation", e);
            throw new UserServiceException("Failed to create user", e);
        }
    }
    
    /**
     * Updates an existing user account.
     * 
     * @param userId the user ID to update
     * @param request the update request
     * @return the updated user
     * @throws UserServiceException if update fails
     */
    public User updateUser(Long userId, UserUpdateRequest request) throws UserServiceException {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        if (request == null) {
            throw new IllegalArgumentException("User update request cannot be null");
        }
        
        if (!request.hasUpdates()) {
            throw new UserServiceException("No fields to update");
        }
        
        try {
            // Find existing user
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                throw new UserServiceException("User not found with ID: " + userId);
            }
            
            User user = userOpt.get();
            
            // Validate and apply updates
            if (request.getEmail() != null) {
                validateEmail(request.getEmail());
                
                // Check for email duplicates (excluding current user)
                Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
                if (existingUser.isPresent() && !existingUser.get().getUserId().equals(userId)) {
                    throw new DuplicateUserException("Email already exists: " + request.getEmail());
                }
                
                user.setEmail(request.getEmail());
            }
            
            if (request.getFirstName() != null) {
                validateNotEmpty(request.getFirstName(), "First name");
                user.setFirstName(request.getFirstName());
            }
            
            if (request.getLastName() != null) {
                validateNotEmpty(request.getLastName(), "Last name");
                user.setLastName(request.getLastName());
            }
            
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            
            if (request.getRole() != null) {
                user.setRole(request.getRole());
            }
            
            if (request.getRestaurantId() != null) {
                user.setRestaurantId(request.getRestaurantId());
            }
            
            if (request.getIsActive() != null) {
                user.setIsActive(request.getIsActive());
            }
            
            // Update user
            user = userRepository.update(user);
            
            logger.info("Updated user: {} (ID: {})", user.getUsername(), user.getUserId());
            return user;
            
        } catch (DuplicateUserException e) {
            throw e;
        } catch (RepositoryException e) {
            logger.error("Repository error during user update", e);
            throw new UserServiceException("Failed to update user", e);
        }
    }
    
    /**
     * Deletes a user account.
     * 
     * @param userId the user ID to delete
     * @throws UserServiceException if deletion fails
     */
    public void deleteUser(Long userId) throws UserServiceException {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        try {
            boolean deleted = userRepository.deleteById(userId);
            if (!deleted) {
                throw new UserServiceException("User not found with ID: " + userId);
            }
            
            logger.info("Deleted user with ID: {}", userId);
            
        } catch (RepositoryException e) {
            logger.error("Repository error during user deletion", e);
            throw new UserServiceException("Failed to delete user", e);
        }
    }
    
    /**
     * Gets a user by ID.
     * 
     * @param userId the user ID
     * @return optional containing the user if found
     * @throws UserServiceException if retrieval fails
     */
    public Optional<User> getUserById(Long userId) throws UserServiceException {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        try {
            return userRepository.findById(userId);
        } catch (RepositoryException e) {
            logger.error("Repository error during user retrieval", e);
            throw new UserServiceException("Failed to retrieve user", e);
        }
    }
    
    /**
     * Gets a user by username.
     * 
     * @param username the username
     * @return optional containing the user if found
     * @throws UserServiceException if retrieval fails
     */
    public Optional<User> getUserByUsername(String username) throws UserServiceException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        try {
            return userRepository.findByUsername(username);
        } catch (RepositoryException e) {
            logger.error("Repository error during user retrieval", e);
            throw new UserServiceException("Failed to retrieve user", e);
        }
    }
    
    /**
     * Gets all users for a restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @return list of users
     * @throws UserServiceException if retrieval fails
     */
    public List<User> getUsersByRestaurant(Long restaurantId) throws UserServiceException {
        if (restaurantId == null) {
            throw new IllegalArgumentException("Restaurant ID cannot be null");
        }
        
        try {
            return userRepository.findByRestaurantId(restaurantId);
        } catch (RepositoryException e) {
            logger.error("Repository error during users retrieval", e);
            throw new UserServiceException("Failed to retrieve users", e);
        }
    }
    
    /**
     * Gets all users with a specific role.
     * 
     * @param role the user role
     * @return list of users
     * @throws UserServiceException if retrieval fails
     */
    public List<User> getUsersByRole(UserRole role) throws UserServiceException {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        
        try {
            return userRepository.findByRole(role);
        } catch (RepositoryException e) {
            logger.error("Repository error during users retrieval", e);
            throw new UserServiceException("Failed to retrieve users", e);
        }
    }
    
    /**
     * Gets all users.
     * 
     * @return list of all users
     * @throws UserServiceException if retrieval fails
     */
    public List<User> getAllUsers() throws UserServiceException {
        try {
            return userRepository.findAll();
        } catch (RepositoryException e) {
            logger.error("Repository error during users retrieval", e);
            throw new UserServiceException("Failed to retrieve users", e);
        }
    }
    
    /**
     * Gets all users as response DTOs.
     * 
     * @return list of user responses
     * @throws UserServiceException if retrieval fails
     */
    public List<UserResponse> getAllUsersAsResponses() throws UserServiceException {
        List<User> users = getAllUsers();
        return users.stream()
                   .map(UserResponse::fromUser)
                   .collect(Collectors.toList());
    }
    
    /**
     * Activates a user account.
     * 
     * @param userId the user ID
     * @throws UserServiceException if activation fails
     */
    public void activateUser(Long userId) throws UserServiceException {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                throw new UserServiceException("User not found with ID: " + userId);
            }
            
            User user = userOpt.get();
            user.setIsActive(true);
            userRepository.update(user);
            
            logger.info("Activated user: {} (ID: {})", user.getUsername(), userId);
            
        } catch (RepositoryException e) {
            logger.error("Repository error during user activation", e);
            throw new UserServiceException("Failed to activate user", e);
        }
    }
    
    /**
     * Deactivates a user account.
     * 
     * @param userId the user ID
     * @throws UserServiceException if deactivation fails
     */
    public void deactivateUser(Long userId) throws UserServiceException {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                throw new UserServiceException("User not found with ID: " + userId);
            }
            
            User user = userOpt.get();
            user.setIsActive(false);
            userRepository.update(user);
            
            logger.info("Deactivated user: {} (ID: {})", user.getUsername(), userId);
            
        } catch (RepositoryException e) {
            logger.error("Repository error during user deactivation", e);
            throw new UserServiceException("Failed to deactivate user", e);
        }
    }
    
    /**
     * Changes a user's password.
     * 
     * @param userId the user ID
     * @param oldPassword the current password
     * @param newPassword the new password
     * @throws UserServiceException if password change fails
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) throws UserServiceException {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new UserServiceException("Current password is required");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new UserServiceException("New password is required");
        }
        
        // Validate new password
        if (!PasswordHasher.isValidPassword(newPassword)) {
            throw new UserServiceException(PasswordHasher.getPasswordRequirements());
        }
        
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                throw new UserServiceException("User not found with ID: " + userId);
            }
            
            User user = userOpt.get();
            
            // Verify old password
            if (!PasswordHasher.verify(oldPassword, user.getPasswordHash())) {
                throw new UserServiceException("Current password is incorrect");
            }
            
            // Hash and set new password
            String newPasswordHash = PasswordHasher.hash(newPassword);
            user.setPasswordHash(newPasswordHash);
            userRepository.update(user);
            
            logger.info("Changed password for user: {} (ID: {})", user.getUsername(), userId);
            
        } catch (RepositoryException e) {
            logger.error("Repository error during password change", e);
            throw new UserServiceException("Failed to change password", e);
        }
    }
    
    /**
     * Resets a user's password (admin function).
     * 
     * @param userId the user ID
     * @param newPassword the new password
     * @throws UserServiceException if password reset fails
     */
    public void resetPassword(Long userId, String newPassword) throws UserServiceException {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new UserServiceException("New password is required");
        }
        
        // Validate new password
        if (!PasswordHasher.isValidPassword(newPassword)) {
            throw new UserServiceException(PasswordHasher.getPasswordRequirements());
        }
        
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                throw new UserServiceException("User not found with ID: " + userId);
            }
            
            User user = userOpt.get();
            
            // Hash and set new password
            String newPasswordHash = PasswordHasher.hash(newPassword);
            user.setPasswordHash(newPasswordHash);
            userRepository.update(user);
            
            logger.info("Reset password for user: {} (ID: {})", user.getUsername(), userId);
            
        } catch (RepositoryException e) {
            logger.error("Repository error during password reset", e);
            throw new UserServiceException("Failed to reset password", e);
        }
    }
    
    /**
     * Validates a user creation request.
     * 
     * @param request the request to validate
     * @throws UserServiceException if validation fails
     */
    private void validateCreateRequest(UserCreateRequest request) throws UserServiceException {
        // Validate username
        validateUsername(request.getUsername());
        
        // Validate password
        if (!PasswordHasher.isValidPassword(request.getPassword())) {
            throw new UserServiceException(PasswordHasher.getPasswordRequirements());
        }
        
        // Validate email
        validateEmail(request.getEmail());
        
        // Validate names
        validateNotEmpty(request.getFirstName(), "First name");
        validateNotEmpty(request.getLastName(), "Last name");
        
        // Validate role
        if (request.getRole() == null) {
            throw new UserServiceException("Role is required");
        }
    }
    
    /**
     * Validates a username.
     * 
     * @param username the username to validate
     * @throws UserServiceException if validation fails
     */
    private void validateUsername(String username) throws UserServiceException {
        if (username == null || username.trim().isEmpty()) {
            throw new UserServiceException("Username is required");
        }
        
        if (username.length() < UserConfig.USERNAME_MIN_LENGTH) {
            throw new UserServiceException(
                String.format("Username must be at least %d characters", UserConfig.USERNAME_MIN_LENGTH)
            );
        }
        
        if (username.length() > UserConfig.USERNAME_MAX_LENGTH) {
            throw new UserServiceException(
                String.format("Username must not exceed %d characters", UserConfig.USERNAME_MAX_LENGTH)
            );
        }
        
        if (!usernamePattern.matcher(username).matches()) {
            throw new UserServiceException("Username can only contain letters, numbers, and underscores");
        }
    }
    
    /**
     * Validates an email address.
     * 
     * @param email the email to validate
     * @throws UserServiceException if validation fails
     */
    private void validateEmail(String email) throws UserServiceException {
        if (email == null || email.trim().isEmpty()) {
            throw new UserServiceException("Email is required");
        }
        
        if (!emailPattern.matcher(email).matches()) {
            throw new UserServiceException("Invalid email format");
        }
    }
    
    /**
     * Validates that a string is not empty.
     * 
     * @param value the value to validate
     * @param fieldName the field name for error messages
     * @throws UserServiceException if validation fails
     */
    private void validateNotEmpty(String value, String fieldName) throws UserServiceException {
        if (value == null || value.trim().isEmpty()) {
            throw new UserServiceException(fieldName + " is required");
        }
    }
}
