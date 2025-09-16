package com.example.resqai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SafePathActivity : AppCompatActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Dummy shelter data for now - replace with actual data source later
    private val shelters = listOf(
        Shelter("Community Hall", LatLng(37.7749, -122.4194), 100, "Food, Water"), // San Francisco
        Shelter("Local School", LatLng(37.7759, -122.4294), 150, "Medical Aid, Blankets"),
        Shelter("City Shelter", LatLng(34.0522, -118.2437), 200, "Food, Water, Pets Allowed") // Los Angeles
    )

    // Replace with your actual Shelter data model if different
    data class Shelter(val name: String, val location: LatLng, val capacity: Int, val supplies: String)

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                getCurrentLocationAndSetupMap()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                getCurrentLocationAndSetupMap()
            }
            else -> {
                // No location access granted.
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()
                // Fallback: Show default map view centered on a generic location
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(0.0, 0.0), 2f))
                displayShelterMarkers()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safe_path)
        title = "Find Safe Path to Shelter"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment_container) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        checkLocationPermissionAndSetupMap()
    }

    private fun checkLocationPermissionAndSetupMap() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Location permission is already granted
                getCurrentLocationAndSetupMap()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Explain why you need the permission
                Toast.makeText(this, "Location permission is needed to show your current location and find nearby shelters.", Toast.LENGTH_LONG).show()
                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
            else -> {
                // Directly request for the permission
                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
        }
    }

    private fun getCurrentLocationAndSetupMap() {
        try {
            googleMap?.isMyLocationEnabled = true
            googleMap?.uiSettings?.isMyLocationButtonEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f)) // Zoom level 12
                } else {
                    // Could not get location, show default
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(0.0, 0.0), 2f))
                    Toast.makeText(this, "Could not retrieve current location. Showing default map.", Toast.LENGTH_SHORT).show()
                }
                displayShelterMarkers() // Display shelters after attempting to get location
            }.addOnFailureListener {
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(0.0, 0.0), 2f))
                Toast.makeText(this, "Failed to get current location. Showing default map.", Toast.LENGTH_SHORT).show()
                displayShelterMarkers()
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Location permission error: ${e.message}", Toast.LENGTH_LONG).show()
            displayShelterMarkers() // Still display shelters
        }
    }

    private fun displayShelterMarkers() {
        shelters.forEach { shelter ->
            googleMap?.addMarker(
                MarkerOptions()
                    .position(shelter.location)
                    .title(shelter.name)
                    .snippet("Capacity: ${shelter.capacity}, Supplies: ${shelter.supplies}")
            )
        }
    }
}
