package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DisasterPrepActivity : AppCompatActivity() {

    private lateinit var lvDisasterTypes: ListView
    private lateinit var tvTitle: TextView

    // Sample data - in a real app, this might come from a database or strings.xml
    private val disasterTypes = arrayOf(
        "Earthquake",
        "Flood",
        "Wildfire",
        "Hurricane",
        "Tornado",
        "Pandemic"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disaster_prep)

        tvTitle = findViewById(R.id.tv_disaster_prep_title)
        lvDisasterTypes = findViewById(R.id.lv_disaster_types)

        tvTitle.text = getString(R.string.disaster_prep_title) // Assuming you have this string

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, disasterTypes)
        lvDisasterTypes.adapter = adapter

        lvDisasterTypes.setOnItemClickListener { parent, view, position, id ->
            val selectedDisaster = disasterTypes[position]
            val intent = Intent(this, PrepTipsActivity::class.java).apply {
                putExtra(PrepTipsActivity.EXTRA_DISASTER_TYPE, selectedDisaster)
            }
            startActivity(intent)
        }
    }
}
