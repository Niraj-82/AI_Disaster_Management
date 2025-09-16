package com.example.resqai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.resqai.data.IncidentRepository
import com.example.resqai.model.Incident
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class IncidentReportActivity : AppCompatActivity() {

    private lateinit var spinnerIncidentType: Spinner
    private lateinit var etIncidentDescription: EditText
    private lateinit var btnTakePhoto: Button
    private lateinit var btnSubmitReport: Button

    private var currentPhotoUri: Uri? = null

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(this, getString(R.string.camera_permission_denied_toast), Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Photo captured successfully", Toast.LENGTH_SHORT).show()
            // currentPhotoUri is already set
        } else {
            currentPhotoUri = null // Reset if photo capture failed or was cancelled
            Toast.makeText(this, "Photo capture cancelled or failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_report)

        spinnerIncidentType = findViewById(R.id.spinner_incident_type)
        etIncidentDescription = findViewById(R.id.et_incident_description)
        btnTakePhoto = findViewById(R.id.btn_take_photo)
        btnSubmitReport = findViewById(R.id.btn_submit_report)

        // Populate Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.incident_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerIncidentType.adapter = adapter
        }

        btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndTakePhoto()
        }

        btnSubmitReport.setOnClickListener {
            submitReport()
        }
        title = getString(R.string.title_incident_report)
    }

    private fun checkCameraPermissionAndTakePhoto() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) -> {
                Toast.makeText(this, "Camera permission is required to attach photos.", Toast.LENGTH_LONG).show()
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: Exception) {
                    // Error occurred while creating the File
                    Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.provider",
                        it
                    )
                    currentPhotoUri = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    @Throws(java.io.IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            // currentPhotoPath = absolutePath // We'll use URI
        }
    }

    private fun submitReport() {
        val incidentType = spinnerIncidentType.selectedItem.toString()
        val description = etIncidentDescription.text.toString().trim()

        if (description.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_provide_description), Toast.LENGTH_SHORT).show()
            return
        }

        val newIncident = Incident(
            type = incidentType,
            description = description,
            timestamp = System.currentTimeMillis(),
            photoUri = currentPhotoUri?.toString(),
            // Replace with actual location if available
            latitude = 37.7749, // Dummy latitude
            longitude = -122.4194 // Dummy longitude
        )

        IncidentRepository.addIncident(newIncident)

        Toast.makeText(this, getString(R.string.report_submitted, "$incidentType"), Toast.LENGTH_LONG).show()
        finish() // Close activity after submission
    }
}