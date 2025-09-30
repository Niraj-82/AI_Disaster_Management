package com.example.resqai.data

import android.content.Context
import android.widget.Toast
import com.example.resqai.model.Incident
import com.example.resqai.utils.NetworkUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object IncidentRepository {

    fun getAllIncidents(context: Context, callback: (List<Incident>) -> Unit) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
            callback(emptyList())
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("incidents")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val incidents = result.toObjects(Incident::class.java)
                callback(incidents)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to load incidents: ${e.message}", Toast.LENGTH_SHORT).show()
                callback(emptyList())
            }
    }
}