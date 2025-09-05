package com.example.aidm

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShelterData(
    val id: String,
    val name: String,
    val address: String,
    val currentCapacity: Int,
    val maxCapacity: Int,
    val latitude: Double?,
    val longitude: Double?,
    val contactInfo: String? = null,
    val servicesAvailable: List<String>? = null, // e.g., ["food", "water", "medical"]
    val availableSupplies: List<String>? = null, // New: e.g., ["blankets", "canned food"]
    val iconUrl: String? = null // Optional URL for a custom icon
) : Parcelable {
    fun getCapacityString(): String {
        return "$currentCapacity / $maxCapacity"
    }
}
