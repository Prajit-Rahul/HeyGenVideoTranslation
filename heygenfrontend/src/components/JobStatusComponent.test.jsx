import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import JobStatusComponent from '../components/JobStatusComponent';
import JobStatusClient from '../services/JobStatusClient.js';
global.alert = jest.fn(); // Mock the global alert function.

jest.mock('../services/JobStatusClient.js'); // Mock the JobStatusClient to isolate tests from backend.

describe('JobStatusComponent', () => {
  let mockClient;

  // Set up a mock client before each test.
  beforeEach(() => {
    mockClient = {
      startJob: jest.fn(), // Mocked startJob function.
      pollJobStatus: jest.fn(), // Mocked pollJobStatus function.
      setGlobalTimeout: jest.fn(), // Mocked setGlobalTimeout function.
    };
    JobStatusClient.mockImplementation(() => mockClient); // Replace JobStatusClient with mockClient.
  });

  // Clear all mocks and timers after each test.
  afterEach(() => {
    jest.clearAllMocks(); 
    jest.clearAllTimers(); 
  });

  // Test case: Verifies the initial render of the JobStatusComponent.
  test('renders the Job Status component', () => {
    render(<JobStatusComponent />);
    expect(screen.getByText('Job Status')).toBeInTheDocument(); // Check for the main heading.
    expect(screen.getByText('Not Started')).toBeInTheDocument(); // Check for the initial status.
    expect(screen.getByText('Start Job')).toBeInTheDocument(); // Check for the presence of the Start Job button.
  });

  // Test case: Verifies that the status updates to "Pending" when a job is started.
  test('displays Pending status when job is started', async () => {
    render(<JobStatusComponent />);
    
    const startJobButton = screen.getByText('Start Job');
    fireEvent.click(startJobButton); // Simulate a click on the Start Job button.
  
    await waitFor(() => {
      expect(mockClient.startJob).toHaveBeenCalled(); // Ensure startJob was called.
      expect(screen.getByText('Pending')).toBeInTheDocument(); // Check if status updates to "Pending".
    });
  });

  // Test case: Verifies that the status updates to "Completed" on job completion.
  test('updates status to Completed on job completion', async () => {
    mockClient.startJob.mockResolvedValue('mockJobId'); // Simulate a successful job start.
    mockClient.pollJobStatus.mockImplementation((callback) => callback('completed')); // Simulate a "completed" status.

    render(<JobStatusComponent />);

    const startButton = screen.getByText('Start Job');
    fireEvent.click(startButton);

    await waitFor(() => expect(screen.getByText('Completed')).toBeInTheDocument()); // Check if status updates to "Completed".
  });

  // Test case: Verifies that the status updates to "Error" when a job fails.
  test('shows error when job fails', async () => {
    mockClient.startJob.mockResolvedValue('mockJobId'); // Simulate a successful job start.
    mockClient.pollJobStatus.mockImplementation((callback) => callback('error')); // Simulate an "error" status.

    render(<JobStatusComponent />);

    const startButton = screen.getByText('Start Job');
    fireEvent.click(startButton);

    await waitFor(() => expect(screen.getByText('Error')).toBeInTheDocument()); // Check if status updates to "Error".
  });

  // Test case: Verifies that the global timeout is updated successfully.
  test('updates global timeout successfully', async () => {
    render(<JobStatusComponent />);
    
    const input = screen.getByPlaceholderText('Enter new timeout in ms');
    fireEvent.change(input, { target: { value: '2000' } }); // Simulate input of a new timeout value.
    
    const setTimeoutButton = screen.getByText('Set Timeout');
    fireEvent.click(setTimeoutButton); // Simulate a click on the Set Timeout button.
  
    await waitFor(() => {
      expect(mockClient.setGlobalTimeout).toHaveBeenCalledWith(2000); // Ensure the correct timeout value was sent.
    });
  
    await waitFor(() => {
      expect(input.value).toBe(''); // Ensure the input field is cleared after a successful update.
    });
  });

  // Test case: Verifies that an alert is shown for invalid timeout input.
  test('shows alert for invalid global timeout input', async () => {
    jest.spyOn(window, 'alert').mockImplementation(() => {}); // Mock the alert function.

    render(<JobStatusComponent />);

    const timeoutInput = screen.getByPlaceholderText('Enter new timeout in ms');
    const setTimeoutButton = screen.getByText('Set Timeout');

    fireEvent.change(timeoutInput, { target: { value: '-500' } }); // Simulate invalid input.
    fireEvent.click(setTimeoutButton); // Simulate a click on the Set Timeout button.

    expect(window.alert).toHaveBeenCalledWith('Please enter a valid timeout value greater than 0.'); // Ensure alert is shown with correct message.
  });
});
