package com.example.aidm

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

/**
 * A helper extension function to format a Double to a string with a specific number of decimal places.
 * Renamed from 'format' to 'toFormattedString' to avoid conflict with String.format.
 */
private fun Double.toFormattedString(digits: Int): String {
    return "%.${digits}f".format(this)
}

@Composable
fun ReportIncidentScreen(
    // Using a ViewModel is good practice to survive configuration changes.
    sharedViewModel: SharedIncidentViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    // In a real app, you'd get the repo from a dependency injection framework.
    val repo = sharedViewModel.getRepository()

    var type by remember { mutableStateOf("Flood") }
    var desc by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf<Double?>(null) }
    var lng by remember { mutableStateOf<Double?>(null) }
    var loading by remember { mutableStateOf(false) }
    var submissionMessage by remember { mutableStateOf<String?>(null) }

    // --- Modern Permission Handling (replaces Accompanist) ---
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (!isGranted) {
                submissionMessage = "Location permission denied. Cannot get current location."
            }
        }
    )

    // Request permission if not granted when the composable enters the composition.
    // Using LaunchedEffect ensures this runs only once when the screen is first shown.
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    // --- End of Modern Permission Handling ---


    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Report Incident", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(type, { type = it }, label = { Text("Type") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            desc, { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Describe the incident...") }
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = {
                if (hasLocationPermission) {
                    // Suppress warning because we explicitly check hasLocationPermission right before this.
                    @SuppressLint("MissingPermission")
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)
                    fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                        if (loc != null) {
                            lat = loc.latitude
                            lng = loc.longitude
                            submissionMessage = null // Clear previous messages
                        } else {
                            submissionMessage = "Failed to get location: Location is null."
                        }
                    }.addOnFailureListener {
                        submissionMessage = "Failed to get location: ${it.message}"
                    }
                } else {
                    // If permission is still not granted, ask for it again.
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    submissionMessage = "Location permission not granted. Please grant permission."
                }
            }) { Text("Use Current Location") }
            Spacer(Modifier.width(8.dp))
            Text(
                // <<< FIX IS HERE: Call the renamed helper function.
                text = "Lat: ${lat?.toFormattedString(2) ?: "-"} Lng: ${lng?.toFormattedString(2) ?: "-"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                if (desc.isBlank()) {
                    submissionMessage = "Description cannot be empty."
                    return@Button
                }
                loading = true
                submissionMessage = null
                scope.launch {
                    // Use a default location if the user didn't fetch one.
                    val currentLat = lat ?: 19.0760 // Default if not set (Example: Mumbai)
                    val currentLng = lng ?: 72.8777 // Default if not set

                    val success = repo.submitIncident(type, desc, currentLat, currentLng)
                    loading = false
                    if (success) {
                        desc = "" // Clear description on success
                        lat = null // Clear location
                        lng = null
                        submissionMessage = "Incident reported successfully!"
                    } else {
                        submissionMessage = "Failed to report incident."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading // Disable button while loading
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Submit")
            }
        }
        // Display the submission status message
        submissionMessage?.let {
            Text(
                it,
                color = if (it.contains("successfully")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
