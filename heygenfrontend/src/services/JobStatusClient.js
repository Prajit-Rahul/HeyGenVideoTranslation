import axios from 'axios';
class JobStatusClient {
  /**
   * Initializes the JobStatusClient with configuration options.
   * 
   * @param {string} baseUrl - The base URL of the backend API.
   * @param {number} timeout - Timeout duration for jobs (default: 15000 ms).
   * @param {number} pollingInterval - Interval between status polling (default: 2000 ms).
   * @param {number} maxRetries - Maximum number of polling retries (default: 10).
   */
  constructor(baseUrl, timeout = 15000, pollingInterval = 2000, maxRetries = 10) {
    this.baseUrl = baseUrl;
    this.timeout = timeout;
    this.pollingInterval = pollingInterval;
    this.maxRetries = maxRetries;
    this.retries = 0; // Tracks the number of polling retries.
    this.jobId = null; // Stores the current job ID.
    this.isPolling = false; // Indicates if polling is active.
    this.pollingTimer = null; // Reference to the polling timer.
  }

  /**
   * Starts a new job on the backend.
   * 
   * @returns {string} The ID of the newly created job.
   * @throws {Error} If the API call fails.
   */
  async startJob() {
    try {
      const response = await axios.get(`${this.baseUrl}/api/start`);
      this.jobId = response.data.jobId; // Store the job ID for future operations.
      return this.jobId;
    } catch (error) {
      console.error("Error starting the job:", error);
      throw error;
    }
  }

  /**
   * Fetches the current status of the job from the backend.
   * 
   * @returns {string} The status of the job ('pending', 'completed', or 'error').
   * @throws {Error} If the job ID is not set or the API call fails.
   */
  async getJobStatus() {
    if (!this.jobId) {
      throw new Error("Job ID is not set.");
    }
    try {
      const response = await axios.get(`${this.baseUrl}/api/status/${this.jobId}`);
      return response.data.status;
    } catch (error) {
      console.error("Error fetching job status:", error);
      throw error;
    }
  }

  /**
   * Sets a new global timeout value for jobs on the backend.
   * 
   * @param {number} timeout - The new timeout value in milliseconds.
   * @returns {string} A success message from the backend.
   * @throws {Error} If the API call fails.
   */
  async setGlobalTimeout(timeout) {
    try {
      const response = await axios.post(`${this.baseUrl}/api/set-global-timeout`, null, {
        params: { timeout },
      });
      return response.data.message;
    } catch (error) {
      console.error("Error setting global timeout:", error);
      throw error;
    }
  }

  /**
   * Polls the backend for the job status at regular intervals until the job is completed, fails, or retries are exhausted.
   * 
   * @param {function} callback - A callback function that handles the status updates.
   */
  async pollJobStatus(callback) {
    if (this.isPolling) return; // Prevent multiple polling loops.

    this.isPolling = true;
    this.retries = 0;

    // Function to perform a single poll operation.
    const poll = async () => {
      try {
        const status = await this.getJobStatus();
        if (status === 'completed' || status === 'error') {
          callback(status); // Invoke the callback with the final status.
          this.stopPolling();
        } else if (this.retries >= this.maxRetries) {
          callback('pending'); // Indicate polling exhaustion.
          this.stopPolling();
        } else {
          this.retries++; // Increment retry count.
        }
      } catch (error) {
        console.error("Polling error:", error);
        callback('error'); // Notify error status.
        this.stopPolling();
      }
    };

    // Start polling at the specified interval.
    this.pollingTimer = setInterval(poll, this.pollingInterval);
  }

  /**
   * Stops the polling process.
   */
  stopPolling() {
    if (this.pollingTimer) {
      clearInterval(this.pollingTimer);
      this.isPolling = false;
    }
  }
}

export default JobStatusClient;
