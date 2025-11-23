package com.valinor.infrastructure.csv;

import com.valinor.exception.CsvException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CsvFileManager.
 * Tests file operations, locking, backup, and atomic writes.
 */
class CsvFileManagerTest {
    
    @TempDir
    Path tempDir;
    
    private CsvFileManager fileManager;
    private Path testFilePath;
    
    @BeforeEach
    void setUp() throws CsvException {
        testFilePath = tempDir.resolve("test.csv");
        fileManager = new CsvFileManager(testFilePath.toString());
    }
    
    @Test
    @DisplayName("Should create file manager with valid path")
    void testConstructorWithValidPath() {
        assertNotNull(fileManager);
        assertEquals(testFilePath, fileManager.getFilePath());
    }
    
    @Test
    @DisplayName("Should throw exception for null file path")
    void testConstructorWithNullPath() {
        assertThrows(CsvException.class, () -> new CsvFileManager(null));
    }
    
    @Test
    @DisplayName("Should throw exception for empty file path")
    void testConstructorWithEmptyPath() {
        assertThrows(CsvException.class, () -> new CsvFileManager(""));
    }
    
    @Test
    @DisplayName("Should create parent directories if they don't exist")
    void testCreateParentDirectories() throws CsvException {
        Path nestedPath = tempDir.resolve("nested/dir/test.csv");
        CsvFileManager manager = new CsvFileManager(nestedPath.toString());
        
        assertNotNull(manager);
        assertTrue(Files.exists(nestedPath.getParent()));
    }
    
    @Test
    @DisplayName("Should return false when file doesn't exist")
    void testExistsWhenFileDoesNotExist() {
        assertFalse(fileManager.exists());
    }
    
    @Test
    @DisplayName("Should return true when file exists")
    void testExistsWhenFileExists() throws CsvException {
        fileManager.createIfNotExists("header");
        assertTrue(fileManager.exists());
    }
    
    @Test
    @DisplayName("Should create file with header if it doesn't exist")
    void testCreateIfNotExists() throws CsvException {
        String header = "id,name,email";
        fileManager.createIfNotExists(header);
        
        assertTrue(fileManager.exists());
        List<String> lines = fileManager.readAllLines();
        assertEquals(1, lines.size());
        assertEquals(header, lines.get(0));
    }
    
    @Test
    @DisplayName("Should not overwrite existing file when createIfNotExists is called")
    void testCreateIfNotExistsDoesNotOverwrite() throws CsvException {
        // Create file with initial content
        List<String> initialContent = Arrays.asList("id,name", "1,Test");
        fileManager.writeAllLines(initialContent);
        
        // Try to create with different header
        fileManager.createIfNotExists("different,header");
        
        // Verify original content is preserved
        List<String> lines = fileManager.readAllLines();
        assertEquals(initialContent, lines);
    }
    
    @Test
    @DisplayName("Should read empty list when file doesn't exist")
    void testReadAllLinesWhenFileDoesNotExist() throws CsvException {
        List<String> lines = fileManager.readAllLines();
        assertNotNull(lines);
        assertTrue(lines.isEmpty());
    }
    
    @Test
    @DisplayName("Should write and read lines correctly")
    void testWriteAndReadLines() throws CsvException {
        List<String> linesToWrite = Arrays.asList(
            "id,name,email",
            "1,John Doe,john@example.com",
            "2,Jane Smith,jane@example.com"
        );
        
        fileManager.writeAllLines(linesToWrite);
        List<String> linesRead = fileManager.readAllLines();
        
        assertEquals(linesToWrite, linesRead);
    }
    
    @Test
    @DisplayName("Should handle empty lines list")
    void testWriteEmptyLines() throws CsvException {
        List<String> emptyLines = Arrays.asList();
        fileManager.writeAllLines(emptyLines);
        
        // File should be created but empty
        assertTrue(fileManager.exists());
        List<String> lines = fileManager.readAllLines();
        assertTrue(lines.isEmpty());
    }
    
    @Test
    @DisplayName("Should create backup when writing to existing file")
    void testBackupCreation() throws CsvException {
        // Write initial content
        List<String> initialLines = Arrays.asList("id,name", "1,Test");
        fileManager.writeAllLines(initialLines);
        
        // Write new content
        List<String> newLines = Arrays.asList("id,name", "2,Updated");
        fileManager.writeAllLines(newLines);
        
        // Backup should exist
        assertTrue(Files.exists(fileManager.getBackupPath()));
    }
    
    @Test
    @DisplayName("Should handle special characters in content")
    void testSpecialCharacters() throws CsvException {
        List<String> lines = Arrays.asList(
            "id,name,description",
            "1,Test,\"Contains, comma\"",
            "2,Test2,\"Contains \"\"quotes\"\"\""
        );
        
        fileManager.writeAllLines(lines);
        List<String> readLines = fileManager.readAllLines();
        
        assertEquals(lines, readLines);
    }
    
    @Test
    @DisplayName("Should delete file and backup")
    void testDelete() throws CsvException {
        // Create file and backup
        fileManager.writeAllLines(Arrays.asList("header", "data"));
        assertTrue(fileManager.exists());
        // Backup is created during write
        
        // Delete
        fileManager.delete();
        
        assertFalse(fileManager.exists());
        assertFalse(Files.exists(fileManager.getBackupPath()));
    }
    
    @Test
    @DisplayName("Should handle delete when file doesn't exist")
    void testDeleteNonExistentFile() {
        assertDoesNotThrow(() -> fileManager.delete());
    }
    
    @Test
    @DisplayName("Should handle concurrent reads")
    void testConcurrentReads() throws CsvException, InterruptedException {
        List<String> lines = Arrays.asList("id,name", "1,Test");
        fileManager.writeAllLines(lines);
        
        Thread[] threads = new Thread[5];
        boolean[] success = new boolean[5];
        
        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    List<String> readLines = fileManager.readAllLines();
                    success[index] = readLines.equals(lines);
                } catch (CsvException e) {
                    success[index] = false;
                }
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // All reads should succeed
        for (boolean s : success) {
            assertTrue(s);
        }
    }
    
    @Test
    @DisplayName("Should get correct file path")
    void testGetFilePath() {
        assertEquals(testFilePath, fileManager.getFilePath());
    }
    
    @Test
    @DisplayName("Should get correct backup path")
    void testGetBackupPath() {
        Path expectedBackupPath = Path.of(testFilePath.toString() + ".backup");
        assertEquals(expectedBackupPath, fileManager.getBackupPath());
    }
    
    @Test
    @DisplayName("Should handle Unicode content")
    void testUnicodeContent() throws CsvException {
        List<String> lines = Arrays.asList(
            "id,name,description",
            "1,Café,Déjà vu",
            "2,日本語,中文",
            "3,Emoji,😀🎉"
        );
        
        fileManager.writeAllLines(lines);
        List<String> readLines = fileManager.readAllLines();
        
        assertEquals(lines, readLines);
    }
    
    @Test
    @DisplayName("Should handle large files")
    void testLargeFile() throws CsvException {
        List<String> lines = Arrays.asList("id,name,email");
        
        // Add 1000 lines
        for (int i = 1; i <= 1000; i++) {
            lines = new java.util.ArrayList<>(lines);
            lines.add(String.format("%d,Name%d,email%d@example.com", i, i, i));
        }
        
        fileManager.writeAllLines(lines);
        List<String> readLines = fileManager.readAllLines();
        
        assertEquals(lines.size(), readLines.size());
        assertEquals(lines, readLines);
    }
}
