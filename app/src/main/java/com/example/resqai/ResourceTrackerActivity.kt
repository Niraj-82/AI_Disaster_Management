package com.example.resqai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.model.Shelter

class ResourceTrackerActivity : AppCompatActivity() {

    private lateinit var shelterAdapter: ShelterAdapter
    private lateinit var recyclerView: RecyclerView
    private val dummyShelters = listOf(
        Shelter("1", "City Hall Shelter", "123 Main St", 100, 45, listOf("Water", "Blankets", "Canned Food"), "555-1234", 34.0522, -118.2437),
        Shelter("2", "Community Center North", "456 North Ave", 150, 90, listOf("Medical Kits", "Baby Formula"), "555-5678", 34.0588, -118.2399),
        Shelter("3", "High School Gym", "789 School Rd", 200, 120, listOf("Water", "Cots", "Pet Food"), "555-9012", 34.0450, -118.2500)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource_tracker)
        title = getString(R.string.title_activity_resource_tracker)

        recyclerView = findViewById(R.id.rv_shelters)
        recyclerView.layoutManager = LinearLayoutManager(this)
        shelterAdapter = ShelterAdapter(dummyShelters)
        recyclerView.adapter = shelterAdapter
    }
}