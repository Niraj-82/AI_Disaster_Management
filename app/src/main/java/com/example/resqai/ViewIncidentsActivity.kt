package com.example.resqai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resqai.databinding.ActivityViewIncidentsBinding
import com.example.resqai.db.DatabaseHelper

class ViewIncidentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewIncidentsBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var incidentAdapter: IncidentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewIncidentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        dbHelper = DatabaseHelper(this)

        setupRecyclerView()
        loadIncidents()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarViewIncidents)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarViewIncidents.setNavigationOnClickListener { 
            onBackPressedDispatcher.onBackPressed() 
        }
    }

    private fun setupRecyclerView() {
        incidentAdapter = IncidentAdapter(emptyList()) // Start with an empty list
        binding.recyclerViewIncidents.apply {
            layoutManager = LinearLayoutManager(this@ViewIncidentsActivity)
            adapter = incidentAdapter
        }
    }

    private fun loadIncidents() {
        val incidents = dbHelper.getAllIncidents()
        incidentAdapter.updateData(incidents)
    }
}
