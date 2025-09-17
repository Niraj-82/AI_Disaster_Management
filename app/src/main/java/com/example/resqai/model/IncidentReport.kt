package com.example.resqai.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class IncidentReport(
    var id: String = "", // Document ID from Firestore
    val incidentType: String = "",
    val description: String = "",
    val location: String = "", // Simple text input for now
    @ServerTimestamp // Automatically set by Firestore on the server
    val timestamp: Date? = null,
    val reporterName: String = "", // Simple text input
    val severity: String = "" // e.g., Low, Medium, High
) {
    // No-argument constructor is required by Firestore for deserialization
    constructor() : this("", "", "", "", null, "", "")
}