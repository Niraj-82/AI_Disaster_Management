package com.example.aidm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fabSos: FloatingActionButton
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncherSos =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocationAndConfirmSos()
            } else {
                Toast.makeText(this, "Location permission denied. Cannot send SOS with location.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        fabSos = findViewById(R.id.fab_sos)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupBottomNavigation()
        setupSosButton()

        // Set default fragment
        if (savedInstanceState == null) {
            // Check if the intent has a specific fragment to load
            val fragmentToLoad = intent.getStringExtra("LOAD_FRAGMENT")
            if (fragmentToLoad == "PROFILE") {
                 bottomNavigationView.selectedItemId = R.id.nav_profile
            } else {
                bottomNavigationView.selectedItemId = R.id.nav_report_incident // Default
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_report_incident -> selectedFragment = ReportIncidentFragment()
                R.id.nav_first_aid -> selectedFragment = FirstAidFragment()
                R.id.nav_shelters -> selectedFragment = ShelterListFragment()
                R.id.nav_announcements -> selectedFragment = AnnouncementsFragment()
                R.id.nav_profile -> selectedFragment = ProfileFragment()
            }
            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    // .addToBackStack(null) // Optional: if you want to add to back stack
                    .commit()
            }
            true
        }
    }

    private fun setupSosButton() {
        fabSos.setOnClickListener {
            checkLocationPermissionAndInitiateSos()
        }
    }

    private fun checkLocationPermissionAndInitiateSos() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocationAndConfirmSos()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Show an explanation to the user *asynchronously*
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission for SOS to share your current location with emergency contacts. Please grant the permission.")
                    .setPositiveButton("OK") { _, _ ->
                        requestPermissionLauncherSos.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                        Toast.makeText(this, "SOS cannot be sent without location permission.", Toast.LENGTH_SHORT).show()
                    }
                    .create()
                    .show()
            }
            else -> {
                requestPermissionLauncherSos.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocationAndConfirmSos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // This should ideally not be reached if checkLocationPermissionAndInitiateSos is called first
            Toast.makeText(this, "Location permission missing.", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Getting current location for SOS...", Toast.LENGTH_SHORT).show()
        // Use PRIORITY_HIGH_ACCURACY for SOS.
        // A CancellationToken is good practice for getCurrentLocation.
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    Log.d("HomeActivitySOS", "Current location for SOS: $userLocation")
                    showSosConfirmationDialog(userLocation)
                } else {
                    Toast.makeText(this, "Failed to get current location for SOS. Try again.", Toast.LENGTH_LONG).show()
                    Log.e("HomeActivitySOS", "FusedLocationProvider returned null location.")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting location: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("HomeActivitySOS", "Error getting location", e)
            }
    }

    private fun showSosConfirmationDialog(location: LatLng) {
        AlertDialog.Builder(this)
            .setTitle("Confirm SOS Activation")
            .setMessage("Are you sure you want to send an SOS alert with your current location (${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)})?")
            .setPositiveButton("SEND SOS") { _, _ ->
                sendSosAlert(location)
            }
            .setNegativeButton("Cancel", null)
            .setIcon(R.drawable.ic_sos_default) // Optional: Show SOS icon in dialog
            .show()
    }

    private fun sendSosAlert(location: LatLng) {
        // Placeholder for actual SOS sending logic
        // This is where you'd implement SMS or Backend API call
        Log.i("HomeActivitySOS", "SOS Alert Triggered! Location: ${location.latitude}, ${location.longitude}")
        Toast.makeText(this, "SOS Alert Sent (Simulated) from ${location.latitude}, ${location.longitude}", Toast.LENGTH_LONG).show()

        // --- SMS Example (Conceptual - requires SEND_SMS permission and handling) ---
        // val smsManager = SmsManager.getDefault()
        // val emergencyContactNumber = "YOUR_EMERGENCY_CONTACT_NUMBER" // Load from prefs or constant
        // val message = "SOS! I need help at: https://maps.google.com/?q=${location.latitude},${location.longitude}"
        // try {
        //     smsManager.sendTextMessage(emergencyContactNumber, null, message, null, null)
        //     Toast.makeText(this, "SOS SMS actually sent to $emergencyContactNumber", Toast.LENGTH_LONG).show()
        // } catch (e: Exception) {
        //     Toast.makeText(this, "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
        //     Log.e("HomeActivitySOS", "SMS sending failed", e)
        // }

        // --- Backend API Example (Conceptual - requires Retrofit setup for this specific call) ---
        // CoroutineScope(Dispatchers.IO).launch {
        //     try {
        //         val userId = "current_user_id_placeholder" // Get this from your auth system
        //         // val request = SosRequest(userId, location.latitude, location.longitude, System.currentTimeMillis())
        //         // val response = yourMainApiService.sendSosSignal(request) // Assuming an ApiService instance
        //         // withContext(Dispatchers.Main) {
        //         //     if (response.isSuccessful) {
        //         //         Toast.makeText(this@HomeActivity, "SOS signal sent to server.", Toast.LENGTH_LONG).show()
        //         //     } else {
        //         //         Toast.makeText(this@HomeActivity, "Failed to send SOS to server: ${response.code()}", Toast.LENGTH_LONG).show()
        //         //     }
        //         // }
        //          withContext(Dispatchers.Main) { // Simulate network delay then success
        //              kotlinx.coroutines.delay(1000)
        //              Toast.makeText(this@HomeActivity, "SOS signal reported to server (Simulated).", Toast.LENGTH_LONG).show()
        //          }
        //     } catch (e: Exception) {
        //         withContext(Dispatchers.Main) {
        //             Toast.makeText(this@HomeActivity, "SOS network error: ${e.message}", Toast.LENGTH_LONG).show()
        //             Log.e("HomeActivitySOS", "SOS API call failed", e)
        //         }
        //     }
        // }
    }
}
