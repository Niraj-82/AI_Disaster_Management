package com.example.resqai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.resqai.databinding.ActivityReportIncidentBinding
import com.example.resqai.db.DatabaseHelper
import com.example.resqai.model.Incident
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.UUID

class ReportIncidentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportIncidentBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private var imageUri: Uri? = null
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var generativeModel: GenerativeModel

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data
            binding.imageViewIncidentPreview.setImageURI(imageUri)
            binding.imageViewIncidentPreview.visibility = View.VISIBLE
        }
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportIncidentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        dbHelper = DatabaseHelper(this)

        initializeGenerativeModel()
        setupToolbar()
        setupLocationRadioGroup()
        setupButtons()

        if (binding.radioButtonCurrentLocation.isChecked) {
            requestLocationPermission()
        }
    }

    private fun initializeGenerativeModel() {
        // NOTE: Make sure you have your Gemini API key in local.properties
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarReportIncident)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarReportIncident.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupLocationRadioGroup() {
        binding.radioGroupLocation.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioButtonCurrentLocation) {
                binding.tilLocation.visibility = View.GONE
                requestLocationPermission()
            } else {
                binding.tilLocation.visibility = View.VISIBLE
            }
        }
    }

    private fun setupButtons() {
        binding.buttonAddPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        binding.buttonSubmitIncident.setOnClickListener {
            submitReport()
        }
        
        binding.buttonPredictSeverity.setOnClickListener {
            predictSeverity()
        }
    }
    
    private fun predictSeverity() {
        val incidentType = binding.editTextIncidentType.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()

        if (incidentType.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Please enter incident type and description first.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.textViewSeverityResult.visibility = View.VISIBLE
        binding.textViewSeverityResult.text = "Predicting..."

        lifecycleScope.launch {
            try {
                val prompt = """
                    Based on the following incident details, analyze the situation and predict the severity level.
                    The output should be only one word: Low, Medium, or High.

                    Incident Type: "$incidentType"
                    Description: "$description"
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                
                // Ensure the response is one of the expected values, with a fallback
                val severity = response.text?.trim()?.let {
                    if (it.equals("Low", ignoreCase = true) || it.equals("Medium", ignoreCase = true) || it.equals("High", ignoreCase = true)) {
                        it
                    } else {
                        "Cannot determine"
                    }
                } ?: "Cannot determine"

                binding.textViewSeverityResult.text = "Severity: $severity"

            } catch (e: Exception) {
                binding.textViewSeverityResult.text = "Error"
                Toast.makeText(this@ReportIncidentActivity, "Error predicting severity: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    Toast.makeText(this, "Location acquired.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Could not get location.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun submitReport() {
        val incidentType = binding.editTextIncidentType.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val reporterName = binding.editTextReporterName.text.toString().trim()
        val manualLocation = binding.editTextLocation.text.toString().trim()

        if (incidentType.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Please fill in incident type and description.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.buttonSubmitIncident.isEnabled = false
        Toast.makeText(this, "Submitting report...", Toast.LENGTH_SHORT).show()

        if (imageUri != null) {
            uploadImageAndSaveIncident(incidentType, description, reporterName, manualLocation)
        } else {
            saveIncident(incidentType, description, null, reporterName, manualLocation)
        }
    }

    private fun uploadImageAndSaveIncident(incidentType: String, description: String, reporterName: String, manualLocation: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        imageUri?.let {
            imageRef.putFile(it).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveIncident(incidentType, description, uri.toString(), reporterName, manualLocation)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                binding.buttonSubmitIncident.isEnabled = true
            }
        }
    }

    private fun saveIncident(incidentType: String, description: String, imageUrl: String?, reporterName: String, manualLocation: String) {
        val incident = Incident(
            type = incidentType,
            description = description,
            imageUrl = imageUrl,
            timestamp = System.currentTimeMillis(),
            latitude = if (binding.radioButtonCurrentLocation.isChecked) currentLocation?.latitude else null,
            longitude = if (binding.radioButtonCurrentLocation.isChecked) currentLocation?.longitude else null,
            locationString = if (binding.radioButtonManualLocation.isChecked) manualLocation else null,
            reporterName = reporterName.ifEmpty { null }
        )

        val id = dbHelper.addIncident(incident)

        if (id != -1L) {
            Toast.makeText(this, "Report submitted successfully.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error submitting report.", Toast.LENGTH_LONG).show()
            binding.buttonSubmitIncident.isEnabled = true
        }
    }
}
