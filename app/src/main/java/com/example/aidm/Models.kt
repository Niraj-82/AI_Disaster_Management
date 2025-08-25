package com.example.aidm

import kotlinx.serialization.Serializable
import androidx.activity.ComponentActivity
import android.os.Bundle
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

val mockShelters = listOf(
    Shelter("1", "City Hall Shelter", 19.0760, 72.8777, 300, 140, "Fort, Mumbai"),
    Shelter("2", "Community Stadium", 19.2183, 72.9781, 500, 420, "Thane West"),
    Shelter("3", "School Gym Shelter", 18.5204, 73.8567, 200, 60, "Pune Camp")
)
