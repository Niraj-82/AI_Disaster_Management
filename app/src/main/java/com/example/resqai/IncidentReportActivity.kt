package com.example.resqai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.resqai.model.Incident

class IncidentReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_report)
    }

    fun addIncident(incident: Incident) {
        // Right now, this function doesn't do anything.
        // You would add your logic here to save the incident, perhaps to a database.
    }
}