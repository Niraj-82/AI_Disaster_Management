package com.example.resqai.model

data class Shelter(
    val id: String,
    val name: String,
    val address: String,
    val capacity: Int,
    val currentOccupancy: Int,
    val supplies: List<String>,
    val contactInfo: String,
    val latitude: Double,
    val longitude: Double
)