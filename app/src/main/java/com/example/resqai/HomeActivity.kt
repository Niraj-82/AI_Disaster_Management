package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val reportIncidentButton: Button = findViewById(R.id.btn_report_incident)
        val viewMapButton: Button = findViewById(R.id.btn_view_map)
        val firstAidButton: Button = findViewById(R.id.btn_first_aid)
        val profileButton: Button = findViewById(R.id.btn_profile)

        reportIncidentButton.setOnClickListener {
            startActivity(Intent(this, IncidentReportActivity::class.java))
        }

        viewMapButton.setOnClickListener {
            Toast.makeText(this, "Feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        firstAidButton.setOnClickListener {
            startActivity(Intent(this, FirstAidActivity::class.java))
        }

        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
