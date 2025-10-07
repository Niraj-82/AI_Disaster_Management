package com.example.resqai.model

import com.google.firebase.firestore.GeoPoint


data class Incident(
    val id: String = "",
    val type: String = "",
    val description: String = "",
    val timestamp: Long = 0,
    val imageUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val reporterName: String? = null, // Added to store the reporter's name
    val locationString: String? = null // Added to store the manual location address
)
