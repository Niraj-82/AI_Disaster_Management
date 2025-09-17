package com.example.aidm

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.layout.layout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.resqai.model.IncidentReport // Import the data class

class ReportIncidentActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var editTextIncidentType: TextInputEditText
    private lateinit var editTextDescription: TextInputEditText
    private lateinit var editTextLocation: TextInputEditText
    private lateinit var editTextReporterName: TextInputEditText
    private lateinit var editTextSeverity: TextInputEditText
    private lateinit var buttonSubmitIncident: MaterialButton

    // TODO: Inject or initialize ReportIncidentViewModel here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_incident)

        toolbar = findViewById(R.id.toolbarReportIncident)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button
        supportActionBar?.setDisplayShowHomeEnabled(true)

        editTextIncidentType = findViewById(R.id.editTextIncidentType)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextLocation = findViewById(R.id.editTextLocation)
        editTextReporterName = findViewById(R.id.editTextReporterName)
        editTextSeverity = findViewById(R.id.editTextSeverity)
        buttonSubmitIncident = findViewById(R.id.buttonSubmitIncident)

        buttonSubmitIncident.setOnClickListener {
            submitIncidentReport()
        }
    }

    private fun submitIncidentReport() {
        val incidentType = editTextIncidentType.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
        val location = editTextLocation.text.toString().trim()
        val reporterName = editTextReporterName.text.toString().trim()
        val severity = editTextSeverity.text.toString().trim()

        if (incidentType.isEmpty() || description.isEmpty() || location.isEmpty() || severity.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_LONG).show()
            return
        }

        // Create an IncidentReport object (timestamp will be set by Firestore)
        // val incidentReport = IncidentReport(
        //     incidentType = incidentType,
        //     description = description,
        //     location = location,
        //     reporterName = reporterName, // Can be empty if optional
        //     severity = severity
        // )

        // TODO: Call ViewModel to save incidentReport
        Toast.makeText(this, "Incident Type: $incidentType\nDescription: $description\nLocation: $location\nSeverity: $severity", Toast.LENGTH_LONG).show()
        // For now, just show a Toast. Actual submission will involve the ViewModel.

        // Optionally, clear fields or navigate away after submission
        // clearForm()
        // finish() // Close activity after submission
    }

    private fun clearForm() {
        editTextIncidentType.text = null
        editTextDescription.text = null
        editTextLocation.text = null
        editTextReporterName.text = null
        editTextSeverity.text = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
