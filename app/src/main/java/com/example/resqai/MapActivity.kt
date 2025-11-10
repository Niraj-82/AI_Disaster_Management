package com.example.resqai

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.resqai.model.Shelter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.google.android.gms.location.Priority

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Modern way to handle permission requests
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Get the location.
                getCurrentLocationAndZoom()
            } else {
                // Explain to the user that the feature is unavailable
                Toast.makeText(this, "Location permission denied. Cannot show current location.", Toast.LENGTH_LONG).show()
                // Optionally, set a default location
                mapView.controller.setZoom(9.0)
                mapView.controller.setCenter(GeoPoint(51.5074, -0.1278)) // Default to London
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize osmdroid configuration
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))

        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val shelter = intent.getParcelableExtra<Shelter>("shelter")

        if (shelter != null && shelter.latitude != null && shelter.longitude != null) {
            // If a shelter is provided, center on it
            val shelterPoint = GeoPoint(shelter.latitude!!, shelter.longitude!!)
            mapView.controller.setZoom(15.0)
            mapView.controller.setCenter(shelterPoint)

            val shelterMarker = Marker(mapView)
            shelterMarker.position = shelterPoint
            shelterMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            shelterMarker.title = shelter.name
            mapView.overlays.add(shelterMarker)
        } else {
            // Otherwise, try to center on the user's current location
            checkLocationPermissionAndGetLocation()
        }
    }

    private fun checkLocationPermissionAndGetLocation() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                getCurrentLocationAndZoom()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected.
                // For now, we'll just request the permission.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                // Directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocationAndZoom() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Use the FusedLocationProviderClient to get the current location
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val userLocation = GeoPoint(location.latitude, location.longitude)
                        mapView.controller.setZoom(16.0)
                        mapView.controller.setCenter(userLocation)

                        // Add a marker for the user's location
                        val userMarker = Marker(mapView)
                        userMarker.position = userLocation
                        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        userMarker.title = "My Location"
                        // Optional: use a different icon for the user
                        // userMarker.icon = resources.getDrawable(R.drawable.ic_user_location, theme)
                        mapView.overlays.add(userMarker)
                        mapView.invalidate() // Refresh the map
                    } else {
                        Toast.makeText(this, "Could not get current location.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
