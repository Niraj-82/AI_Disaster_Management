package com.example.resqai.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Shelter(
    val id: String? = null,
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    // Field for the new feature, as requested
    val status: String? = null, // e.g., "EMPTY", "HALF_FULL", "FULL"
    val medicalAvailable: Boolean? = null,
    // Fields from existing app code
    val capacity: Int? = null,
    val currentOccupancy: Int? = null,
    val supplies: List<String>? = null,
    val contactInfo: String? = null,
    @ServerTimestamp
    val lastUpdated: Date? = null
) : Parcelable {
    // No-argument constructor required by Firestore for deserialization
    constructor() : this(null, null, null, null, null, null, null, null, null, null, null, null)
}
