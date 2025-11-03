package com.example.resqai

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.resqai.db.DatabaseHelper
import com.example.resqai.model.Announcement
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.Date

class SOSActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var firestore: FirebaseFirestore
    private val PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        dbHelper = DatabaseHelper(this)
        firestore = FirebaseFirestore.getInstance()

        val sosButton: Button = findViewById(R.id.button_sos)
        sosButton.setOnClickListener {
            checkPermissionsAndSendSos()
        }
    }

    private fun checkPermissionsAndSendSos() {
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)

        val permissionsToRequest = mutableListOf<String>()
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (smsPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.SEND_SMS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            sendSosMessageToContacts()
        }
    }

    private fun sendSosMessageToContacts() {
        val emergencyContacts = dbHelper.getAllEmergencyContacts()

        if (emergencyContacts.isEmpty()) {
            Toast.makeText(this, "No emergency contacts found. Please add contacts first.", Toast.LENGTH_LONG).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // This check is technically redundant, but good practice
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val message = "SOS! I need help! My current location is: https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
                    try {
                        val smsManager = getSystemService(SmsManager::class.java)
                        var messagesSent = 0
                        for (contact in emergencyContacts) {
                            smsManager.sendTextMessage(contact.phoneNumber, null, message, null, null)
                            messagesSent++
                        }
                        Toast.makeText(this, "SOS message sent to $messagesSent contact(s).", Toast.LENGTH_LONG).show()
                        saveSosAsAnnouncement(location)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Failed to send SOS message.", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "Could not determine location. Please ensure location services are enabled.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location.", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveSosAsAnnouncement(location: Location) {
        val announcement = Announcement(
            title = "SOS Alert",
            message = "A user has triggered an SOS alert.",
            timestamp = Date().time,
            location = GeoPoint(location.latitude, location.longitude)
        )

        firestore.collection("announcements")
            .add(announcement)
            .addOnSuccessListener {
                // Optionally, confirm that the announcement was saved
            }
            .addOnFailureListener {
                // Optionally, handle the failure to save the announcement
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                sendSosMessageToContacts()
            } else {
                Toast.makeText(this, "Permissions denied. Cannot send SOS.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
