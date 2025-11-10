package com.example.resqai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.model.Shelter
import java.util.Date

class ResourceTrackerActivity : AppCompatActivity() {

    private lateinit var shelterAdapter: ShelterAdapter
    private lateinit var recyclerView: RecyclerView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource_tracker)
        title = getString(R.string.title_activity_resource_tracker)

        val dummyShelters = listOf(
            Shelter(
                id = "1",
                name = "City Hall Shelter",
                latitude = 34.0522,
                longitude = -118.2437,
                address = "123 Main St",
                status = "Half Full",
                medicalAvailable = true,
                capacity = 100,
                currentOccupancy = 45,
                supplies = "Water, Blankets, Canned Food",
                contactInfo = "555-1234",
                lastUpdated = Date()
            ),
            Shelter(
                id = "2",
                name = "Community Center North",
                latitude = 34.0588,
                longitude = -118.2399,
                address = "456 North Ave",
                status = "Half Full",
                medicalAvailable = true,
                capacity = 150,
                currentOccupancy = 90,
                supplies = "Medical Kits, Baby Formula",
                contactInfo = "555-5678",
                lastUpdated = Date()
            ),
            Shelter(
                id = "3",
                name = "High School Gym",
                latitude = 34.0450,
                longitude = -118.2500,
                address = "789 School Rd",
                status = "Full",
                medicalAvailable = false,
                capacity = 200,
                currentOccupancy = 200,
                supplies = "Water, Cots, Pet Food",
                contactInfo = "555-9012",
                lastUpdated = Date()
            )
        )

        recyclerView = findViewById(R.id.rv_shelters)
        recyclerView.layoutManager = LinearLayoutManager(this)
        shelterAdapter = ShelterAdapter(dummyShelters, null, false) { _ -> /* No-op click listener */ }
        recyclerView.adapter = shelterAdapter
    }
}
