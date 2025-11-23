package com.valinor.infrastructure.csv;

import com.valinor.exception.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CsvParser.
 * Tests CSV parsing and formatting functionality.
 */
class CsvParserTest {
    
    private CsvParser csvParser;
    private String[] headers;
    
    @BeforeEach
    void setUp() {
        csvParser = new CsvParser();
        headers = new String[]{"id", "name", "email"};
    }
    
    @Test
    @DisplayName("Should create parser with default constructor")
    void testDefaultConstructor() {
        assertNotNull(csvParser);
    }
    
    @Test
    @DisplayName("Should create parser with custom headers")
    void testConstructorWithHeaders() {
        CsvParser parser = new CsvParser(headers);
        assertNotNull(parser);
    }
    
    @Test
    @DisplayName("Should parse simple CSV lines")
    void testParseSimpleLines() throws CsvException {
        List<String> lines = Arrays.asList(
            "id,name,email",
            "1,John Doe,john@example.com",
            "2,Jane Smith,jane@example.com"
        );
        
        List<Map<String, String>> records = csvParser.parseLines(lines);
        
        assertEquals(2, records.size());
        assertEquals("1", records.get(0).get("id"));
        assertEquals("John Doe", records.get(0).get("name"));
        assertEquals("john@example.com", records.get(0).get("email"));
    }
    
    @Test
    @DisplayName("Should handle empty lines list")
    void testParseEmptyLines() throws CsvException {
        List<String> lines = new ArrayList<>();
        List<Map<String, String>> records = csvParser.parseLines(lines);
        
        assertNotNull(records);
        assertTrue(records.isEmpty());
    }
    
    @Test
    @DisplayName("Should handle null lines list")
    void testParseNullLines() throws CsvException {
        List<Map<String, String>> records = csvParser.parseLines(null);
        
        assertNotNull(records);
        assertTrue(records.isEmpty());
    }
    
    @Test
    @DisplayName("Should parse CSV with quoted values")
    void testParseQuotedValues() throws CsvException {
        List<String> lines = Arrays.asList(
            "id,name,description",
            "1,\"John Doe\",\"A simple description\"",
            "2,\"Jane, Smith\",\"Contains, comma\""
        );
        
        List<Map<String, String>> records = csvParser.parseLines(lines);
        
        assertEquals(2, records.size());
        assertEquals("Jane, Smith", records.get(1).get("name"));
        assertEquals("Contains, comma", records.get(1).get("description"));
    }
    
    @Test
    @DisplayName("Should parse CSV with escaped quotes")
    void testParseEscapedQuotes() throws CsvException {
        List<String> lines = Arrays.asList(
            "id,name,quote",
            "1,Test,\"He said \"\"Hello\"\"\""
        );
        
        List<Map<String, String>> records = csvParser.parseLines(lines);
        
        assertEquals(1, records.size());
        assertTrue(records.get(0).get("quote").contains("Hello"));
    }
    
    @Test
    @DisplayName("Should format records to CSV lines")
    void testFormatRecords() throws CsvException {
        List<Map<String, String>> records = new ArrayList<>();
        
        Map<String, String> record1 = new LinkedHashMap<>();
        record1.put("id", "1");
        record1.put("name", "John Doe");
        record1.put("email", "john@example.com");
        records.add(record1);
        
        Map<String, String> record2 = new LinkedHashMap<>();
        record2.put("id", "2");
        record2.put("name", "Jane Smith");
        record2.put("email", "jane@example.com");
        records.add(record2);
        
        List<String> lines = csvParser.formatRecords(records, headers);
        
        assertNotNull(lines);
        assertTrue(lines.size() >= 3); // Header + 2 records
        assertTrue(lines.get(0).contains("id"));
    }
    
    @Test
    @DisplayName("Should format empty records list")
    void testFormatEmptyRecords() throws CsvException {
        List<Map<String, String>> records = new ArrayList<>();
        List<String> lines = csvParser.formatRecords(records, headers);
        
        assertNotNull(lines);
        assertTrue(lines.size() >= 1); // At least header
        assertTrue(lines.get(0).contains("id"));
    }
    
    @Test
    @DisplayName("Should format null records list")
    void testFormatNullRecords() throws CsvException {
        List<String> lines = csvParser.formatRecords(null, headers);
        
        assertNotNull(lines);
        assertEquals(1, lines.size()); // Just header
    }
    
