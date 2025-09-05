package com.example.aidm

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.input.key.type
import androidx.lifecycle.observe
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SharedIncidentActivity : AppCompatActivity() {

    private val sharedIncidentViewModel: SharedIncidentViewModel by viewModels()

    private lateinit var toolbar: MaterialToolbar
    private lateinit var imageViewIncidentPhoto: ImageView
    private lateinit var textViewIncidentType: TextView
    private lateinit var textViewIncidentDescription: TextView
    private lateinit var textViewIncidentLocation: TextView
    private lateinit var textViewIncidentTimestamp: TextView
    private lateinit var textViewIncidentStatus: TextView
    private lateinit var textViewReportedBy: TextView

    companion object {
        const val EXTRA_INCIDENT_ID = "INCIDENT_ID" // Key for passing incident ID via Intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_incident_incideant)

        // Initialize views
        toolbar = findViewById(R.id.toolbar_shared_incident)
        imageViewIncidentPhoto = findViewById(R.id.imageViewIncidentPhoto)
        textViewIncidentType = findViewById(R.id.textViewIncidentType)
        textViewIncidentDescription = findViewById(R.id.textViewIncidentDescription)
        textViewIncidentLocation = findViewById(R.id.textViewIncidentLocation)
        textViewIncidentTimestamp = findViewById(R.id.textViewIncidentTimestamp)
        textViewIncidentStatus = findViewById(R.id.textViewIncidentStatus)
        textViewReportedBy = findViewById(R.id.textViewReportedBy)

        // Setup Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Incident Details" // Default title

        // Observe LiveData from ViewModel
        sharedIncidentViewModel.incidentDetails.observe(this) { incident ->
            if (incident != null) {
                supportActionBar?.title = incident.type // Update toolbar title with incident type
                textViewIncidentType.text = incident.type
                textViewIncidentDescription.text = incident.description
                textViewIncidentLocation.text = incident.location
                textViewIncidentStatus.text = incident.status
                textViewReportedBy.text = incident.reportedBy ?: "N/A"

                // Format timestamp
                try {
                    val sdf = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
                    textViewIncidentTimestamp.text = sdf.format(Date(incident.timestamp))
                } catch (e: Exception) {
                    textViewIncidentTimestamp.text = "Invalid date"
                }

                // Load image using a library like Coil or Glide
                // Example with a placeholder if imageUrl is null or empty
                if (!incident.imageUrl.isNullOrEmpty()) {
                    // imageViewIncidentPhoto.load(incident.imageUrl) {
                    //     placeholder(R.drawable.ic_menu_gallery) // from activity_shared_incident_incideant.xml
                    //     error(android.R.drawable.ic_menu_report_image)
                    // }
                    Toast.makeText(this, "Image loading via ${incident.imageUrl} (not implemented)", Toast.LENGTH_SHORT).show()
                    imageViewIncidentPhoto.setImageResource(android.R.drawable.ic_menu_gallery) // Placeholder
                } else {
                    imageViewIncidentPhoto.setImageResource(android.R.drawable.ic_menu_gallery) // Default placeholder
                }

            } else {
                Toast.makeText(this, "Incident details not found or error loading.", Toast.LENGTH_LONG).show()
                // Optionally, finish the activity if no data can be shown
                // finish()
            }
        }

        // Get incident ID from Intent and load data
        val incidentId = intent.getStringExtra(EXTRA_INCIDENT_ID)
        if (incidentId != null) {
            sharedIncidentViewModel.loadIncidentById(incidentId)
        } else {
            Toast.makeText(this, "No incident ID provided.", Toast.LENGTH_LONG).show()
            // Fallback or load a default/error state if no ID is passed
            sharedIncidentViewModel.setIncidentData(null) // Clear or set to an error state
            // finish() // Or finish if an ID is strictly required
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        if (item.itemId == android.R.id.home) {
            // This is the Up button
            finish() // or super.onBackPressed();
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

