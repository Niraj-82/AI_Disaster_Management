package com.example.resqai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.resqai.model.Incident
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.UUID

class IncidentReportActivity : AppCompatActivity() {

    private lateinit var spinnerIncidentType: Spinner
    private lateinit var etIncidentDescription: EditText
    private lateinit var btnTakePhoto: Button
    private lateinit var incidentImageView: ImageView
    private lateinit var btnSubmitReport: Button

    private var imageBitmap: Bitmap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            imageBitmap = data?.extras?.get("data") as Bitmap
            incidentImageView.setImageBitmap(imageBitmap)
            incidentImageView.visibility = View.VISIBLE
        }
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission is required to tag the incident location.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_report)

        spinnerIncidentType = findViewById(R.id.spinner_incident_type)
        etIncidentDescription = findViewById(R.id.et_incident_description)
        btnTakePhoto = findViewById(R.id.btn_take_photo)
        incidentImageView = findViewById(R.id.incidentImageView)
        btnSubmitReport = findViewById(R.id.btn_submit_report)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()

        btnTakePhoto.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    dispatchTakePictureIntent()
                }
                else -> {
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }

        btnSubmitReport.setOnClickListener {
            submitReport()
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        this.currentLocation = location
                    } else {
                        Toast.makeText(this, "Could not get location. Please ensure location services are enabled.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }

    private fun submitReport() {
        val incidentType = spinnerIncidentType.selectedItem.toString()
        val description = etIncidentDescription.text.toString()

        if (description.isBlank()) {
            Toast.makeText(this, "Please provide a description.", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentLocation == null) {
            Toast.makeText(this, "Location not available. Cannot submit report.", Toast.LENGTH_SHORT).show()
            requestLocationPermission()
            return
        }

        btnSubmitReport.isEnabled = false
        Toast.makeText(this, "Submitting report...", Toast.LENGTH_SHORT).show()

        if (imageBitmap != null) {
            uploadImageAndSaveIncident(incidentType, description)
        } else {
            saveIncident(incidentType, description, null)
        }
    }

    private fun uploadImageAndSaveIncident(incidentType: String, description: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                saveIncident(incidentType, description, uri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show()
            btnSubmitReport.isEnabled = true
        }
    }

    private fun saveIncident(incidentType: String, description: String, imageUrl: String?) {
        val location = currentLocation
        if (location == null) {
            Toast.makeText(this, "Failed to submit: Location is missing.", Toast.LENGTH_SHORT).show()
            btnSubmitReport.isEnabled = true
            return
        }

        val db = FirebaseFirestore.getInstance()
        val incident = Incident(
            id = UUID.randomUUID().toString(),
            type = incidentType,
            description = description,
            imageUrl = imageUrl,
            timestamp = Date().time,
            latitude = location.latitude,
            longitude = location.longitude
        )

        db.collection("incidents")
            .add(incident)
            .addOnSuccessListener {
                Toast.makeText(this, "Report submitted successfully.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error submitting report: ${e.message}", Toast.LENGTH_SHORT).show()
                btnSubmitReport.isEnabled = true
            }
    }
}
