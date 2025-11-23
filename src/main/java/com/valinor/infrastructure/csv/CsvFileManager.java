package com.valinor.infrastructure.csv;

import com.valinor.exception.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Utility class for managing CSV file operations with proper locking,
 * backup, and atomic write capabilities.
 */
public class CsvFileManager {
    
    private static final Logger logger = LoggerFactory.getLogger(CsvFileManager.class);
    private static final String BACKUP_SUFFIX = ".backup";
    private static final String TEMP_SUFFIX = ".tmp";
    
    private final Path filePath;
    private final Path backupPath;
    private final Path tempPath;
    private final ReentrantReadWriteLock fileLock = new ReentrantReadWriteLock();
    
    /**
     * Constructs a new CsvFileManager for the specified file path.
     * 
     * @param filePath the path to the CSV file
     * @throws CsvException if the file path is invalid
     */
    public CsvFileManager(String filePath) throws CsvException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new CsvException("File path cannot be null or empty");
        }
        
        this.filePath = Paths.get(filePath).toAbsolutePath().normalize();
        this.backupPath = Paths.get(filePath + BACKUP_SUFFIX);
        this.tempPath = Paths.get(filePath + TEMP_SUFFIX);
        
        // Ensure parent directory exists
        try {
            Files.createDirectories(this.filePath.getParent());
        } catch (IOException e) {
            throw new CsvException("Failed to create parent directories for: " + filePath, e);
        }
    }
    
    /**
     * Reads all lines from the CSV file with shared lock.
     * 
     * @return list of lines from the file
     * @throws CsvException if reading fails
     */
    public List<String> readAllLines() throws CsvException {
        fileLock.readLock().lock();
        try {
            logger.debug("Reading from file: {}", filePath);
            
            if (!Files.exists(filePath)) {
                logger.info("File does not exist, returning empty list: {}", filePath);
                return new ArrayList<>();
            }
            
            return Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CsvException("Failed to read file: " + filePath, e);
        } finally {
            fileLock.readLock().unlock();
        }
    }
    
    /**
     * Writes lines to the CSV file atomically with exclusive lock.
     * This method creates a backup, writes to a temp file, then atomically moves it.
     * 
     * @param lines the lines to write
     * @throws CsvException if writing fails
     */
    public void writeAllLines(List<String> lines) throws CsvException {
        fileLock.writeLock().lock();
        try {
            logger.debug("Writing {} lines to file: {}", lines.size(), filePath);
            
            // Create backup if original file exists
            if (Files.exists(filePath)) {
                createBackup();
            }
            
            // Write to temporary file first
            writeToTempFile(lines);
            
            // Atomically move temp file to target location
            atomicMove();
            
            logger.debug("Successfully wrote {} lines to file: {}", lines.size(), filePath);
            
        } catch (IOException e) {
            // Try to restore from backup if write failed
            tryRestoreFromBackup();
            throw new CsvException("Failed to write file: " + filePath, e);
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    
    /**
     * Creates a backup of the current file.
     * 
     * @throws IOException if backup creation fails
     */
    private void createBackup() throws IOException {
        if (Files.exists(filePath)) {
            Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            logger.debug("Created backup: {}", backupPath);
        }
    }
    
    /**
     * Writes lines to a temporary file.
     * 
     * @param lines the lines to write
     * @throws IOException if writing fails
     */
    private void writeToTempFile(List<String> lines) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        logger.debug("Wrote to temporary file: {}", tempPath);
    }
    
    /**
     * Atomically moves the temporary file to the target location.
     * 
     * @throws IOException if atomic move fails
     */
    private void atomicMove() throws IOException {
        Files.move(tempPath, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        logger.debug("Atomically moved {} to {}", tempPath, filePath);
    }
    
    /**
     * Attempts to restore from backup if a write operation failed.
     */
    private void tryRestoreFromBackup() {
        try {
            if (Files.exists(backupPath)) {
                Files.move(backupPath, filePath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Restored from backup: {}", backupPath);
            }
        } catch (IOException e) {
            logger.error("Failed to restore from backup: {}", backupPath, e);
        }
    }
    
    /**
     * Checks if the CSV file exists.
     * 
     * @return true if the file exists, false otherwise
     */
    public boolean exists() {
        return Files.exists(filePath);
    }
    
    /**
     * Creates an empty CSV file with the given header if it doesn't exist.
     * 
     * @param header the header line to write
     * @throws CsvException if creation fails
     */
    public void createIfNotExists(String header) throws CsvException {
        if (!exists()) {
            writeAllLines(List.of(header));
            logger.info("Created new CSV file with header: {}", filePath);
        }
    }
    
    /**
     * Deletes the CSV file and its backup.
     * 
     * @throws CsvException if deletion fails
     */
    public void delete() throws CsvException {
        fileLock.writeLock().lock();
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("Deleted file: {}", filePath);
            }
            if (Files.exists(backupPath)) {
                Files.delete(backupPath);
                logger.debug("Deleted backup file: {}", backupPath);
            }
        } catch (IOException e) {
            throw new CsvException("Failed to delete file: " + filePath, e);
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    
    /**
     * Gets the file path.
     * 
     * @return the file path
     */
    public Path getFilePath() {
        return filePath;
    }
    
    /**
     * Gets the backup file path.
     * 
     * @return the backup file path
     */
    public Path getBackupPath() {
        return backupPath;
    }
}