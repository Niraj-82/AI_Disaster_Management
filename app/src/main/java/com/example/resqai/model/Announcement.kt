package com.example.resqai.model

import com.google.firebase.firestore.GeoPoint

data class Announcement(
    val id: String? = null,
    val title: String? = null,
    val message: String? = null,
    val timestamp: Long? = null,
    val location: GeoPoint? = null
)
