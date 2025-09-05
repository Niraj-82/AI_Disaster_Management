package com.example.aidm

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns // Needed for getting file name from ACTION_OPEN_DOCUMENT
import android.util.Log // For logging errors
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ReportIncidentActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var autoCompleteIncidentType: AutoCompleteTextView
    private lateinit var editTextIncidentDescription: TextInputEditText
    private lateinit var editTextIncidentLocation: TextInputEditText
    private lateinit var buttonAttachMedia: Button
    private lateinit var textViewAttachedFileName: TextView
    private lateinit var editTextReporterName: TextInputEditText
    private lateinit var editTextReporterContact: TextInputEditText
    private lateinit var buttonSubmitReport: Button
    private lateinit var textFieldLayoutIncidentLocation: TextInputLayout

    private var attachedFileUri: Uri? = null

    // Using ACTION_OPEN_DOCUMENT for a more robust way to pick media
    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) { // Activity.RESULT_OK simplified to RESULT_OK
            result.data?.data?.let { uri ->
                attachedFileUri = uri
                // Attempt to get a displayable name for the file
                val fileName = try {
                    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (cursor.moveToFirst() && nameIndex != -1) {
                            cursor.getString(nameIndex)
                        } else {
                            "Attached File"
                        }
                    } ?: "Attached File"
                } catch (e: Exception) {
                    Log.e("ReportIncidentActivity", "Error getting file name", e) // Log the exception
                    "Attached File"
                }
                textViewAttachedFileName.text = fileName
                textViewAttachedFileName.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_incident)

        // Initialize views - Ensure these IDs match your XML exactly
        toolbar = findViewById(R.id.toolbar_report_incident)
        autoCompleteIncidentType = findViewById(R.id.autoCompleteIncidentType)
        editTextIncidentDescription = findViewById(R.id.editTextIncidentDescription)
        editTextIncidentLocation = findViewById(R.id.editTextIncidentLocation)
        textFieldLayoutIncidentLocation = findViewById(R.id.textFieldLayoutIncidentLocation)
        buttonAttachMedia = findViewById(R.id.buttonAttachMedia)
        textViewAttachedFileName = findViewById(R.id.textViewAttachedFileName)
        editTextReporterName = findViewById(R.id.editTextReporterName)
        editTextReporterContact = findViewById(R.id.editTextReporterContact)
        buttonSubmitReport = findViewById(R.id.buttonSubmitReport)

        // Setup Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Populate Incident Types
        val incidentTypes = arrayOf("Fire", "Medical Emergency", "Road Accident", "Suspicious Activity", "Natural Disaster", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, incidentTypes)
        autoCompleteIncidentType.setAdapter(adapter)

        // Attach Media Button Click
        buttonAttachMedia.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*" // Allows all types
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*")) // Specify desired MIME types
            }
            pickMediaLauncher.launch(intent)
        }

        // Location Icon Click (End icon of the location field)
        textFieldLayoutIncidentLocation.setEndIconOnClickListener {
            // TODO: Implement "Use Current Location" functionality
            Toast.makeText(this, "Get Current Location clicked (Not implemented)", Toast.LENGTH_SHORT).show()
        }

        // Submit Report Button Click
        buttonSubmitReport.setOnClickListener {
            if (validateInput()) {
                // TODO: Implement report submission logic
                val incidentType = autoCompleteIncidentType.text.toString()
                val description = editTextIncidentDescription.text.toString()
                val location = editTextIncidentLocation.text.toString()
                val reporterName = editTextReporterName.text.toString()
                val reporterContact = editTextReporterContact.text.toString()

                val reportDetails = """
                    Incident Type: $incidentType
                    Description: $description
                    Location: $location
                    Media: ${attachedFileUri?.toString() ?: "None"}
                    Reporter: $reporterName
                    Contact: $reporterContact
                """.trimIndent()

                Toast.makeText(this, "Report Submitted!\n$reportDetails", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun validateInput(): Boolean {
        if (autoCompleteIncidentType.text.isNullOrBlank()) {
            autoCompleteIncidentType.error = "Incident type is required"
            autoCompleteIncidentType.requestFocus()
            return false
        }
        if (editTextIncidentDescription.text.isNullOrBlank()) {
            editTextIncidentDescription.error = "Description is required"
            editTextIncidentDescription.requestFocus()
            return false
        }
        if (editTextIncidentLocation.text.isNullOrBlank()) {
            editTextIncidentLocation.error = "Location is required"
            editTextIncidentLocation.requestFocus()
            return false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
