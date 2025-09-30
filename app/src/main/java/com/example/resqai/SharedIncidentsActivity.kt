package com.example.resqai

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.adapter.SharedIncidentAdapter
import com.example.resqai.data.IncidentRepository
import com.example.resqai.model.Incident
import com.example.resqai.utils.NetworkUtils

class SharedIncidentsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SharedIncidentAdapter
    private lateinit var tvNoIncidents: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_incidents)
        title = getString(R.string.shared_incidents_title)

        recyclerView = findViewById(R.id.rv_shared_incidents)
        tvNoIncidents = findViewById(R.id.tv_no_incidents)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SharedIncidentAdapter(emptyList()) // Initialize with an empty list
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadIncidents()
    }

    private fun loadIncidents() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show()
            tvNoIncidents.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            return
        }

        IncidentRepository.getAllIncidents(this) { incidents ->
            if (incidents.isEmpty()) {
                tvNoIncidents.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                tvNoIncidents.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.updateIncidents(incidents)
            }
        }
    }
}