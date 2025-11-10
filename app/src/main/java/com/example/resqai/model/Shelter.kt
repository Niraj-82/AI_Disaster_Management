package com.example.resqai.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Shelter(
    val id: String? = null,
    val name: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val status: String? = null,
    val capacity: Long? = 0,
    val currentOccupancy: Long? = 0,
    val supplies: String? = null,
    val contactInfo: String? = null,
    val medicalAvailable: Boolean? = false,
    val lastUpdated: Date? = null
) : Parcelable
