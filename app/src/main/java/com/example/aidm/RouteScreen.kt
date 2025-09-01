package com.example.aidm

import android.os.Bundle // Keep for potential future use in Activity
import androidx.activity.ComponentActivity // Keep for potential future use in Activity
import androidx.activity.compose.setContent // Keep for potential future use in Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.platform.LocalContext // Not used in this version
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aidm.AIDMTheme // Ensure this path is correct for your theme
import kotlinx.coroutines.delay

// Data classes
data class RouteDetails(
    val origin: String,
    val destination: String,
    val distance: String,
    val duration: String,
    val steps: List<RouteStep> = emptyList()
)

data class RouteStep(
    val instruction: String,
    val distance: String
)

/*
// If RouteActivity is defined in a separate file, this can be removed from here
// or kept if this file is meant to be self-contained for RouteScreen demonstration.
class RouteActivity : ComponentActivity() {
    companion object {
        const val EXTRA_ORIGIN = "com.example.aidm.EXTRA_ORIGIN"
        const val EXTRA_DESTINATION = "com.example.aidm.EXTRA_DESTINATION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val origin = intent.getStringExtra(EXTRA_ORIGIN) ?: "Unknown Origin"
        val destination = intent.getStringExtra(EXTRA_DESTINATION) ?: "Unknown Destination"
        setContent {
            AIDMTheme {
                RouteScreen(originName = origin, destinationName = destination)
            }
        }
    }
}
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteScreen(originName: String, destinationName: String) {
    var routeDetails by remember { mutableStateOf<RouteDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(originName, destinationName) {
        isLoading = true
        error = null
        delay(1500) // Simulate network/calculation delay
        try {
            if (originName.isNotBlank() && originName != "Unknown Origin" &&
                destinationName.isNotBlank() && destinationName != "Unknown Destination") {
                // Simulate successful route calculation
                routeDetails = RouteDetails(
                    origin = originName,
                    destination = destinationName,
                    distance = "10.5 km",
                    duration = "25 mins",
                    steps = listOf(
                        RouteStep("Head north on Main St", "1.2 km"),
                        RouteStep("Turn right onto Oak Ave", "3.0 km"),
                        RouteStep("Continue on Highway 101", "5.8 km"),
                        RouteStep("Arrive at $destinationName", "0.5 km")
                    )
                )
            } else {
                error = "Origin or Destination is invalid or missing."
            }
        } catch (e: Exception) {
            error = "Failed to calculate route: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Route Details") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> CircularProgressIndicator()
                    error != null -> Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    routeDetails != null -> Text(
                        text = "Map showing route from\n${routeDetails!!.origin}\nto\n${routeDetails!!.destination}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    else -> Text(
                        text = "Map View Area",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!isLoading && error == null && routeDetails != null) {
                val details = routeDetails!!
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Place, contentDescription = "Origin", tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text(details.origin, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "To", modifier = Modifier.size(18.dp))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Place, contentDescription = "Destination", tint = Color.Red)
                        Spacer(Modifier.width(8.dp))
                        Text(details.destination, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Distance: ${details.distance}", style = MaterialTheme.typography.bodyLarge)
                    Text("Estimated Duration: ${details.duration}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (details.steps.isNotEmpty()) {
                        Text("Directions:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(details.steps) { step ->
                                RouteStepItem(step)
                                Divider()
                            }
                        }
                    }
                }
            } else if (!isLoading && error != null) {
                // Error message is already displayed in the map Box area
            } else if (!isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No route details available.")
                }
            }
        }
    }
}

@Composable
fun RouteStepItem(step: RouteStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = step.instruction,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = step.distance,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, name = "Route Screen Success")
@Composable
fun RouteScreenSuccessPreview() {
    AIDMTheme {
        // This preview shows the initial state before LaunchedEffect or with predefined data.
        // For a true "success" preview with data, you'd make RouteScreen accept RouteDetails as a parameter.
        RouteScreen(originName = "123 Main St, Anytown", destinationName = "Central Park, Big City")
    }
}

@Preview(showBackground = true, name="Route Screen Loading")
@Composable
fun RouteScreenLoadingPreview() {
    AIDMTheme {
        // This preview shows the initial state which should be loading.
        RouteScreen(originName = "Loading Origin", destinationName = "Loading Destination")
    }
}

@Preview(showBackground = true, name="Route Screen Error")
@Composable
fun RouteScreenErrorPreview() {
    AIDMTheme {
        // This triggers the error state due to invalid/blank origin/destination.
        RouteScreen(originName = "", destinationName = "")
    }
}
