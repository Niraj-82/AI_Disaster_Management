package com.example.resqai.data

import com.example.resqai.model.Incident

object IncidentRepository {
    private val reportedIncidents = mutableListOf<Incident>()

    fun addIncident(incident: Incident) {
        reportedIncidents.add(0, incident) // Add to the top of the list
    }

    fun getAllIncidents(): List<Incident> {
        return reportedIncidents.toList()
    }

    fun clearIncidents() {
        reportedIncidents.clear()
    }
}