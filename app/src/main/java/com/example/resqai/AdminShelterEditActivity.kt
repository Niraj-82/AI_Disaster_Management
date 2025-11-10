package com.example.resqai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.resqai.model.Shelter
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AdminShelterEditActivity : AppCompatActivity() {

    private lateinit var etShelterName: EditText
    private lateinit var etShelterAddress: EditText
    private lateinit var etShelterCapacity: EditText
    private lateinit var etShelterCurrentOccupancy: EditText
    private lateinit var etShelterSupplies: EditText
    private lateinit var etShelterContactInfo: EditText
    private lateinit var btnSelectLocation: Button
    private lateinit var tvSelectedLocation: TextView
    private lateinit var btnSaveShelter: Button
    private lateinit var btnDeleteShelter: Button

    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

    private var currentShelterId: String? = null
    private lateinit var db: FirebaseFirestore

    private val mapPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedLatitude = data?.getDoubleExtra("latitude", 0.0)
            selectedLongitude = data?.getDoubleExtra("longitude", 0.0)
            tvSelectedLocation.text = "Lat: $selectedLatitude, Lon: $selectedLongitude"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_shelter_edit)

        db = FirebaseFirestore.getInstance()

        etShelterName = findViewById(R.id.etShelterName)
        etShelterAddress = findViewById(R.id.etShelterAddress)
        etShelterCapacity = findViewById(R.id.etShelterCapacity)
        etShelterCurrentOccupancy = findViewById(R.id.etShelterCurrentOccupancy)
        etShelterSupplies = findViewById(R.id.etShelterSupplies)
        etShelterContactInfo = findViewById(R.id.etShelterContactInfo)
        btnSelectLocation = findViewById(R.id.btnSelectLocation)
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation)
        btnSaveShelter = findViewById(R.id.btnSaveShelter)
        btnDeleteShelter = findViewById(R.id.btnDeleteShelter)

        currentShelterId = intent.getStringExtra("SHELTER_ID")

        if (currentShelterId != null) {
            title = "Edit Shelter"
            btnDeleteShelter.visibility = View.VISIBLE
            loadShelterData(currentShelterId!!)
        } else {
            title = "Add New Shelter"
            btnDeleteShelter.visibility = View.GONE
        }

        btnSelectLocation.setOnClickListener {
            val intent = Intent(this, MapPickerActivity::class.java)
            mapPickerLauncher.launch(intent)
        }

        btnSaveShelter.setOnClickListener { saveShelter() }
        btnDeleteShelter.setOnClickListener { deleteShelter() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_shelter_edit_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadShelterData(shelterId: String) {
        db.collection("shelters").document(shelterId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val shelter = document.toObject(Shelter::class.java)
                    shelter?.let {
                        etShelterName.setText(it.name)
                        etShelterAddress.setText(it.address)
                        etShelterCapacity.setText(it.capacity.toString())
                        etShelterCurrentOccupancy.setText(it.currentOccupancy.toString())
                        etShelterSupplies.setText(it.supplies)
                        etShelterContactInfo.setText(it.contactInfo)

                        selectedLatitude = it.latitude
                        selectedLongitude = it.longitude
                        if (selectedLatitude != null && selectedLongitude != null) {
                            tvSelectedLocation.text = "Lat: $selectedLatitude, Lon: $selectedLongitude"
                        } else {
                            tvSelectedLocation.text = "No location selected"
                        }
                    }
                } else {
                    Toast.makeText(this, "Shelter not found.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading shelter: ${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
    }

    private fun saveShelter() {
        if (etShelterName.text.toString().trim().isEmpty() ||
            etShelterAddress.text.toString().trim().isEmpty() ||
            etShelterCapacity.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Name, Address, and Capacity are required.", Toast.LENGTH_SHORT).show()
            return
        }

        val name = etShelterName.text.toString().trim()
        val address = etShelterAddress.text.toString().trim()
        val capacity = etShelterCapacity.text.toString().toLongOrNull()
        val currentOccupancy = etShelterCurrentOccupancy.text.toString().toLongOrNull()
        val supplies = etShelterSupplies.text.toString().trim()
        val contactInfo = etShelterContactInfo.text.toString().trim()
        val latitude = selectedLatitude
        val longitude = selectedLongitude

        if (latitude == null || longitude == null) {
            Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show()
            return
        }

        val shelterDocRef = if (currentShelterId != null) {
            db.collection("shelters").document(currentShelterId!!)
        } else {
            db.collection("shelters").document()
        }

        val shelter = Shelter(
            id = shelterDocRef.id,
            name = name,
            address = address,
            capacity = capacity,
            currentOccupancy = currentOccupancy,
            supplies = supplies,
            contactInfo = contactInfo,
            latitude = latitude,
            longitude = longitude,
            lastUpdated = Date()
        )

        shelterDocRef.set(shelter)
            .addOnSuccessListener {
                Toast.makeText(this, "Shelter saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving shelter: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteShelter() {
        if (currentShelterId == null) {
            Toast.makeText(this, "Cannot delete unsaved shelter.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("shelters").document(currentShelterId!!)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Shelter deleted successfully.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting shelter: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}