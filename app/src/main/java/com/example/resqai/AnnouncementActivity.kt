package com.example.resqai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.adapter.AnnouncementAdapter
import com.example.resqai.model.Announcement
import com.example.resqai.utils.NetworkUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AnnouncementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnnouncementAdapter
    private val announcements = mutableListOf<Announcement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_announcement)

        recyclerView = findViewById(R.id.rv_announcements)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AnnouncementAdapter(announcements)
        recyclerView.adapter = adapter

        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("announcements")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                announcements.clear()
                for (document in result) {
                    val announcement = document.toObject(Announcement::class.java)
                    announcements.add(announcement)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load announcements: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}