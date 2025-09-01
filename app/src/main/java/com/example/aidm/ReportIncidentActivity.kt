package com.example.aidm // Or your package name

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.aidm.AIDMTheme // Your app's theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Data class for an incident report
data class IncidentReportData(
    val type: String = "",
    val description: String = "",
    val location: String = "",
    val mediaUri: Uri? = null
)

class ReportIncidentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDMTheme {
                ReportIncidentScreen(
                    onSubmitSuccess = {
                        Toast.makeText(this, "Incident reported successfully!", Toast.LENGTH_LONG).show()
                        finish() // Close the activity after successful submission
                    },
                    onCancel = {
                        finish() // Close the activity if user cancels
                    }
                )
            }
        }
    }
}

// Helper to create a Uri for storing an image from the camera
fun Context.createImageUri(): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = File(cacheDir, "images") // Using cacheDir/images/ as defined in file_paths.xml
    if (!storageDir.exists()) storageDir.mkdirs()
    val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
    return FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", imageFile)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIncidentScreen(onSubmitSuccess: () -> Unit, onCancel: () -> Unit) {
    var incidentType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) } // For attached media

    var isLoading by remember { mutableStateOf(false) }
    var submissionError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val incidentTypes = listOf("Fire", "Medical Emergency", "Road Accident", "Theft", "Suspicious Activity", "Other")
    var expandedIncidentTypeDropdown by remember { mutableStateOf(false) }

    // --- Image Pickers ---
    var tempImageUriForCamera: Uri? = null // To hold URI before camera app returns

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success: Boolean ->
            if (success) {
                imageUri = tempImageUriForCamera // Use the URI we provided to the camera
                Log.d("ReportIncident", "Image taken successfully: $imageUri")
            } else {
                Log.e("ReportIncident", "Image capture failed or was cancelled.")
                tempImageUriForCamera = null // Clear if capture failed
            }
        }
    )

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
            Log.d("ReportIncident", "Image picked successfully: $imageUri")
        }
    )

    // --- Permission Launcher (Example for Camera) ---
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                tempImageUriForCamera = context.createImageUri() // Create URI before launching camera
                tempImageUriForCamera?.let { uri ->
                    takePictureLauncher.launch(uri)
                } ?: Log.e("ReportIncident", "Could not create URI for camera image.")
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // --- Submission Logic (Placeholder) ---
    suspend fun submitReport(report: IncidentReportData): Boolean {
        isLoading = true
        submissionError = null
        delay(2000) // Simulate network delay

        // ** IMPORTANT: Replace this with your actual report submission logic **
        // Example: Upload image if imageUri is not null, then send report data
        Log.i("ReportIncident", "Submitting Report: $report")
        // For this example, assume submission is successful if fields are okay
        if (report.type.isNotBlank() && report.description.isNotBlank()) {
            isLoading = false
            return true
        } else {
            isLoading = false
            submissionError = "Incident type and description are required."
            return false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report an Incident") },
                navigationIcon = {
                    // IconButton(onClick = onCancel) { Icon(Icons.Filled.ArrowBack, "Back") } // Optional back
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Please provide details about the incident.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Incident Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedIncidentTypeDropdown,
                    onExpandedChange = { expandedIncidentTypeDropdown = !expandedIncidentTypeDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = incidentType,
                        onValueChange = { /* Read Only */ },
                        readOnly = true,
                        label = { Text("Incident Type *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedIncidentTypeDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        isError = submissionError != null && incidentType.isBlank()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedIncidentTypeDropdown,
                        onDismissRequest = { expandedIncidentTypeDropdown = false }
                    ) {
                        incidentTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    incidentType = type
                                    expandedIncidentTypeDropdown = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description *") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp, max = 200.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    isError = submissionError != null && description.isBlank(),
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- Media Attachment UI ---
                Text("Attach Media (Optional)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        // Check for camera permission before launching
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                tempImageUriForCamera = context.createImageUri()
                                tempImageUriForCamera?.let { uri ->
                                    takePictureLauncher.launch(uri)
                                } ?: Log.e("ReportIncident", "Could not create URI for camera image.")
                            }
                            else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = "Take Picture", modifier = Modifier.size(ButtonDefaults.IconSize))
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Camera")
                    }
                    Button(onClick = {
                        pickImageLauncher.launch("image/*") // Launch gallery picker
                    }) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = "Pick from Gallery", modifier = Modifier.size(ButtonDefaults.IconSize))
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Gallery")
                    }
                }

                imageUri?.let { uri ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Attached Image:")
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = "Attached image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable {
                                // Optional: Allow user to remove image or view larger
                                // imageUri = null // Example to remove
                            },
                        contentScale = ContentScale.Fit
                    )
                }

                submissionError?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.weight(1f)) // Push buttons to bottom if content is short

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.End // Or Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onCancel, enabled = !isLoading) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (incidentType.isNotBlank() && description.isNotBlank()) {
                                val report = IncidentReportData(
                                    type = incidentType,
                                    description = description,
                                    location = location,
                                    mediaUri = imageUri
                                )
                                coroutineScope.launch {
                                    if (submitReport(report)) {
                                        onSubmitSuccess()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                                submissionError = "Incident type and description are required."
                            }
                        },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Submit Report")
                        }
                    }
                }
            }
        }
    )
}

