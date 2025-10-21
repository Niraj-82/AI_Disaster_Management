package com.example.resqai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resqai.model.Shelter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewSheltersActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: Location? = null
    private lateinit var shelterAdapter: ShelterAdapter
    private val shelters = mutableListOf<Shelter>()
    private var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_shelters)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkUserRole()

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_shelters)
        recyclerView.layoutManager = LinearLayoutManager(this)
        shelterAdapter = ShelterAdapter(shelters, userLocation, isAdmin) { shelter ->
            if (isAdmin) {
                showEditCapacityDialog(shelter)
            }
        }
        recyclerView.adapter = shelterAdapter

        val fabAddShelter: FloatingActionButton = findViewById(R.id.fab_add_shelter)
        fabAddShelter.setOnClickListener {
            startActivity(Intent(this, AddShelterActivity::class.java))
        }

        if (isAdmin) {
            fabAddShelter.visibility = android.view.View.VISIBLE
        }

        if (checkLocationPermission()) {
            getCurrentLocationAndFetchShelters()
        } else {
            requestLocationPermission()
        }
    }

    private fun checkUserRole() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null) {
                val role = document.getString("role")
                isAdmin = role == "admin"
                shelterAdapter.notifyDataSetChanged() // Re-bind adapter with correct admin status
            }
        }
    }

    private fun showEditCapacityDialog(shelter: Shelter) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Capacity")

        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.setText(shelter.capacity.toString())
        builder.setView(input)

        builder.setPositiveButton("Save") { _, _ ->
            val newCapacity = input.text.toString().toLongOrNull()
            if (newCapacity != null) {
                updateShelterCapacity(shelter, newCapacity)
            } else {
                Toast.makeText(this, "Invalid capacity", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun updateShelterCapacity(shelter: Shelter, newCapacity: Long) {
        val db = FirebaseFirestore.getInstance()
        shelter.id?.let {
            db.collection("shelters").document(it).update("capacity", newCapacity)
                .addOnSuccessListener {
                    Toast.makeText(this, "Capacity updated successfully", Toast.LENGTH_SHORT).show()
                    fetchShelters() // Refresh the list
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating capacity: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getCurrentLocationAndFetchShelters() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = location
                    shelterAdapter.userLocation = location
                    fetchShelters()
                } else {
                    Toast.makeText(this, "Could not determine location. Showing all shelters.", Toast.LENGTH_SHORT).show()
                    fetchShelters()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to get location.", Toast.LENGTH_SHORT).show()
                fetchShelters()
            }
        }
    }

    private fun fetchShelters() {
        val db = FirebaseFirestore.getInstance()
        db.collection("shelters").get().addOnSuccessListener { result ->
            val shelterList = result.documents.mapNotNull { document ->
                val shelter = document.toObject(Shelter::class.java)
                shelter?.id = document.id
                shelter
            }

            val availableShelters = shelterList.filter { it.status != "Full" }.toMutableList()

            if (userLocation != null) {
                availableShelters.sortBy { shelter ->
                    if (shelter.latitude != null && shelter.longitude != null) {
                        val shelterLocation = Location("").apply {
                            latitude = shelter.latitude
                            longitude = shelter.longitude
                        }
                        userLocation!!.distanceTo(shelterLocation)
                    } else {
                        Float.MAX_VALUE
                    }
                }
            }

            shelters.clear()
            shelters.addAll(availableShelters)
            shelterAdapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error getting shelters: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocationAndFetchShelters()
            } else {
                Toast.makeText(this, "Permission denied. Cannot sort shelters by distance.", Toast.LENGTH_SHORT).show()
                fetchShelters()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        fetchShelters()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
