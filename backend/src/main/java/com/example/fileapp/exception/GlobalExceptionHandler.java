package com.example.fileapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Error Code	HTTP Status	Description
 * - 4001	400 Bad Request	Duplicate file (same content already exists)
 * - 4002	400 Bad Request	Invalid file type (e.g., unsupported format)
 * - 4003	400 Bad Request	Missing file in the request
 * - 4041	404 Not Found	File not found (wrong UUID)
 * - 5000	500 Internal Server Error	General server error (unexpected issue)
 * - 5001	500 Internal Server Error	File storage issue (cannot save or retrieve file)
 * - 5002	500 Internal Server Error	File upload failed (I/O error)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        int errorCode = ex.getMessage().contains("already exists") ? 4001 : 4003;
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorCode, ex.getMessage());
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFoundException(FileNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, 4041, ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 5002, "File upload failed.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 5000, "An unexpected error occurred.");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, int errorCode, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode,
                message,
                LocalDateTime.now(),
                UUID.randomUUID().toString()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    public record ErrorResponse(int errorCode, String message, LocalDateTime timestamp, String traceId) {}
}
