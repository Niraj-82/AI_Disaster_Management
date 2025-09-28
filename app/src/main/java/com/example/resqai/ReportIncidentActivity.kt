package com.example.resqai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.resqai.model.Incident
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Date
import java.util.UUID

class ReportIncidentActivity : AppCompatActivity() {

    private lateinit var editTextIncidentType: AutoCompleteTextView
    private lateinit var editTextDescription: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var imageViewIncidentPreview: ImageView
    private lateinit var buttonAddPhoto: Button
    private lateinit var buttonSubmitIncident: Button

    private var imageUri: Uri? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let {
                imageUri = it
                imageViewIncidentPreview.setImageURI(it)
                imageViewIncidentPreview.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_incident)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        editTextIncidentType = findViewById(R.id.editTextIncidentType)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextLocation = findViewById(R.id.editTextLocation)
        imageViewIncidentPreview = findViewById(R.id.imageViewIncidentPreview)
        buttonAddPhoto = findViewById(R.id.buttonAddPhoto)
        buttonSubmitIncident = findViewById(R.id.buttonSubmitIncident)

        // Setup Incident Type Dropdown
        val incidentTypes = resources.getStringArray(R.array.incident_types)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, incidentTypes)
        editTextIncidentType.setAdapter(adapter)


        buttonAddPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        buttonSubmitIncident.setOnClickListener { submitReport() }
    }

    private fun submitReport() {
        val type = editTextIncidentType.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
        val location = editTextLocation.text.toString().trim()

        if (type.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Incident Type, Description, and Location are required.", Toast.LENGTH_SHORT).show()
            return
        }

        buttonSubmitIncident.isEnabled = false
        Toast.makeText(this, "Submitting...", Toast.LENGTH_SHORT).show()

        if (imageUri != null) {
            uploadImageAndSaveIncident(type, description, location)
        } else {
            saveIncident(type, description, location, null)
        }
    }

    private fun uploadImageAndSaveIncident(type: String, description: String, location: String) {
        val filename = UUID.randomUUID().toString()
        val storageRef = storage.reference.child("incident_photos/$filename")

        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    saveIncident(type, description, location, imageUrl)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                buttonSubmitIncident.isEnabled = true
            }
    }

    private fun saveIncident(type: String, description: String, locationString: String, imageUrl: String?) {
        val incidentId = UUID.randomUUID().toString()

        val latLng = locationString.split(",").map { it.trim() }
        val latitude = latLng.getOrNull(0)?.toDoubleOrNull()
        val longitude = latLng.getOrNull(1)?.toDoubleOrNull()

        if (latitude == null || longitude == null) {
            Toast.makeText(this, "Invalid location format. Please use 'latitude, longitude'.", Toast.LENGTH_LONG).show()
            buttonSubmitIncident.isEnabled = true
            return
        }

        val incident = Incident(
            id = incidentId,
            type = type,
            description = description,
            timestamp = Date().time,
            latitude = latitude,
            longitude = longitude,
            imageUrl = imageUrl
        )

        db.collection("incidents").document(incidentId)
            .set(incident)
            .addOnSuccessListener {
                Toast.makeText(this, "Incident reported successfully!", Toast.LENGTH_SHORT).show()
                finish() // Close activity
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to report incident: ${e.message}", Toast.LENGTH_LONG).show()
                buttonSubmitIncident.isEnabled = true
            }
    }
}
