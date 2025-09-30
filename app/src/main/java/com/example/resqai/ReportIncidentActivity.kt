package com.example.resqai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.resqai.model.Incident
import com.example.resqai.utils.NetworkUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.Date
import java.util.UUID

class ReportIncidentActivity : AppCompatActivity() {

    private lateinit var editTextIncidentType: AutoCompleteTextView
    private lateinit var editTextDescription: EditText
    private lateinit var radioGroupLocation: RadioGroup
    private lateinit var editTextLocation: EditText
    private lateinit var imageViewIncidentPreview: ImageView
    private lateinit var buttonAddPhoto: Button
    private lateinit var buttonSubmitIncident: Button

    private var imageUri: Uri? = null
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            showPhotoOptions()
        } else {
            Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission is required to use your current location.", Toast.LENGTH_SHORT).show()
        }
    }

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

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        success ->
        if (success) {
            imageUri?.let {
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        editTextIncidentType = findViewById(R.id.editTextIncidentType)
        editTextDescription = findViewById(R.id.editTextDescription)
        radioGroupLocation = findViewById(R.id.radioGroupLocation)
        editTextLocation = findViewById(R.id.editTextLocation)
        imageViewIncidentPreview = findViewById(R.id.imageViewIncidentPreview)
        buttonAddPhoto = findViewById(R.id.buttonAddPhoto)
        buttonSubmitIncident = findViewById(R.id.buttonSubmitIncident)

        val incidentTypes = resources.getStringArray(R.array.incident_types)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, incidentTypes)
        editTextIncidentType.setAdapter(adapter)

        radioGroupLocation.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonCurrentLocation -> {
                    findViewById<View>(R.id.tilLocation).visibility = View.GONE
                    getCurrentLocation()
                }
                R.id.radioButtonManualLocation -> {
                    findViewById<View>(R.id.tilLocation).visibility = View.VISIBLE
                    currentLatitude = null
                    currentLongitude = null
                }
            }
        }

        buttonAddPhoto.setOnClickListener { handlePhotoClick() }
        buttonSubmitIncident.setOnClickListener { submitReport() }

        // Set initial state
        findViewById<View>(R.id.tilLocation).visibility = View.GONE
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        currentLatitude = location.latitude
                        currentLongitude = location.longitude
                        Toast.makeText(this, "Current location captured.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Could not get current location. Please enter it manually.", Toast.LENGTH_SHORT).show()
                        radioGroupLocation.check(R.id.radioButtonManualLocation)
                    }
                }
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun handlePhotoClick() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                showPhotoOptions()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("This app needs camera access to take photos for incident reports.")
                    .setPositiveButton("OK") { _, _ ->
                        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showPhotoOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(this)
            .setTitle("Add Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val file = File(filesDir, "pic.jpg")
                        val newImageUri = FileProvider.getUriForFile(this, "com.example.resqai.fileprovider", file)
                        imageUri = newImageUri
                        takePictureLauncher.launch(newImageUri)
                    }
                    1 -> {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        pickImageLauncher.launch(intent)
                    }
                }
            }
            .show()
    }

    private fun submitReport() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show()
            return
        }

        val type = editTextIncidentType.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        if (type.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Incident Type and Description are required.", Toast.LENGTH_SHORT).show()
            return
        }

        buttonSubmitIncident.isEnabled = false
        Toast.makeText(this, "Submitting...", Toast.LENGTH_SHORT).show()

        val currentImageUri = imageUri
        if (currentImageUri != null) {
            uploadImageAndSaveIncident(type, description, currentImageUri)
        } else {
            saveIncident(type, description, null)
        }
    }

    private fun uploadImageAndSaveIncident(type: String, description: String, uriToUpload: Uri) {
        val filename = UUID.randomUUID().toString()
        val storageRef = storage.reference.child("incident_photos/$filename")

        storageRef.putFile(uriToUpload)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    saveIncident(type, description, imageUrl)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                buttonSubmitIncident.isEnabled = true
            }
    }

    private fun saveIncident(type: String, description: String, imageUrl: String?) {
        val incidentId = UUID.randomUUID().toString()

        var latitude: Double? = null
        var longitude: Double? = null

        if (radioGroupLocation.checkedRadioButtonId == R.id.radioButtonCurrentLocation) {
            latitude = currentLatitude
            longitude = currentLongitude
        } else {
            val locationString = editTextLocation.text.toString().trim()
            if (locationString.isNotEmpty()) {
                val latLng = locationString.split(",").map { it.trim() }
                latitude = latLng.getOrNull(0)?.toDoubleOrNull()
                longitude = latLng.getOrNull(1)?.toDoubleOrNull()

                if (latitude == null || longitude == null) {
                    Toast.makeText(this, "Invalid manual location format. Please use 'latitude, longitude'.", Toast.LENGTH_LONG).show()
                    buttonSubmitIncident.isEnabled = true
                    return
                }
            }
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
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to report incident: ${e.message}", Toast.LENGTH_LONG).show()
                buttonSubmitIncident.isEnabled = true
            }
    }
}
