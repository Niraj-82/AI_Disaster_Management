package com.example.resqai.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Shelter(
    val id: String? = null,
    val name: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val status: String? = null,
    val capacity: Int? = null,
    val currentOccupancy: Int? = null,
    val supplies: String? = null,
    val contactInfo: String? = null,
    val medicalAvailable: Boolean? = false,
    val lastUpdated: String? = null
) : Parcelable