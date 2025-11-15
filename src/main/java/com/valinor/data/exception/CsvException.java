package com.valinor.data.exception;

/**
 * Exception thrown when CSV file operations fail.
 * This includes file I/O errors, parsing errors, and format issues.
 */
public class CsvException extends RepositoryException {
    
    /**
     * Constructs a new CsvException with the specified detail message.
     * 
     * @param message the detail message explaining the exception
     */
    public CsvException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new CsvException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the exception
     * @param cause the cause of the exception
     */
    public CsvException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new CsvException with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public CsvException(Throwable cause) {
        super(cause);
    }
}