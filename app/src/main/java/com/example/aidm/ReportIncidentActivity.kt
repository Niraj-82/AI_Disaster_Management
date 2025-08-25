package com.example.aidm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.launch
import androidx.compose.animation.core.copy
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aidm.ui.theme.AIDMTheme // Assuming your theme is here
import kotlinx.coroutines.delay // For simulating submission delay

// Data class for an incident report
data class IncidentReport(
    val type: String = "",
    val description: String = "",
    val location: String = "", // Optional: could be more complex (lat/lng)
    // val mediaUri: Uri? = null // For attaching media
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
                    }
                )
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun ReportIncidentScreen(onSubmitSuccess: () -> Unit) {
    var incidentType by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var description by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var location by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") } // Optional field
    var isLoading by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var submissionError by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val incidentTypes = listOf("Fire", "Medical Emergency", "Road Accident", "Theft", "Suspicious Activity", "Other")
    var expandedIncidentTypeDropdown by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    // Simulate submission logic
    suspend fun submitReport(report: IncidentReport): Boolean {
        isLoading = true
        submissionError = null
        delay(2000) // Simulate network delay for submission

        // ** IMPORTANT: Replace this with your actual report submission logic **
        // Example: Call your ViewModel/Repository to send the report to a backend
        println("Submitting Report: $report") // Log for now
        // For this example, assume submission is always successful if fields are okay
        if (report.type.isNotBlank() && report.description.isNotBlank()) {
            isLoading = false
            return true
        } else {
            isLoading = false
            submissionError = "Incident type and description are required."
            return false
        }
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(title = { androidx.compose.material3.Text("Report an Incident") })
        },
        content = { paddingValues ->
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply padding from Scaffold
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Make content scrollable
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.Text(
                    "Please provide details about the incident.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Incident Type Dropdown
                androidx.compose.material3.ExposedDropdownMenuBox(
                    expanded = expandedIncidentTypeDropdown,
                    onExpandedChange = { expandedIncidentTypeDropdown = !expandedIncidentTypeDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    androidx.compose.material3.OutlinedTextField(
                        value = incidentType,
                        onValueChange = { /* Read Only */ },
                        readOnly = true,
                        label = { androidx.compose.material3.Text("Incident Type *") },
                        trailingIcon = { androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedIncidentTypeDropdown) },
                        modifier = Modifier
                            .menuAnchor() // Important for proper dropdown behavior
                            .fillMaxWidth(),
                        isError = submissionError != null && incidentType.isBlank()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedIncidentTypeDropdown,
                        onDismissRequest = { expandedIncidentTypeDropdown = false }
                    ) {
                        incidentTypes.forEach { type ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { androidx.compose.material3.Text(type) },
                                onClick = {
                                    incidentType = type
                                    expandedIncidentTypeDropdown = false
                                }
                            )
                        }
                    }
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { androidx.compose.material3.Text("Description *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), // Multi-line
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    isError = submissionError != null && description.isBlank(),
                    maxLines = 5
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { androidx.compose.material3.Text("Location (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    )
                )
                // TODO: Add fields for attaching media (photos/videos) - This is more complex
                // You would typically use ActivityResultLauncher for this.

                submissionError?.let {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Text(
                        text = it,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                    )
                }

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))

                androidx.compose.material3.Button(
                    onClick = {
                        if (incidentType.isNotBlank() && description.isNotBlank()) {
                            val report = IncidentReport(
                                type = incidentType,
                                description = description,
                                location = location
                            )
                            // Launch coroutine for suspend function
                            kotlinx.coroutines.GlobalScope.launch { // Use a proper scope in real app
                                if (submitReport(report)) {
                                    onSubmitSuccess()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                            submissionError = "Incident type and description are required."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        androidx.compose.material3.Text("Submit Report")
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun ReportIncidentScreenPreview() {
    AIDMTheme {
        ReportIncidentScreen(onSubmitSuccess = {})
    }
}

