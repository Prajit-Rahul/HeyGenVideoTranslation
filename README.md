
# HeyGen Video Translation System Documentation

This document provides a comprehensive overview of the HeyGen Video Translation System, including both the backend and frontend implementations, their integration, and testing methodologies.

---

## Table of Contents

1. [Backend Documentation](#backend-documentation)
   - [Overview](#overview)
   - [APIs](#apis)
   - [Components](#components)
   - [Assumptions](#assumptions)
   - [Tests](#tests)
   - [Setup and Running](#setup-and-running)
2. [Frontend and Integration Documentation](#frontend-and-integration-documentation)
   - [Overview](#overview-1)
   - [Frontend Components](#frontend-components)
   - [Tests](#tests-1)
   - [Setup and Running](#setup-and-running-1)
   - [Integration Workflow](#integration-workflow)
   - [Additional Notes](#additional-notes-1)

---

## Backend Documentation

### Overview
The backend is a Spring Boot application that simulates a video translation service. It provides APIs to:
- Start a translation job.
- Poll the backend for job status updates.
- Update the global timeout settings for the translation jobs.

The backend updates job statuses (`pending`, `completed`, `error`) based on elapsed time, simulating real-world processing delays.

---

### APIs

#### Start Job
- **Endpoint**: `/api/start`
- **Method**: `GET`
- **Response**:
  ```json
  {
    "jobId": "1234-5678-91011",
    "status": "pending"
  }
  ```

#### Get Job Status
- **Endpoint**: `/api/status/{jobId}`
- **Method**: `GET`
- **Response**:
  ```json
  {
    "jobId": "1234-5678-91011",
    "status": "completed"
  }
  ```

#### Set Global Timeout
- **Endpoint**: `/api/set-global-timeout`
- **Method**: `POST`
- **Response**:
  ```json
  {
    "message": "Global timeout updated successfully"
  }
  ```

---

### Components

#### Controller
Manages API endpoints and delegates business logic to the `JobService`.

#### Service
Handles job state management and timeout configuration.

#### Exception Handling
Custom exception handling for job-related errors.

#### Model
Represents a job entity with properties like `status`, `startTime`, and `timeout`.

---
### Assumptions

#### Pending
- **Condition**: `elapsedTime ≤ timeout`
- The job is still in progress.

#### Completed
- **Condition**: `timeout < elapsedTime ≤ 2 * timeout`
- The job has finished successfully.

#### Error
- **Condition**: `elapsedTime > 2 * timeout`
- The job has failed due to exceeding acceptable time limits.

These assumptions simulate real-world delays and outcomes in a video translation process.

---

## Tests

Integration tests for the backend are located in `test/java/com/heygen/controller/JobControllerIntegrationTest`. Key tests include:

### 1. **Start Job**
- Verifies job initiation and returns a valid job ID.

### 2. **Get Job Status**
  - **Pending Status**: Verifies job status is "pending" before the timeout.
  - **Completed Status (After Timeout)**: Verifies job status is "completed" after the timeout but before twice the timeout.
  - **Error Status (After Double Timeout)**: Verifies job status is "error" after double the timeout.

### 3. **Set Global Timeout**
- Tests the configuration and update of the global timeout.


---

### Setup and Running

1. Clone the repository and navigate to the backend directory.
   ```bash
   git clone <repository-url>
   cd backend
   ```
2. Build and run the backend.
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

---

## Frontend and Integration Documentation

### Overview

The frontend is a React application that interacts with the backend to:
- Start a translation job.
- Monitor job statuses.
- Update global timeout settings.

### Frontend Components

#### JobStatusComponent
- **Responsibilities**:
  - Displays job status (`Not Started`, `Pending`, `Completed`, `Error`).
  - Manages user interactions for starting jobs and updating timeouts.
- **Methods**:
  - `startJob`: Initiates a job and polls for status updates.
  - `handleStatusUpdate`: Updates the UI based on job status.
  - `updateGlobalTimeout`: Sends a new timeout value to the backend.

#### JobStatusClient
- **Responsibilities**:
  - Handles API interactions with the backend.
  - Provides methods for starting jobs, fetching statuses, and updating timeouts.

---

### Tests

#### JobStatusComponent Tests
Key test cases include:
- Rendering the component.
- Verifying job start functionality.
- Handling status updates (`Pending`, `Completed`, `Error`).
- Updating timeout properly
- Validating global timeout inputs.

#### JobStatusClient Tests
Mocks backend responses to validate API interactions.

---

### Setup and Running

1. Clone the repository and navigate to the frontend directory.
   ```bash
   cd frontend
   ```
2. Install dependencies and start the development server.
   ```bash
   npm install
   npm start
   ```

---

### Integration Workflow

1. **Start a Job**:
   - The frontend sends a `GET` request to `/api/start`.
   - The backend responds with a job ID and an initial status.

2. **Poll for Job Status**:
   - The frontend periodically sends `GET` requests to `/api/status/{jobId}`.
   - Updates the UI based on the status (`Pending`, `Completed`, `Error`).

3. **Update Global Timeout**:
   - The frontend sends a `POST` request to `/api/set-global-timeout` with the timeout value.

---

## Additional Notes

- Ensure both backend and frontend servers are running for seamless integration.
- Modify `JobStatusClient` configuration for custom polling intervals and timeouts.
- Use `App.css` and `index.css` to customize the frontend UI.

---

This documentation provides all the necessary details for setting up, running, and testing the HeyGen Video Translation System.
