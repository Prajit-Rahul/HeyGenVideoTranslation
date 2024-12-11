package com.heygen.exception;

/**
 * Custom exception to handle job-related errors in the application.
 */
public class JobException extends RuntimeException {

    private final int statusCode;

    /**
     * Constructs a JobException with the specified detail message.
     *
     * @param message the detail message.
     */
    public JobException(String message) {
        super(message);
        this.statusCode = 400; // Default status code for client errors
    }

    /**
     * Constructs a JobException with the specified detail message and status code.
     *
     * @param message    the detail message.
     * @param statusCode the HTTP status code for the exception.
     */
    public JobException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Retrieves the associated HTTP status code for this exception.
     *
     * @return the HTTP status code.
     */
    public int getStatusCode() {
        return statusCode;
    }
}

