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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class SosActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_REQUEST_CODE = 101
    private val EMERGENCY_PHONE_NUMBER = "1234567890" // Replace with a real emergency contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
            sendSosMessage()
        }
    }

    private fun sendSosMessage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // This check is technically redundant due to the initial permission check, but good practice
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val message = "SOS! I need help! My current location is: https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}"
                    try {
                        val smsManager = SmsManager.getDefault()
                        smsManager.sendTextMessage(EMERGENCY_PHONE_NUMBER, null, message, null, null)
                        Toast.makeText(this, "SOS message sent to $EMERGENCY_PHONE_NUMBER", Toast.LENGTH_LONG).show()
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
                sendSosMessage()
            } else {
                Toast.makeText(this, "Permissions denied. Cannot send SOS.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
