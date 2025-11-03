package com.example.resqai.model

data class Incident(
    val id: Long = 0,
    val type: String,
    val description: String,
    val imageUrl: String?,
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?,
    val locationString: String?,
    val reporterName: String?
)
