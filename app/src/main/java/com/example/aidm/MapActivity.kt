package com.example.aidm

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.data.position
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.aidm.ui.theme.AIDMTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.* // Core maps-compose imports

// Example data class for map markers
data class MapMarkerInfo(
    val position: LatLng,
    val title: String,
    val snippet: String? = null,
    val iconResId: Int? = null // Optional: For custom marker icon
)

class MapActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastKnownLocation: LatLng? by androidx.compose.runtime.mutableStateOf(null)

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher. You can use either a val, as shown in this snippet,
    // or a lateinit var in your onAttach() or onCreate() method.
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app.
                Log.d("MapActivity", "Location permission granted.")
                getCurrentLocation()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.d("MapActivity", "Location permission denied.")
                // Handle the case where permission is denied (e.g., show a message)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            AIDMTheme {
                MapScreen(
                    initialUserLocation = lastKnownLocation,
                    onMapReady = {
                        // Request location permission when the map is ready
                        // (or earlier if needed for initial camera position)
                        requestLocationPermission()
                    }
                )
            }
        }
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.d("MapActivity", "Location permission already granted.")
                getCurrentLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                // TODO: Show a rationale dialog
                Log.d("MapActivity", "Showing location permission rationale.")
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) // Still ask
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                Log.d("MapActivity", "Requesting location permission.")
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission") // Suppressed because permission is checked before calling
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    lastKnownLocation = LatLng(location.latitude, location.longitude)
                    Log.d("MapActivity", "Last known location: $lastKnownLocation")
                } else {
                    Log.d("MapActivity", "Last known location is null. May need to request updates.")
                    // Handle case where location is null (e.g., location services disabled)
                    // You might want to request location updates here if lastLocation is null
                }
            }
            .addOnFailureListener { e ->
                Log.e("MapActivity", "Error getting location", e)
                // Handle failure to get location
            }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class) // For TopAppBar
@androidx.compose.runtime.Composable
fun MapScreen(
    initialUserLocation: LatLng?,
    onMapReady: () -> Unit
) {
    val context = LocalContext.current

    // Default camera position (e.g., a central point or last known location)
    val defaultCity = LatLng(34.0522, -118.2437) // Los Angeles
    val cameraInitialPosition = initialUserLocation ?: defaultCity

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cameraInitialPosition, 10f)
    }

    // Example list of markers - replace with your actual data source
    val markers = androidx.compose.runtime.remember {
        listOf(
            MapMarkerInfo(LatLng(34.0522, -118.2437), "Los Angeles", "City of Angels"),
            MapMarkerInfo(LatLng(34.0000, -118.3000), "South LA", "Near USC", R.drawable.ic_launcher_foreground), // Example with custom icon
            MapMarkerInfo(LatLng(33.9416, -118.4085), "LAX Airport", "Los Angeles International")
        )
    }

    // Effect to move camera when initialUserLocation updates and is valid
    androidx.compose.runtime.LaunchedEffect(initialUserLocation) {
        if (initialUserLocation != null) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(initialUserLocation, 15f), // Zoom closer for user location
                durationMs = 1000
            )
        }
    }

    // Effect to call onMapReady once when the composable enters composition
    androidx.compose.runtime.LaunchedEffect(Unit) {
        onMapReady()
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(title = { androidx.compose.material3.Text("Map View") })
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(onClick = {
                initialUserLocation?.let {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(it, 15f),
                        durationMs = 1000
                    )
                }
            }) {
                androidx.compose.material3.Icon(Icons.Filled.MyLocation, "Center on my location")
            }
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            com.google.android.gms.maps.GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = initialUserLocation != null, // Enable only if location is known
                    mapType = MapType.NORMAL, // Other types: SATELLITE, HYBRID, TERRAIN
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = false // We use our own FAB
                    )
                ),
                onMapLoaded = {
                    Log.d("MapScreen", "Map has loaded.")
                    // You can perform actions once the map tiles are loaded
                },
                onPOIClick = { poi ->
                    Toast.makeText(context, "Clicked: ${poi.name}", Toast.LENGTH_SHORT).show()
                }
            ) {
                // Add Markers, Polylines, Polygons, etc. here
                markers.forEach { markerInfo ->
                    com.google.android.gms.maps.model.Marker(
                        state = MarkerState(position = markerInfo.position),
                        title = markerInfo.title,
                        snippet = markerInfo.snippet,
                        icon = markerInfo.iconResId?.let {
                            BitmapDescriptorFactory.fromResource(it)
                        } // Custom icon if provided
                        // onClick = { marker -> ... handle marker click ...; true }
                    )
                }

                // Example Polyline
                com.google.android.gms.maps.model.Polyline(
                    points = listOf(markers.first().position, markers.last().position),
                    color = androidx.compose.ui.graphics.Color.Blue,
                    width = 5f
                )
            }
        }
    }
}

@Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun MapScreenPreview() {
    AIDMTheme {
        // Preview might not fully render GoogleMap without API key and Google Play Services
        // For preview, you might show a placeholder or a simplified version
        MapScreen(initialUserLocation = LatLng(34.0522, -118.2437), onMapReady = {})
    }
}
