package com.heygen.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles JobException and returns an appropriate response entity.
     *
     * @param ex the JobException thrown.
     * @return a structured error response with the specified status code.
     */
    @ExceptionHandler(JobException.class)
    public ResponseEntity<Map<String, String>> handleJobException(JobException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles all other exceptions, returning a generic error response.
     *
     * @param ex the exception thrown.
     * @return a 500 error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(500)
                .body(Map.of("error", "An unexpected error occurred: " + ex.getMessage()));
    }
}