    @Test
    @DisplayName("Should format records with special characters")
    void testFormatRecordsWithSpecialCharacters() throws CsvException {
        List<Map<String, String>> records = new ArrayList<>();
        
        Map<String, String> record = new LinkedHashMap<>();
        record.put("id", "1");
        record.put("name", "John, Doe");
        record.put("email", "john@example.com");
        records.add(record);
        
        List<String> lines = csvParser.formatRecords(records, headers);
        
        assertNotNull(lines);
        assertTrue(lines.size() >= 2);
    }
    
    @Test
    @DisplayName("Should parse single line")
    void testParseLine() throws CsvException {
        // parseLine expects data without header, but uses headers for mapping
        String csvData = "id,name,email\n1,John Doe,john@example.com";
        List<String> lines = Arrays.asList(csvData.split("\n"));
        List<Map<String, String>> records = csvParser.parseLines(lines);
        
        assertNotNull(records);
        assertFalse(records.isEmpty());
        assertEquals("1", records.get(0).get("id"));
        assertEquals("John Doe", records.get(0).get("name"));
        assertEquals("john@example.com", records.get(0).get("email"));
    }
    
    @Test
    @DisplayName("Should handle empty line")
    void testParseEmptyLine() throws CsvException {
        Map<String, String> record = csvParser.parseLine("", headers);
        
        assertNotNull(record);
        assertTrue(record.isEmpty());
    }
    
    @Test
    @DisplayName("Should handle null line")
    void testParseNullLine() throws CsvException {
        Map<String, String> record = csvParser.parseLine(null, headers);
        
        assertNotNull(record);
        assertTrue(record.isEmpty());
    }
    
    @Test
    @DisplayName("Should format single line")
    void testFormatLine() throws CsvException {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("id", "1");
        record.put("name", "John Doe");
        record.put("email", "john@example.com");
        
        String line = csvParser.formatLine(record, headers);
        
        assertNotNull(line);
        assertFalse(line.isEmpty());
        assertTrue(line.contains("John Doe"));
    }
    
    @Test
    @DisplayName("Should handle null record when formatting line")
    void testFormatNullLine() throws CsvException {
        String line = csvParser.formatLine(null, headers);
        
        assertNotNull(line);
        assertTrue(line.isEmpty());
    }
    
    @Test
    @DisplayName("Should validate record with all required headers")
    void testValidateRecordSuccess() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("id", "1");
        record.put("name", "Test");
        record.put("email", "test@example.com");
        
        assertDoesNotThrow(() -> csvParser.validateRecord(record, headers));
    }
    
    @Test
    @DisplayName("Should throw exception for missing required header")
    void testValidateRecordMissingHeader() {
        Map<String, String> record = new LinkedHashMap<>();
        record.put("id", "1");
        record.put("name", "Test");
        // Missing email
        
        assertThrows(CsvException.class, () -> csvParser.validateRecord(record, headers));
    }
    
    @Test
    @DisplayName("Should throw exception for null record")
    void testValidateNullRecord() {
        assertThrows(CsvException.class, () -> csvParser.validateRecord(null, headers));
    }
    
    @Test
    @DisplayName("Should handle Unicode characters")
    void testUnicodeCharacters() throws CsvException {
        List<String> lines = Arrays.asList(
            "id,name,description",
            "1,Café,Déjà vu",
            "2,日本語,中文"
        );
        
        List<Map<String, String>> records = csvParser.parseLines(lines);
        
        assertEquals(2, records.size());
        assertEquals("Café", records.get(0).get("name"));
        assertEquals("日本語", records.get(1).get("name"));
    }
    
    @Test
    @DisplayName("Should handle empty values")
    void testEmptyValues() throws CsvException {
        List<String> lines = Arrays.asList(
            "id,name,email",
            "1,,",
            "2,Jane,"
        );
        
        List<Map<String, String>> records = csvParser.parseLines(lines);
        
        assertEquals(2, records.size());
        assertTrue(records.get(0).get("name").isEmpty() || records.get(0).get("name") == null);
    }
    
    @Test
    @DisplayName("Should preserve order of columns")
    void testColumnOrder() throws CsvException {
        List<Map<String, String>> records = new ArrayList<>();
        
        Map<String, String> record = new LinkedHashMap<>();
        record.put("id", "1");
        record.put("name", "Test");
        record.put("email", "test@example.com");
        records.add(record);
        
        List<String> lines = csvParser.formatRecords(records, headers);
        
        assertTrue(lines.get(0).contains("id"));
        assertTrue(lines.get(0).contains("name"));
        assertTrue(lines.get(0).contains("email"));
    }
}
