package com.example.aidm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FirstAidActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var firstAidAdapter: FirstAidAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_aid)

        // Find the toolbar and set it as the action bar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerViewFirstAid)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create sample data
        val sampleTopics = listOf(
            FirstAidTopic("Burns", "How to treat minor burns from heat or chemicals."),
            FirstAidTopic("Cuts & Scrapes", "Proper cleaning and bandaging techniques."),
            FirstAidTopic("Choking", "What to do when someone is choking."),
            FirstAidTopic("Fractures", "How to immobilize a suspected broken bone."),
            FirstAidTopic("Insect Bites", "Treating stings and bites from common insects."),
            FirstAidTopic("Sprains", "The R.I.C.E. method for treating sprains."),
            FirstAidTopic("Allergic Reactions", "Recognizing and responding to anaphylaxis.")
        )

        // Set the adapter
        firstAidAdapter = FirstAidAdapter(sampleTopics)
        recyclerView.adapter = firstAidAdapter
    }
}