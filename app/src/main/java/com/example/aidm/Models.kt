package com.example.aidm

import kotlinx.serialization.Serializable

@Serializable
data class Shelter(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val capacity: Int,
    val available: Int,
    val address: String
)

@Serializable
data class IncidentReport(
    val id: String,
    val type: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    val timestamp: Long
)
