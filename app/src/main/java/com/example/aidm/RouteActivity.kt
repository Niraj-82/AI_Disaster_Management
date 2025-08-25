package com.example.aidm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.weight
import com.example.aidm.ui.theme.AIDMTheme // Assuming your theme is here
import kotlinx.coroutines.delay // For simulating data loading

// Placeholder data classes
data class RouteDetails(
    val origin: String,
    val destination: String,
    val distance: String,
    val duration: String,
    val steps: List<RouteStep> = emptyList(),
    // In a real app, you might have LatLng for origin/destination
    // val originLatLng: LatLng,
    // val destinationLatLng: LatLng,
)

data class RouteStep(
    val instruction: String,
    val distance: String
)

class RouteActivity : ComponentActivity() {

    companion object {
        const val EXTRA_ORIGIN = "com.example.aidm.EXTRA_ORIGIN"
        const val EXTRA_DESTINATION = "com.example.aidm.EXTRA_DESTINATION"
        // You might pass a full route ID or serialized RouteDetails object too
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

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun RouteScreen(originName: String, destinationName: String) {
    var routeDetails by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<RouteDetails?>(null) }
    var isLoading by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
    var error by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Simulate fetching route details
    androidx.compose.runtime.LaunchedEffect(originName, destinationName) {
        isLoading = true
        error = null
        delay(1500) // Simulate network/calculation delay

        // ** IMPORTANT: Replace this with your actual route calculation/fetching logic **
        // This would involve calling a Directions API (e.g., Google Directions API)
        // or a routing engine.
        try {
            if (originName != "Unknown Origin" && destinationName != "Unknown Destination") {
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
                error = "Origin or Destination is missing."
            }
        } catch (e: Exception) {
            error = "Failed to calculate route: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(title = { androidx.compose.material3.Text("Route Details") })
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ** 1. MAP VIEW PLACEHOLDER **
            // In a real app, this Box would contain your Map Composable
            // (e.g., GoogleMap(), or a custom map view)
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Adjust height as needed
                    .padding(8.dp)
                    .then(
                        if (isLoading || error != null || routeDetails == null) {
                            Modifier.align(Alignment.CenterHorizontally) // Center placeholder text
                        } else {
                            Modifier // No extra alignment if map would be shown
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> androidx.compose.material3.CircularProgressIndicator()
                    error != null -> androidx.compose.material3.Text(error!!, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                    routeDetails != null -> androidx.compose.material3.Text(
                        "Map showing route from\n${routeDetails!!.origin}\nto\n${routeDetails!!.destination}",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    else -> androidx.compose.material3.Text("Map View Area")
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))

            // ** 2. ROUTE INFORMATION & DIRECTIONS **
            if (routeDetails != null && error == null && !isLoading) {
                val details = routeDetails!!
                androidx.compose.foundation.layout.Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        androidx.compose.material3.Icon(Icons.Filled.Place, contentDescription = "Origin", tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                        androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
                        androidx.compose.material3.Text(details.origin, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        androidx.compose.material3.Icon(Icons.Filled.ArrowForward, contentDescription = "To", modifier = Modifier.size(18.dp))
                    }
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        androidx.compose.material3.Icon(Icons.Filled.Place, contentDescription = "Destination", tint = Color.Red) // Example different color
                        androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
                        androidx.compose.material3.Text(details.destination, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material3.Text("Distance: ${details.distance}", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
                    androidx.compose.material3.Text("Estimated Duration: ${details.duration}", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

                    if (details.steps.isNotEmpty()) {
                        androidx.compose.material3.Text("Directions:", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
                        LazyColumn(
                            modifier = Modifier.weight(1f) // Takes remaining space
                        ) {
                            items(details.steps) { step ->
                                RouteStepItem(step)
                                androidx.compose.material3.Divider()
                            }
                        }
                    }
                }
            } else if (!isLoading && error == null) {
                androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.Text("No route details to display.")
                }
            }

            // TODO: Add buttons for "Start Navigation", "Recenter Map", etc.
            // Example:
            // if (routeDetails != null && error == null && !isLoading) {
            //     Button(onClick = { /* TODO: Launch external navigation or in-app navigation */ },
            //            modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            //         Text("Start Navigation")
            //     }
            // }
        }
    }
}

@androidx.compose.runtime.Composable
fun RouteStepItem(step: RouteStep) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        androidx.compose.material3.Text(
            text = step.instruction,
            modifier = Modifier.weight(1f),
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
        androidx.compose.foundation.layout.Spacer(Modifier.width(16.dp))
        androidx.compose.material3.Text(
            text = step.distance,
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun RouteScreenPreview() {
    AIDMTheme {
        RouteScreen(originName = "123 Main St, Anytown", destinationName = "Central Park, Big City")
    }
}

@Preview(showBackground = true, name="Route Screen Loading")
@androidx.compose.runtime.Composable
fun RouteScreenLoadingPreview() {
    AIDMTheme {
        // Simulate loading state for preview
        val loadingRouteScreen: @androidx.compose.runtime.Composable () -> Unit = {
            var isLoading by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
            androidx.compose.runtime.LaunchedEffect(Unit) {
                delay(2000) // Keep it loading for preview
                // isLoading = false // Don't set to false to keep previewing loading
            }
            if(isLoading) {
                androidx.compose.foundation.layout.Box(modifier=Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    androidx.compose.material3.CircularProgressIndicator()
                }
            }
        }
        loadingRouteScreen()
    }
}
