package com.example.resqai.model

// No longer need android.net.Uri import for this data class

data class Incident(
    val id: String = System.currentTimeMillis().toString(), // Simple unique ID
    val type: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    var imageUrl: String? = null, // Will store the remote URL from Firebase Storage
    val latitude: Double?,
    val longitude: Double?
)