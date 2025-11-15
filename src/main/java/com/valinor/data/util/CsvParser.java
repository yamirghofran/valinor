package com.valinor.data.util;

import com.valinor.data.exception.CsvException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for parsing and formatting CSV data using Apache Commons CSV.
 * Provides robust handling of CSV parsing with proper escaping and validation.
 */
public class CsvParser {
    
    private static final Logger logger = LoggerFactory.getLogger(CsvParser.class);
    
    private final CSVFormat csvFormat;
    
    /**
     * Constructs a new CsvParser with default CSV format.
     */
    public CsvParser() {
        this.csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreHeaderCase(true)
            .setTrim(true)
            .setIgnoreEmptyLines(true)
            .build();
    }
    
    /**
     * Constructs a new CsvParser with custom header names.
     * 
     * @param headers the column headers
     */
    public CsvParser(String[] headers) {
        this.csvFormat = CSVFormat.DEFAULT.builder()
            .setHeader(headers)
            .setSkipHeaderRecord(true)
            .setIgnoreHeaderCase(true)
            .setTrim(true)
            .setIgnoreEmptyLines(true)
            .build();
    }
    
    /**
     * Parses CSV lines into a list of record maps.
     * 
     * @param lines the CSV lines to parse
     * @return list of maps representing CSV records
     * @throws CsvException if parsing fails
     */
    public List<Map<String, String>> parseLines(List<String> lines) throws CsvException {
        if (lines == null || lines.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Map<String, String>> records = new ArrayList<>();
        
        try {
            // Find the header line (first non-empty line)
            String headerLine = findHeaderLine(lines);
            if (headerLine == null) {
                throw new CsvException("No header line found in CSV data");
            }
            
            // Create parser with detected headers
            CSVFormat formatWithHeaders = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build();
            
            // Parse all lines
            String csvContent = String.join("\n", lines);
            
            try (CSVParser parser = CSVParser.parse(csvContent, formatWithHeaders)) {
                for (CSVRecord record : parser) {
                    Map<String, String> recordMap = new LinkedHashMap<>();
                    record.toMap().forEach(recordMap::put);
                    records.add(recordMap);
                }
            }
            
            logger.debug("Parsed {} CSV records from {} lines", records.size(), lines.size());
            return records;
            
        } catch (IOException e) {
            throw new CsvException("Failed to parse CSV data", e);
        }
    }
    
    /**
     * Formats a list of record maps into CSV lines.
     * 
     * @param records the records to format
     * @param headers the column headers
     * @return list of CSV lines including header
     * @throws CsvException if formatting fails
     */
    public List<String> formatRecords(List<Map<String, String>> records, String[] headers) throws CsvException {
        if (records == null) {
            records = new ArrayList<>();
        }
        
        List<String> lines = new ArrayList<>();
        
        try {
            // Create CSV format with headers
            CSVFormat formatWithHeaders = CSVFormat.DEFAULT.builder()
                .setHeader(headers)
                .setSkipHeaderRecord(false)
                .build();
            
            StringWriter writer = new StringWriter();
            
            try (CSVPrinter printer = new CSVPrinter(writer, formatWithHeaders)) {
                // Print header
                printer.printRecord((Object[]) headers);
                lines.add(writer.toString().trim());
                writer.getBuffer().setLength(0); // Clear buffer
                
                // Print records
                for (Map<String, String> record : records) {
                    List<String> values = new ArrayList<>();
                    for (String header : headers) {
                        String value = record.get(header);
                        values.add(value != null ? value : "");
                    }
                    printer.printRecord(values);
                    lines.add(writer.toString().trim());
                    writer.getBuffer().setLength(0); // Clear buffer
                }
            }
            
            logger.debug("Formatted {} records into {} CSV lines", records.size(), lines.size());
            return lines;
            
        } catch (IOException e) {
            throw new CsvException("Failed to format CSV data", e);
        }
    }
    
    /**
     * Parses a single CSV line into a map of values.
     * 
     * @param line the CSV line to parse
     * @param headers the expected headers
     * @return map of header to value
     * @throws CsvException if parsing fails
     */
    public Map<String, String> parseLine(String line, String[] headers) throws CsvException {
        if (line == null || line.trim().isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        try {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader(headers)
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();
            
            try (CSVParser parser = CSVParser.parse(line, format)) {
                CSVRecord record = parser.iterator().next();
                Map<String, String> recordMap = new LinkedHashMap<>();
                record.toMap().forEach(recordMap::put);
                return recordMap;
            }
            
        } catch (IOException e) {
            throw new CsvException("Failed to parse CSV line: " + line, e);
        }
    }
    
    /**
     * Formats a single record map into a CSV line.
     * 
     * @param record the record to format
     * @param headers the column headers
     * @return CSV line
     * @throws CsvException if formatting fails
     */
    public String formatLine(Map<String, String> record, String[] headers) throws CsvException {
        if (record == null) {
            return "";
        }
        
        try {
            StringWriter writer = new StringWriter();
            
            try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    String value = record.get(header);
                    values.add(value != null ? value : "");
                }
                printer.printRecord(values);
            }
            
            String result = writer.toString().trim();
            return result.endsWith(",") ? result.substring(0, result.length() - 1) : result;
            
        } catch (IOException e) {
            throw new CsvException("Failed to format CSV line", e);
        }
    }
    
    /**
     * Finds the header line in a list of CSV lines.
     * 
     * @param lines the lines to search
     * @return the header line, or null if not found
     */
    private String findHeaderLine(List<String> lines) {
        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) {
                return line;
            }
        }
        return null;
    }
    
    /**
     * Validates that a record contains all required headers.
     * 
     * @param record the record to validate
     * @param requiredHeaders the required headers
     * @throws CsvException if validation fails
     */
    public void validateRecord(Map<String, String> record, String[] requiredHeaders) throws CsvException {
        if (record == null) {
            throw new CsvException("Record cannot be null");
        }
        
        for (String header : requiredHeaders) {
            if (!record.containsKey(header)) {
                throw new CsvException("Missing required header: " + header);
            }
        }
    }
}