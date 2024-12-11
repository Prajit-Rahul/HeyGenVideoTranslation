package com.heygen.controller;

import com.heygen.HeyGenBackend;
import com.heygen.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = HeyGenBackend.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobControllerIntegrationTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JobController jobController; // Injects the JobController to test its endpoints.

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JobService jobService; // Injects the JobService to simulate job-related operations.

    private MockMvc mockMvc; // MockMvc for simulating HTTP requests.

    /**
     * Sets up the MockMvc object before each test.
     */
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build(); // Initializes MockMvc with the JobController.
    }

    /**
     * Tests the /api/start endpoint to verify that a new job is started successfully.
     *
     * @throws Exception if the mock request fails.
     */
    @Test
    public void testStartJob() throws Exception {
        mockMvc.perform(get("/api/start") // Simulates a GET request to /api/start.
                        .contentType(MediaType.APPLICATION_JSON)) // Specifies the request content type as JSON.
                .andExpect(status().isOk()) // Verifies that the response status is 200 OK.
                .andExpect(content().string(containsString("jobId"))) // Asserts that the response contains "jobId".
                .andExpect(content().string(containsString("pending"))); // Asserts that the response contains "pending" status.

        System.out.println("Job started successfully."); // Logs a success message.
    }

    /**
     * Tests the /api/status/{jobId} endpoint to verify job status retrieval.
     *
     * @throws Exception if the mock request fails.
     */
    @Test
    public void testGetJobStatus() throws Exception {
        // First, simulate job start and get jobId
        String jobId = jobService.startJob(); // Starts a job using the JobService and retrieves its ID.
        System.out.println("Job ID: " + jobId); // Logs the job ID for reference.

        mockMvc.perform(get("/api/status/" + jobId) // Simulates a GET request to /api/status/{jobId}.
                        .contentType(MediaType.APPLICATION_JSON)) // Specifies the request content type as JSON.
                .andExpect(status().isOk()) // Verifies that the response status is 200 OK.
                .andExpect(content().string(containsString("jobId"))) // Asserts that the response contains "jobId".
                .andExpect(content().string(containsString("pending"))); // Asserts that the response contains "pending" status.

        System.out.println("Job status fetched: pending"); // Logs the fetched job status.
    }

    /**
     * Tests the /api/set-global-timeout endpoint to verify global timeout update functionality.
     *
     * @throws Exception if the mock request fails.
     */
    @Test
    public void testSetGlobalTimeout() throws Exception {
        long globalTimeout = 10000; // Define a new global timeout value (10 seconds).

        mockMvc.perform(post("/api/set-global-timeout") // Simulates a POST request to /api/set-global-timeout.
                        .param("timeout", String.valueOf(globalTimeout)) // Adds the "timeout" parameter to the request.
                        .contentType(MediaType.APPLICATION_JSON)) // Specifies the request content type as JSON.
                .andExpect(status().isOk()) // Verifies that the response status is 200 OK.
                .andExpect(content().string(containsString("Global timeout updated successfully"))); // Asserts that the response contains the success message.

        System.out.println("Global timeout updated successfully"); // Logs a success message.
    }

    /**
     * Tests the /api/status/{jobId} endpoint to verify job status transitions to "completed"
     * when the elapsed time is between timeout and twice the timeout.
     *
     * @throws Exception if the mock request fails.
     */
    @Test
    public void testJobCompletedStatus() throws Exception {
        // First, simulate job start and get jobId
        String jobId = jobService.startJob(); // Starts a job using the JobService and retrieves its ID.
        System.out.println("Job ID: " + jobId); // Logs the job ID for reference.

        // Wait for a duration between timeout and 2x timeout to ensure the job enters the "completed" state.
        long waitTime = jobService.getGlobalTimeout() + 5000; // Slightly more than the timeout but less than 2x timeout.
        Thread.sleep(waitTime); // Introduces a delay to simulate job processing time.

        // Fetch the job status after the wait.
        mockMvc.perform(get("/api/status/" + jobId) // Simulates a GET request to /api/status/{jobId}.
                        .contentType(MediaType.APPLICATION_JSON)) // Specifies the request content type as JSON.
                .andExpect(status().isOk()) // Verifies that the response status is 200 OK.
                .andExpect(content().string(containsString("jobId"))) // Asserts that the response contains "jobId".
                .andExpect(content().string(containsString("completed"))); // Asserts that the response contains "completed" status.

        System.out.println("Job status transitioned to completed as expected."); // Logs the success of the test.
    }

    /**
     * Tests the /api/status/{jobId} endpoint to verify job status transitions to "error" after the timeout period.
     *
     * @throws Exception if the mock request fails.
     */
    @Test
    public void testJobErrorStatus() throws Exception {
        // First, simulate job start and get jobId
        String jobId = jobService.startJob(); // Starts a job using the JobService and retrieves its ID.
        System.out.println("Job ID: " + jobId); // Logs the job ID for reference.

        // Wait for more than double the default timeout to ensure the job enters the "error" state.
        long waitTime = jobService.getGlobalTimeout() * 2 + 1000; // Slightly longer than 2x timeout.
        Thread.sleep(waitTime); // Introduces a delay to simulate job processing time.

        // Fetch the job status after the wait.
        mockMvc.perform(get("/api/status/" + jobId) // Simulates a GET request to /api/status/{jobId}.
                        .contentType(MediaType.APPLICATION_JSON)) // Specifies the request content type as JSON.
                .andExpect(status().isOk()) // Verifies that the response status is 200 OK.
                .andExpect(content().string(containsString("jobId"))) // Asserts that the response contains "jobId".
                .andExpect(content().string(containsString("error"))); // Asserts that the response contains "error" status.

        System.out.println("Job status transitioned to error as expected."); // Logs the success of the test.
    }
}
