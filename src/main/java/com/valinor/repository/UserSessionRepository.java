package com.valinor.repository;

import com.valinor.domain.model.UserSession;
import com.valinor.exception.RepositoryException;
import com.valinor.repository.mapper.UserSessionEntityMapper;

import java.util.List;
import java.util.Optional;

/**
 * CSV-based repository for UserSession entities.
 * Provides CRUD operations for session data stored in CSV files.
 */
public class UserSessionRepository extends AbstractCsvRepository<UserSession, Long> {
    
    /**
     * Constructs a new UserSessionRepository.
     * 
     * @param filePath the path to the user sessions CSV file
     * @throws RepositoryException if initialization fails
     */
    public UserSessionRepository(String filePath) throws RepositoryException {
        super(filePath, new UserSessionEntityMapper());
    }
    
    /**
     * Finds a session by its token.
     * 
     * @param token the session token to search for
     * @return optional containing the session if found
     * @throws RepositoryException if search fails
     */
    public Optional<UserSession> findBySessionToken(String token) throws RepositoryException {
        return findOneByField("session_token", token);
    }
    
    /**
     * Finds all sessions for a specific user.
     * 
     * @param userId the user ID
     * @return list of sessions for the user
     * @throws RepositoryException if search fails
     */
    public List<UserSession> findByUserId(Long userId) throws RepositoryException {
        return findByField("user_id", userId);
    }
    
    /**
     * Finds all active sessions.
     * 
     * @return list of active sessions
     * @throws RepositoryException if search fails
     */
    public List<UserSession> findActiveSessions() throws RepositoryException {
        return findByField("is_active", true);
    }
    
    /**
     * Finds all active sessions for a specific user.
     * 
     * @param userId the user ID
     * @return list of active sessions for the user
     * @throws RepositoryException if search fails
     */
    public List<UserSession> findActiveSessionsByUserId(Long userId) throws RepositoryException {
        return findWhere(session -> 
            session.getUserId().equals(userId) && 
            session.getIsActive() != null && 
            session.getIsActive()
        );
    }
    
    /**
     * Finds all valid (active and not expired) sessions.
     * 
     * @return list of valid sessions
     * @throws RepositoryException if search fails
     */
    public List<UserSession> findValidSessions() throws RepositoryException {
        return findWhere(session -> session.isValid());
    }
    
    /**
     * Finds all expired sessions.
     * 
     * @return list of expired sessions
     * @throws RepositoryException if search fails
     */
    public List<UserSession> findExpiredSessions() throws RepositoryException {
        return findWhere(session -> session.isExpired());
    }
    
    /**
     * Expires a session by its token (sets is_active to false).
     * 
     * @param token the session token
     * @return true if the session was expired, false if not found
     * @throws RepositoryException if operation fails
     */
    public boolean expireSession(String token) throws RepositoryException {
        Optional<UserSession> sessionOpt = findBySessionToken(token);
        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            session.expire();
            update(session);
            return true;
        }
        return false;
    }
    
    /**
     * Expires all sessions for a specific user.
     * 
     * @param userId the user ID
     * @return the number of sessions expired
     * @throws RepositoryException if operation fails
     */
    public int expireAllUserSessions(Long userId) throws RepositoryException {
        List<UserSession> sessions = findActiveSessionsByUserId(userId);
        int count = 0;
        
        for (UserSession session : sessions) {
            session.expire();
            update(session);
            count++;
        }
        
        return count;
    }
    
    /**
     * Cleans up expired sessions by marking them as inactive.
     * This should be called periodically to maintain session hygiene.
     * 
     * @return the number of sessions cleaned up
     * @throws RepositoryException if operation fails
     */
    public int cleanupExpiredSessions() throws RepositoryException {
        List<UserSession> expiredSessions = findWhere(session -> 
            session.isExpired() && 
            session.getIsActive() != null && 
            session.getIsActive()
        );
        
        int count = 0;
        for (UserSession session : expiredSessions) {
            session.expire();
            update(session);
            count++;
        }
        
        return count;
    }
    
    /**
     * Deletes all expired and inactive sessions.
     * This permanently removes old session records from storage.
     * 
     * @return the number of sessions deleted
     * @throws RepositoryException if operation fails
     */
    public int deleteExpiredSessions() throws RepositoryException {
        List<UserSession> expiredSessions = findWhere(session -> 
            session.isExpired() && 
            (session.getIsActive() == null || !session.getIsActive())
        );
        
        int count = 0;
        for (UserSession session : expiredSessions) {
            if (deleteById(session.getSessionId())) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Counts active sessions for a user.
     * 
     * @param userId the user ID
     * @return the number of active sessions
     * @throws RepositoryException if count fails
     */
    public long countActiveSessionsByUserId(Long userId) throws RepositoryException {
        return findActiveSessionsByUserId(userId).size();
    }
    
    /**
     * Checks if a session token exists and is valid.
     * 
     * @param token the session token
     * @return true if the session exists and is valid
     * @throws RepositoryException if check fails
     */
    public boolean isValidSession(String token) throws RepositoryException {
        Optional<UserSession> sessionOpt = findBySessionToken(token);
        return sessionOpt.isPresent() && sessionOpt.get().isValid();
    }
    
    @Override
    protected boolean fieldValueMatches(UserSession entity, String fieldName, Object expectedValue) {
        if (entity == null || expectedValue == null) {
            return false;
        }
        
        try {
            switch (fieldName.toLowerCase()) {
                case "session_id":
                    return expectedValue.equals(entity.getSessionId());
                case "user_id":
                    return expectedValue.equals(entity.getUserId());
                case "session_token":
                    return expectedValue.equals(entity.getSessionToken());
                case "ip_address":
                    return expectedValue.equals(entity.getIpAddress());
                case "is_active":
                    if (expectedValue instanceof Boolean) {
                        return expectedValue.equals(entity.getIsActive());
                    } else if (expectedValue instanceof String) {
                        return Boolean.parseBoolean((String) expectedValue) == entity.getIsActive();
                    }
                    return false;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
