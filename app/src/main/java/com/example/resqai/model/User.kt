package com.example.resqai.model

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "normal" // Default role is "normal", can be "admin"
) {
    // No-argument constructor for Firestore deserialization
    constructor() : this("", "", "normal")
}