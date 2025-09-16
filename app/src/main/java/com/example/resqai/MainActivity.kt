package com.example.resqai

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val medicalInfoButton: Button = findViewById(R.id.btn_medical_info)
        medicalInfoButton.setOnClickListener {
            val intent = Intent(this, MedicalInfoActivity::class.java)
            startActivity(intent)
        }

        val resourceTrackerButton: Button = findViewById(R.id.btn_resource_tracker)
        resourceTrackerButton.setOnClickListener {
            val intent = Intent(this, ResourceTrackerActivity::class.java)
            startActivity(intent)
        }

        val safeRoutingButton: Button = findViewById(R.id.btn_safe_routing)
        safeRoutingButton.setOnClickListener {
            val intent = Intent(this, SafeRoutingActivity::class.java)
            startActivity(intent)
        }

        val sosButton: Button = findViewById(R.id.btn_sos)
        sosButton.setOnClickListener {
            val intent = Intent(this, SOSActivity::class.java)
            startActivity(intent)
        }

        val incidentReportButton: Button = findViewById(R.id.btn_incident_report)
        incidentReportButton.setOnClickListener {
            val intent = Intent(this, IncidentReportActivity::class.java)
            startActivity(intent)
        }

        val viewIncidentsButton: Button = findViewById(R.id.btn_view_incidents)
        viewIncidentsButton.setOnClickListener {
            val intent = Intent(this, SharedIncidentsActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}