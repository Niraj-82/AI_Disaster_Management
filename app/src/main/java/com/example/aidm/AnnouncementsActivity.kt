package com.example.aidm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class AnnouncementsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var announcementAdapter: AnnouncementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcements)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_announcements)
        setSupportActionBar(toolbar)
        // Enable the back button if this is not a top-level screen
        // supportActionBar?.setDisplayHomeAsUpEnabled(true) 
        // supportActionBar?.setDisplayShowHomeEnabled(true)

        recyclerView = findViewById(R.id.recyclerViewAnnouncements)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create sample data
        val sampleAnnouncements = listOf(
            AnnouncementItem("Shelter Capacity Update", "November 5, 2023", "Central City Shelter now has 20 additional beds available. Please direct new arrivals accordingly."),
            AnnouncementItem("Volunteer Training Session", "November 2, 2023", "A mandatory training session for all new volunteers will be held on November 10th at 3 PM in the main hall."),
            AnnouncementItem("Donation Drive Success!", "October 30, 2023", "Thank you to everyone who contributed to our winter clothing drive. We exceeded our goal!"),
            AnnouncementItem("System Maintenance Alert", "October 28, 2023", "Please be advised of scheduled system maintenance tonight from 1 AM to 3 AM. Access to the resource portal may be intermittent.")
        )

        announcementAdapter = AnnouncementAdapter(sampleAnnouncements)
        recyclerView.adapter = announcementAdapter
    }

    // Optional: Handle toolbar back button press
    // override fun onSupportNavigateUp(): Boolean {
    //     onBackPressedDispatcher.onBackPressed()
    //     return true
    // }
}
