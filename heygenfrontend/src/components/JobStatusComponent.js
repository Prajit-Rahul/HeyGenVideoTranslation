import React, { useState } from 'react';
import JobStatusClient from '../services/JobStatusClient.js';
import '../App.css';

const JobStatusComponent = () => {
  // State variables to manage job status, job ID, and global timeout input.
  const [status, setStatus] = useState('Not Started');
  const [jobId, setJobId] = useState(null);
  const [newTimeout, setNewTimeout] = useState("");

  // Instantiate the JobStatusClient with the backend URL.
  const client = new JobStatusClient('http://localhost:8080');

  // Function to start a job and initiate status polling.
  const startJob = async () => {
    try {
      const id = await client.startJob(); // Starts the job and retrieves the job ID.
      setJobId(id); // Save the job ID in state.
      setStatus('Pending'); // Update the status to "Pending".
      client.pollJobStatus(handleStatusUpdate); // Begin polling for job status updates.
    } catch (error) {
      setStatus('Error'); // Set status to "Error" if the job fails to start.
    }
  };

  // Callback function to handle status updates from polling.
  const handleStatusUpdate = (currentStatus) => {
    if (currentStatus === 'completed') {
      setStatus('Completed');
    } else if (currentStatus === 'error') {
      setStatus('Error');
    } else if (currentStatus === 'pending') {
      setStatus('Pending');
    }
  };

  // Function to update the global timeout value on the backend.
  const updateGlobalTimeout = async () => {
    if (!newTimeout || isNaN(newTimeout) || newTimeout <= 0) {
      alert("Please enter a valid timeout value greater than 0.");
      return;
    }
    try {
      await client.setGlobalTimeout(Number(newTimeout)); // Send the new timeout value to the backend.
      alert(`Global timeout updated to ${newTimeout} ms.`); 
      setNewTimeout(""); // Clear the input field.
    } catch (error) {
      alert("Failed to update the global timeout."); 
      console.error(error);
    }
  };

  return (
    <div>
      <div className="image-container">
        <img
          src="heygen.svg"
          alt="Heygen Title"
          className="title-image"
        />
      </div>

      <div className="container my-5">
        <div className="card shadow p-4">
          <h1 className="text-center mb-4">Job Status</h1>
          <h2
            className={`text-center ${
              status === 'Completed'
                ? 'text-success'
                : status === 'Error'
                ? 'text-danger'
                : 'text-primary'
            }`}
          >
            {status}
          </h2>

          <div className="d-flex justify-content-center my-4">
            {status !== 'Pending' && (
              <button className="btn btn-primary mx-2" onClick={startJob}>
                Start Job
              </button>
            )}
          </div>

          <hr />

          <h3 className="text-center my-4">Manage Global Timeout</h3>
          <div className="row justify-content-center">
            <div className="col-md-6">
              <div className="input-group mb-3">
                <input
                  type="number"
                  className="form-control"
                  placeholder="Enter new timeout in ms"
                  value={newTimeout}
                  onChange={(e) => setNewTimeout(e.target.value)} 
                />
                <button className="btn btn-success" onClick={updateGlobalTimeout}>
                  Set Timeout
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default JobStatusComponent;
