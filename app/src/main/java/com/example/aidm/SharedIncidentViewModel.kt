package com.example.aidm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Basic data class to represent incident details.
// You should customize this to match your actual incident data structure.
data class IncidentData(
    val id: String,
    val type: String,
    val description: String,
    val location: String,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Long, // Or String, depending on how you store dates
    val status: String,
    val reportedBy: String?,
    val imageUrl: String? // URL for an image related to the incident
)

class SharedIncidentViewModel : ViewModel() {

    private val _incidentDetails = MutableLiveData<IncidentData?>()
    val incidentDetails: LiveData<IncidentData?> = _incidentDetails

    // Instance of the repository
    private val repository = FakeRepo() // Or initialize from a constructor if using DI

    fun getRepository(): FakeRepo {
        return repository
    }

    // Function to load incident details.
    // In a real app, this might fetch data from a repository (network/database)
    // based on an incidentId.
    fun loadIncidentById(incidentId: String) {
        // --- Placeholder: Mock Data Loading ---
        // Replace this with your actual data fetching logic.
        // For example, if you have a list of incidents or a database:
        if (incidentId == "mock_incident_123") {
            _incidentDetails.value = IncidentData(
                id = "mock_incident_123",
                type = "Structure Fire",
                description = "A fire was reported at the old warehouse on Main Street. Smoke visible from downtown. Emergency services are on their way.",
                location = "123 Main Street, Anytown",
                latitude = 34.0522,
                longitude = -118.2437,
                timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000), // 2 hours ago
                status = "Active",
                reportedBy = "Citizen Reporter (via App)",
                imageUrl = "https://via.placeholder.com/400x200.png?text=Incident+Image" // Placeholder image URL
            )
        } else {
            // Simulate another incident for demonstration
            if (incidentId == "mock_incident_456") {
                _incidentDetails.value = IncidentData(
                    id = "mock_incident_456",
                    type = "Road Accident",
                    description = "Multi-car pileup on Highway 101, near exit 5B. Traffic is heavily congested. Avoid area if possible.",
                    location = "Highway 101, Exit 5B",
                    latitude = 34.0012,
                    longitude = -118.4532,
                    timestamp = System.currentTimeMillis() - (1 * 60 * 60 * 1000), // 1 hour ago
                    status = "Ongoing",
                    reportedBy = "Traffic Cam AI",
                    imageUrl = "https://via.placeholder.com/400x200.png?text=Road+Accident"
                )
            } else {
                _incidentDetails.value = null // Incident not found or error
            }
        }
        // --- End Placeholder ---
    }

    // Function to directly set incident details if you already have the object
    fun setIncidentData(incident: IncidentData?) {
        _incidentDetails.value = incident
    }

    // You might add other functions here, for example:
    // - To update the status of an incident
    // - To handle user interactions related to the incident

    // Clear the incident details, e.g., when the ViewModel is no longer needed or on error
    fun clearIncident() {
        _incidentDetails.value = null
    }
}
