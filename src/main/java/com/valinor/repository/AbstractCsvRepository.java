package com.valinor.repository;

import com.valinor.exception.CsvException;
import com.valinor.exception.EntityValidationException;
import com.valinor.exception.RepositoryException;
import com.valinor.infrastructure.csv.CsvFileManager;
import com.valinor.infrastructure.csv.CsvParser;
import com.valinor.repository.mapper.EntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Abstract base class for CSV-based repositories.
 * Provides common CRUD operations and CSV file management functionality.
 * 
 * @param <T> the type of entity this repository manages
 * @param <ID> the type of the entity's identifier
 */
public abstract class AbstractCsvRepository<T, ID> implements CsvRepository<T, ID> {
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractCsvRepository.class);
    
    protected final CsvFileManager fileManager;
    protected final CsvParser csvParser;
    protected final EntityMapper<T> entityMapper;
    protected final Map<ID, T> entityCache;
    protected final AtomicLong idGenerator;
    protected final String[] columnNames;
    
    // Cache management
    private volatile boolean cacheDirty = true;
    private final Object cacheLock = new Object();
    
    /**
     * Constructs a new AbstractCsvRepository.
     * 
     * @param filePath the path to the CSV file
     * @param entityMapper the entity mapper for converting between entities and CSV
     * @throws RepositoryException if initialization fails
     */
    protected AbstractCsvRepository(String filePath, EntityMapper<T> entityMapper) throws RepositoryException {
        try {
            this.fileManager = new CsvFileManager(filePath);
            this.csvParser = new CsvParser();
            this.entityMapper = entityMapper;
            this.entityCache = new ConcurrentHashMap<>();
            this.idGenerator = new AtomicLong(0);
            this.columnNames = entityMapper.getColumnNames().toArray(new String[0]);
            
            // Initialize the file with header if it doesn't exist
            initializeFile();
            
            // Load initial data and determine next ID
            loadCache();
            initializeIdGenerator();
            
            logger.info("Initialized CSV repository for file: {}", filePath);
        } catch (CsvException e) {
            throw new RepositoryException("Failed to initialize CSV repository", e);
        }
    }
    
    @Override
    public T save(T entity) throws RepositoryException {
        try {
            // Validate the entity
            entityMapper.validateEntity(entity);
            
            // Generate ID if needed
            ID id = (ID) entityMapper.getPrimaryKey(entity);
            if (id == null) {
                id = generateNextId();
                entityMapper.setPrimaryKey(entity, id);
            }
            
            synchronized (cacheLock) {
                // Add to cache
                entityCache.put(id, entity);
                cacheDirty = true;
                
                // Persist to file
                persistCacheToFile();
            }
            
            logger.debug("Saved entity with ID: {}", id);
            return entity;
            
        } catch (EntityValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException("Failed to save entity", e);
        }
    }
    
    @Override
    public Optional<T> findById(ID id) throws RepositoryException {
        try {
            ensureCacheLoaded();
            return Optional.ofNullable(entityCache.get(id));
        } catch (Exception e) {
            throw new RepositoryException("Failed to find entity by ID: " + id, e);
        }
    }
    
    @Override
    public List<T> findAll() throws RepositoryException {
        try {
            ensureCacheLoaded();
            return new ArrayList<>(entityCache.values());
        } catch (Exception e) {
            throw new RepositoryException("Failed to find all entities", e);
        }
    }
    
    @Override
    public T update(T entity) throws RepositoryException {
        try {
            // Validate the entity
            entityMapper.validateEntity(entity);
            
            ID id = (ID) entityMapper.getPrimaryKey(entity);
            if (id == null) {
                throw new RepositoryException("Cannot update entity without ID");
            }
            
            synchronized (cacheLock) {
                // Check if entity exists
                if (!entityCache.containsKey(id)) {
                    throw new RepositoryException("Entity not found with ID: " + id);
                }
                
                // Update in cache
                entityCache.put(id, entity);
                cacheDirty = true;
                
                // Persist to file
                persistCacheToFile();
            }
            
            logger.debug("Updated entity with ID: {}", id);
            return entity;
            
        } catch (EntityValidationException e) {
            throw e;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException("Failed to update entity", e);
        }
    }
    
    @Override
    public boolean deleteById(ID id) throws RepositoryException {
        try {
            synchronized (cacheLock) {
                T removed = entityCache.remove(id);
                if (removed != null) {
                    cacheDirty = true;
                    persistCacheToFile();
                    logger.debug("Deleted entity with ID: {}", id);
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            throw new RepositoryException("Failed to delete entity with ID: " + id, e);
        }
    }
    
    @Override
    public boolean existsById(ID id) throws RepositoryException {
        try {
            ensureCacheLoaded();
            return entityCache.containsKey(id);
        } catch (Exception e) {
            throw new RepositoryException("Failed to check if entity exists with ID: " + id, e);
        }
    }
    
    @Override
    public long count() throws RepositoryException {
        try {
            ensureCacheLoaded();
            return entityCache.size();
        } catch (Exception e) {
            throw new RepositoryException("Failed to count entities", e);
        }
    }
    
    @Override
    public List<T> findByField(String fieldName, Object value) throws RepositoryException {
        try {
            ensureCacheLoaded();
            return entityCache.values().stream()
                .filter(entity -> fieldValueMatches(entity, fieldName, value))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RepositoryException("Failed to find entities by field: " + fieldName, e);
        }
    }
    
    @Override
    public List<T> findByFields(Map<String, Object> criteria) throws RepositoryException {
        try {
            ensureCacheLoaded();
            return entityCache.values().stream()
                .filter(entity -> allFieldsMatch(entity, criteria))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RepositoryException("Failed to find entities by criteria", e);
        }
    }
    
    @Override
    public Optional<T> findOneByField(String fieldName, Object value) throws RepositoryException {
        List<T> results = findByField(fieldName, value);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    @Override
    public List<T> findWhere(Predicate<T> predicate) throws RepositoryException {
        try {
            ensureCacheLoaded();
            return entityCache.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RepositoryException("Failed to find entities with predicate", e);
        }
    }
    
    @Override
    public List<T> saveAll(List<T> entities) throws RepositoryException {
        try {
            List<T> savedEntities = new ArrayList<>();
            
            synchronized (cacheLock) {
                for (T entity : entities) {
                    // Validate the entity
                    entityMapper.validateEntity(entity);
                    
                    // Generate ID if needed
                    ID id = (ID) entityMapper.getPrimaryKey(entity);
                    if (id == null) {
                        id = generateNextId();
                        entityMapper.setPrimaryKey(entity, id);
                    }
                    
                    // Add to cache
                    entityCache.put(id, entity);
                    savedEntities.add(entity);
                }
                
                cacheDirty = true;
                persistCacheToFile();
            }
            
            logger.debug("Saved {} entities", savedEntities.size());
            return savedEntities;
            
        } catch (EntityValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException("Failed to save entities", e);
        }
    }
    
    @Override
    public int deleteAllById(List<ID> ids) throws RepositoryException {
        try {
            synchronized (cacheLock) {
                int deletedCount = 0;
                for (ID id : ids) {
                    if (entityCache.remove(id) != null) {
                        deletedCount++;
                    }
                }
                
                if (deletedCount > 0) {
                    cacheDirty = true;
                    persistCacheToFile();
                }
                
                logger.debug("Deleted {} entities", deletedCount);
                return deletedCount;
            }
        } catch (Exception e) {
            throw new RepositoryException("Failed to delete entities", e);
        }
    }
    
    @Override
    public int deleteByFields(Map<String, Object> criteria) throws RepositoryException {
        List<T> toDelete = findByFields(criteria);
            List<ID> ids = toDelete.stream()
                .map(entity -> {
                    try {
                        return (ID) entityMapper.getPrimaryKey(entity);
                    } catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        return deleteAllById(ids);
    }
    
    @Override
    public String getFilePath() {
        return fileManager.getFilePath().toString();
    }
    
    @Override
    public void reload() throws RepositoryException {
        synchronized (cacheLock) {
            cacheDirty = true;
            loadCache();
            initializeIdGenerator();
        }
        logger.info("Reloaded repository from file: {}", getFilePath());
    }
    
    /**
     * Initializes the CSV file with header if it doesn't exist.
     * 
     * @throws CsvException if initialization fails
     */
    private void initializeFile() throws CsvException {
        String header = String.join(",", columnNames);
        fileManager.createIfNotExists(header);
    }
    
    /**
     * Loads all entities from the CSV file into the cache.
     * 
     * @throws RepositoryException if loading fails
     */
    private void loadCache() throws RepositoryException {
        try {
            List<String> lines = fileManager.readAllLines();
            if (lines.isEmpty()) {
                entityCache.clear();
                return;
            }
            
            List<Map<String, String>> records = csvParser.parseLines(lines);
            Map<ID, T> newCache = new ConcurrentHashMap<>();
            
            for (Map<String, String> record : records) {
                try {
                    T entity = entityMapper.fromCsvRecord(record);
                    ID id = (ID) entityMapper.getPrimaryKey(entity);
                    if (id != null) {
                        newCache.put(id, entity);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to parse record: {}", record, e);
                }
            }
            
            synchronized (cacheLock) {
                entityCache.clear();
                entityCache.putAll(newCache);
                cacheDirty = false;
            }
            
            logger.debug("Loaded {} entities into cache", entityCache.size());
            
        } catch (CsvException e) {
            throw new RepositoryException("Failed to load cache from file", e);
        }
    }
    
    /**
     * Initializes the ID generator based on existing entities.
     */
    private void initializeIdGenerator() {
        long maxId = 0;
        for (T entity : entityCache.values()) {
            try {
                Object id = entityMapper.getPrimaryKey(entity);
                if (id instanceof Long) {
                    maxId = Math.max(maxId, (Long) id);
                } else if (id instanceof Integer) {
                    maxId = Math.max(maxId, ((Integer) id).longValue());
                } else if (id instanceof String) {
                    try {
                        long numericId = Long.parseLong((String) id);
                        maxId = Math.max(maxId, numericId);
                    } catch (NumberFormatException e) {
                        // Ignore non-numeric IDs
                    }
                }
            } catch (RepositoryException e) {
                // Ignore entities that can't provide their ID
                logger.warn("Failed to get primary key for entity during ID generator initialization", e);
            }
        }
        idGenerator.set(maxId + 1);
    }
    
    /**
     * Generates the next ID for new entities.
     * 
     * @return the next ID
     */
    @SuppressWarnings("unchecked")
    private ID generateNextId() {
        return (ID) Long.valueOf(idGenerator.getAndIncrement());
    }
    
    /**
     * Ensures the cache is loaded and up-to-date.
     * 
     * @throws RepositoryException if loading fails
     */
    private void ensureCacheLoaded() throws RepositoryException {
        if (cacheDirty) {
            synchronized (cacheLock) {
                if (cacheDirty) {
                    loadCache();
                }
            }
        }
    }
    
    /**
     * Persists the current cache to the CSV file.
     * 
     * @throws RepositoryException if persistence fails
     */
    private void persistCacheToFile() throws RepositoryException {
        try {
            List<Map<String, String>> records = new ArrayList<>();
            for (T entity : entityCache.values()) {
                try {
                    Map<String, String> record = entityMapper.toCsvRecord(entity);
                    records.add(record);
                } catch (RepositoryException e) {
                    throw new RuntimeException("Failed to convert entity to CSV record", e);
                }
            }
            
            List<String> lines = csvParser.formatRecords(records, columnNames);
            fileManager.writeAllLines(lines);
            
            logger.debug("Persisted {} entities to file", records.size());
            
        } catch (CsvException e) {
            throw new RepositoryException("Failed to persist cache to file", e);
        }
    }
    
    /**
     * Checks if a field value matches the expected value.
     * This method should be overridden by subclasses for custom field access.
     * 
     * @param entity the entity to check
     * @param fieldName the field name
     * @param expectedValue the expected value
     * @return true if the field value matches
     */
    protected abstract boolean fieldValueMatches(T entity, String fieldName, Object expectedValue);
    
    /**
     * Checks if all field criteria match for an entity.
     * 
     * @param entity the entity to check
     * @param criteria the field criteria
     * @return true if all criteria match
     */
    private boolean allFieldsMatch(T entity, Map<String, Object> criteria) {
        for (Map.Entry<String, Object> entry : criteria.entrySet()) {
            if (!fieldValueMatches(entity, entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }
}
