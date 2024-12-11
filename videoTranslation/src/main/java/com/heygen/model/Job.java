package com.heygen.model;

/**
 * Represents a Job entity with attributes such as status, start time, and timeout.
 */
public class Job {
    private String status; // Current status of the job (e.g., "pending", "completed", "error").
    private final long startTime; // The timestamp (in milliseconds) when the job started.
    private long timeout; // The timeout duration for the job (in milliseconds).

    /**
     * Constructs a Job instance with the given status, start time, and timeout.
     *
     * @param status    the initial status of the job.
     * @param startTime the timestamp when the job starts (in milliseconds).
     * @param timeout   the timeout duration for the job (in milliseconds).
     */
    public Job(String status, long startTime, long timeout) {
        this.status = status;
        this.startTime = startTime;
        this.timeout = timeout;
    }

    /**
     * Gets the current status of the job.
     *
     * @return the status of the job.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the job.
     *
     * @param status the new status to be assigned to the job.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the start time of the job.
     *
     * @return the start time of the job in milliseconds.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the timeout duration of the job.
     *
     * @return the timeout duration in milliseconds.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout duration for the job.
     *
     * @param timeout the new timeout duration in milliseconds.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
