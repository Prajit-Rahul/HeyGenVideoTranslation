package com.heygen.controller;

import com.heygen.exception.JobException;
import com.heygen.service.JobService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*") // Allows cross-origin requests from any origin.
@RequestMapping("/api") // Base path for all endpoints in this controller.
public class JobController {

    private final JobService jobService;

    /**
     * Constructor for JobController.
     *
     * @param jobService the service layer handling job-related logic.
     */
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Endpoint to start a new job.
     *
     * @return a JSON response containing the job ID and its initial status ("pending").
     * @throws JobException if there is an error while starting the job.
     */
    @GetMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> startJob() {
        try {
            String jobId = jobService.startJob(); // Calls the service layer to start a new job.

            // JSON response with the job ID and initial status.
            String jsonResponse = "{"
                    + "\"jobId\": \"" + jobId + "\","
                    + "\"status\": \"pending\""
                    + "}";

            return ResponseEntity.ok(jsonResponse); // Returns a 200 OK response with the JSON payload.
        } catch (Exception e) {
            throw new JobException("Failed to start the job: " + e.getMessage()); // Throws a custom exception in case of errors.
        }
    }

    /**
     * Endpoint to fetch the status of a job.
     *
     * @param jobId the unique identifier of the job.
     * @return a JSON response containing the job ID and its current status.
     * @throws JobException if there is an error while fetching the job status.
     */
    @GetMapping("status/{jobId}")
    public ResponseEntity<String> getJobStatus(@PathVariable("jobId") String jobId) {
        try {
            Map<String, String> status = jobService.getJobStatus(jobId); // Calls the service layer to retrieve job status.

            // JSON response with the job ID and current status.
            String jsonResponse = "{"
                    + "\"jobId\": \"" + jobId + "\","
                    + "\"status\": \"" + status.get("status") + "\""
                    + "}";

            return ResponseEntity.ok(jsonResponse); // Returns a 200 OK response with the JSON payload.
        } catch (Exception e) {
            throw new JobException("Error fetching job status: " + e.getMessage(), 500); // Throws a custom exception in case of errors.
        }
    }

    /**
     * Endpoint to set the global timeout for all jobs.
     *
     * @param timeout the new timeout value in milliseconds.
     * @return a JSON response confirming the update.
     * @throws JobException if there is an error while updating the timeout.
     */
    @PostMapping(value = "/set-global-timeout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setGlobalTimeout(@RequestParam("timeout") long timeout) {
        try {
            jobService.setGlobalTimeout(timeout); // Calls the service layer to update the global timeout.
            String jsonResponse = "{\"message\": \"Global timeout updated successfully\"}";

            return ResponseEntity.ok(jsonResponse); // Returns a 200 OK response with a success message.
        } catch (Exception e) {
            throw new JobException("Error setting global timeout: " + e.getMessage(), 500); // Throws a custom exception in case of errors.
        }
    }
}
