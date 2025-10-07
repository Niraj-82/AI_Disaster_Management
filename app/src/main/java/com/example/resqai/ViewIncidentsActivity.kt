package com.example.resqai

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.adapter.IncidentAdapter
import com.example.resqai.model.IncidentReport
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewIncidentsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var incidentsRecyclerView: RecyclerView
    private lateinit var incidentAdapter: IncidentAdapter
    private var incidentList = mutableListOf<IncidentReport>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_incidents)

        incidentsRecyclerView = findViewById(R.id.incidentsRecyclerView)
        incidentsRecyclerView.layoutManager = LinearLayoutManager(this)

        incidentAdapter = IncidentAdapter(incidentList)
        incidentsRecyclerView.adapter = incidentAdapter

        db = FirebaseFirestore.getInstance()

        fetchIncidents()
    }

    private fun fetchIncidents() {
        db.collection("incidents")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                incidentList.clear()
                for (document in result) {
                    val incident = document.toObject(IncidentReport::class.java)
                    incident.id = document.id // Store the document ID
                    incidentList.add(incident)
                }
                incidentAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("ViewIncidentsActivity", "Error getting documents.", exception)
            }
    }
}