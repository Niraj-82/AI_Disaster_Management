package com.example.aidm

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class HazardType {
    FLOOD,
    FIRE,
    BLOCKED_ROAD,
    STRUCTURAL_DAMAGE,
    CHEMICAL_SPILL,
    LANDSLIDE,
    POWER_OUTAGE_AREA, // For wider area hazards
    OTHER
}

@Parcelize
data class HazardData(
    val id: String,
    val type: HazardType,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val reportedAt: Long, // Timestamp, e.g., System.currentTimeMillis()
    val severity: Int, // e.g., 1 (low) to 5 (high)
    val affectedRadiusKm: Double? = null, // For area-based hazards like floods or fires
    val reportedByUserId: String? = null, // Optional: if crowdsourced
    val isVerified: Boolean = false // If verification is needed
) : Parcelable