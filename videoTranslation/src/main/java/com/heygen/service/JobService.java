package com.heygen.service;

import com.heygen.exception.JobException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.heygen.model.Job;

@Service // Marks this class as a Spring service component.
public class JobService {
    private final Map<String, Job> jobStore = new HashMap<>(); // Stores jobs with their unique IDs.
    private long defaultTimeout = 15000; // Default timeout for jobs (15 seconds).

    /**
     * Starts a new job by generating a unique job ID and creating a Job instance.
     *
     * @return the unique ID of the newly created job.
     */
    public String startJob() {
        String jobId = UUID.randomUUID().toString(); // Generates a unique ID for the job.
        Job job = new Job("pending", System.currentTimeMillis(), defaultTimeout); // Creates a new job instance.
        jobStore.put(jobId, job); // Stores the job in the jobStore map.
        return jobId; // Returns the job ID.
    }

    /**
     * Retrieves the status of a job based on its ID and updates the status if necessary.
     *
     * @param jobId the unique ID of the job.
     * @return a map containing the job ID and its current status.
     * @throws JobException if the job ID is invalid.
     */
    public Map<String, String> getJobStatus(String jobId) {
        if (!jobStore.containsKey(jobId)) {
            throw new JobException("Invalid jobId: " + jobId); // Throws an exception if the job ID is not found.
        }

        Job job = jobStore.get(jobId);
        long elapsedTime = System.currentTimeMillis() - job.getStartTime(); // Calculates elapsed time since the job started.

        // Updates the job's status based on elapsed time.
        if ("pending".equals(job.getStatus())) {
            if (elapsedTime > job.getTimeout() && elapsedTime <= 2 * job.getTimeout()) {
                job.setStatus("completed");
            } else if (elapsedTime > 2 * job.getTimeout()) {
                job.setStatus("error");
            }
        }

        return Map.of(
                "status", job.getStatus(), // Returns the current status.
                "jobId", jobId // Returns the job ID.
        );
    }

    /**
     * Sets a custom timeout for a specific job.
     *
     * @param jobId  the unique ID of the job.
     * @param timeout the new timeout value in milliseconds.
     * @throws JobException if the job ID is invalid or the timeout is less than or equal to zero.
     */
    public void setTimeout(String jobId, long timeout) {
        if (!jobStore.containsKey(jobId)) {
            throw new JobException("Invalid jobId: " + jobId); // Throws an exception if the job ID is not found.
        }
        if (timeout <= 0) {
            throw new JobException("Timeout must be greater than zero."); // Throws an exception for invalid timeout values.
        }
        jobStore.get(jobId).setTimeout(timeout); // Updates the job's timeout.
    }

    /**
     * Sets the global timeout value for all newly created jobs.
     *
     * @param timeout the new global timeout value in milliseconds.
     * @throws JobException if the timeout is less than or equal to zero.
     */
    public void setGlobalTimeout(long timeout) {
        if (timeout <= 0) {
            throw new JobException("Timeout must be greater than zero."); // Throws an exception for invalid timeout values.
        }
        this.defaultTimeout = timeout; // Updates the global timeout.
    }

    /**
     * Retrieves the current global timeout value.
     *
     * @return the global timeout value in milliseconds.
     */
    public long getGlobalTimeout() {
        return defaultTimeout;
    }
}
