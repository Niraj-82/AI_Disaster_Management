package com.example.resqai.model

import android.net.Uri

data class Incident(
    val id: String = System.currentTimeMillis().toString(), // Simple unique ID
    val type: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val photoUri: String?, // Store URI as String
    val latitude: Double?,
    val longitude: Double?
)