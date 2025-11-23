package com.valinor.service.user;

import com.valinor.infrastructure.config.UserConfig;
import com.valinor.service.dto.user.LoginRequest;
import com.valinor.service.dto.user.LoginResponse;
import com.valinor.service.dto.user.UserResponse;
import com.valinor.domain.model.User;
import com.valinor.domain.model.UserSession;
import com.valinor.exception.AuthenticationException;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.UserRepository;
import com.valinor.repository.UserSessionRepository;
import com.valinor.infrastructure.security.PasswordHasher;
import com.valinor.infrastructure.security.SessionTokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for handling user authentication and session management.
 * Provides login, logout, and session validation functionality.
 */
public class AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    
    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    
    /**
     * Constructs a new AuthenticationService.
     * 
     * @param userRepository the user repository
     * @param sessionRepository the session repository
     */
    public AuthenticationService(UserRepository userRepository, UserSessionRepository sessionRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null");
        }
        if (sessionRepository == null) {
            throw new IllegalArgumentException("UserSessionRepository cannot be null");
        }
        
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }
    
    /**
     * Authenticates a user and creates a new session.
     * 
     * @param request the login request containing credentials
     * @return a login response with session token and user information
     * @throws AuthenticationException if authentication fails
     */
    public LoginResponse login(LoginRequest request) throws AuthenticationException {
        if (request == null) {
            throw new IllegalArgumentException("Login request cannot be null");
        }
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new AuthenticationException("Username is required");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new AuthenticationException("Password is required");
        }
        
        try {
            // Find user by username
            Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
            if (!userOpt.isPresent()) {
                logger.warn("Login failed: User not found - {}", request.getUsername());
                throw new AuthenticationException("Invalid username or password");
            }
            
            User user = userOpt.get();
            
            // Check if account is active
            if (!user.isUsable()) {
                logger.warn("Login failed: Account not usable - {}", request.getUsername());
                throw new AuthenticationException("Account is inactive or not properly configured");
            }
            
            // Verify password
            if (!verifyPassword(request.getPassword(), user.getPasswordHash())) {
                logger.warn("Login failed: Invalid password - {}", request.getUsername());
                throw new AuthenticationException("Invalid username or password");
            }
            
            // Create new session
            String sessionToken = SessionTokenGenerator.generateToken();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(UserConfig.SESSION_EXPIRY_HOURS);
            
            UserSession session = new UserSession(user.getUserId(), sessionToken, expiresAt);
            session.setIpAddress(request.getIpAddress());
            sessionRepository.save(session);
            
            // Update last login time
            user.setLastLogin(LocalDateTime.now());
            userRepository.update(user);
            
            // Build response
            UserResponse userResponse = UserResponse.fromUser(user);
            LoginResponse response = new LoginResponse(
                sessionToken,
                userResponse,
                expiresAt,
                user.getPermissions()
            );
            
            logger.info("User logged in successfully: {}", user.getUsername());
            return response;
            
        } catch (AuthenticationException e) {
            throw e;
        } catch (RepositoryException e) {
            logger.error("Repository error during login", e);
            throw new AuthenticationException("Authentication failed due to system error", e);
        }
    }
    
    /**
     * Logs out a user by expiring their session.
     * 
     * @param sessionToken the session token to invalidate
     * @throws AuthenticationException if logout fails
     */
    public void logout(String sessionToken) throws AuthenticationException {
        if (sessionToken == null || sessionToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Session token cannot be null or empty");
        }
        
        try {
            boolean expired = sessionRepository.expireSession(sessionToken);
            if (expired) {
                logger.info("User logged out successfully");
            } else {
                logger.warn("Logout attempted with invalid session token");
            }
        } catch (RepositoryException e) {
            logger.error("Repository error during logout", e);
            throw new AuthenticationException("Logout failed due to system error", e);
        }
    }
    
    /**
     * Validates a session token and returns the associated user.
     * 
     * @param sessionToken the session token to validate
     * @return optional containing the user if session is valid
     */
    public Optional<User> validateSession(String sessionToken) {
        if (sessionToken == null || sessionToken.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            // Find session by token
            Optional<UserSession> sessionOpt = sessionRepository.findBySessionToken(sessionToken);
            if (!sessionOpt.isPresent()) {
                logger.debug("Session validation failed: Session not found");
                return Optional.empty();
            }
            
            UserSession session = sessionOpt.get();
            
            // Check if session is valid
            if (!session.isValid()) {
                logger.debug("Session validation failed: Session expired or inactive");
                return Optional.empty();
            }
            
            // Find and return the user
            Optional<User> userOpt = userRepository.findById(session.getUserId());
            if (!userOpt.isPresent()) {
                logger.warn("Session validation failed: User not found for session");
                return Optional.empty();
            }
            
            User user = userOpt.get();
            if (!user.isUsable()) {
                logger.debug("Session validation failed: User account not usable");
                return Optional.empty();
            }
            
            return Optional.of(user);
            
        } catch (RepositoryException e) {
            logger.error("Repository error during session validation", e);
            return Optional.empty();
        }
    }
    
    /**
     * Refreshes a session by extending its expiration time.
     * 
     * @param sessionToken the session token to refresh
     * @return the updated session
     * @throws AuthenticationException if refresh fails
     */
    public UserSession refreshSession(String sessionToken) throws AuthenticationException {
        if (sessionToken == null || sessionToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Session token cannot be null or empty");
        }
        
        try {
            Optional<UserSession> sessionOpt = sessionRepository.findBySessionToken(sessionToken);
            if (!sessionOpt.isPresent()) {
                throw new AuthenticationException("Session not found");
            }
            
            UserSession session = sessionOpt.get();
            
            if (!session.isValid()) {
                throw new AuthenticationException("Session is expired or inactive");
            }
            
            // Extend expiration
            LocalDateTime newExpiration = LocalDateTime.now().plusHours(UserConfig.SESSION_EXPIRY_HOURS);
            session.setExpiresAt(newExpiration);
            sessionRepository.update(session);
            
            logger.info("Session refreshed successfully");
            return session;
            
        } catch (RepositoryException e) {
            logger.error("Repository error during session refresh", e);
            throw new AuthenticationException("Session refresh failed due to system error", e);
        }
    }
    
    /**
     * Logs out all sessions for a specific user.
     * 
     * @param userId the user ID
     * @return the number of sessions logged out
     * @throws AuthenticationException if operation fails
     */
    public int logoutAllSessions(Long userId) throws AuthenticationException {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        try {
            int count = sessionRepository.expireAllUserSessions(userId);
            logger.info("Logged out {} sessions for user ID: {}", count, userId);
            return count;
        } catch (RepositoryException e) {
            logger.error("Repository error during logout all sessions", e);
            throw new AuthenticationException("Logout all sessions failed due to system error", e);
        }
    }
    
    /**
     * Verifies a plain text password against a hashed password.
     * 
     * @param plainPassword the plain text password
     * @param hashedPassword the hashed password
     * @return true if the password matches
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return PasswordHasher.verify(plainPassword, hashedPassword);
        } catch (Exception e) {
            logger.error("Error verifying password", e);
            return false;
        }
    }
    
    /**
     * Hashes a plain text password.
     * 
     * @param plainPassword the plain text password
     * @return the hashed password
     * @throws IllegalArgumentException if password is null or empty
     */
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        return PasswordHasher.hash(plainPassword);
    }
    
    /**
     * Cleans up expired sessions.
     * This should be called periodically to maintain session hygiene.
     * 
     * @return the number of sessions cleaned up
     */
    public int cleanupExpiredSessions() {
        try {
            int count = sessionRepository.cleanupExpiredSessions();
            logger.info("Cleaned up {} expired sessions", count);
            return count;
        } catch (RepositoryException e) {
            logger.error("Error cleaning up expired sessions", e);
            return 0;
        }
    }
}
