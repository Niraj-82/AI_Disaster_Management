package com.example.resqai

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.resqai.model.Shelter
import com.google.firebase.firestore.FirebaseFirestore

class AdminShelterEditActivity : AppCompatActivity() {

    private lateinit var etShelterName: EditText
    private lateinit var etShelterAddress: EditText
    private lateinit var etShelterCapacity: EditText
    private lateinit var etShelterCurrentOccupancy: EditText
    private lateinit var etShelterSupplies: EditText
    private lateinit var etShelterContactInfo: EditText
    private lateinit var etShelterLatitude: EditText
    private lateinit var etShelterLongitude: EditText
    private lateinit var btnSaveShelter: Button
    private lateinit var btnDeleteShelter: Button

    private var currentShelterId: String? = null
    private lateinit var db: FirebaseFirestore

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
        etShelterLatitude = findViewById(R.id.etShelterLatitude)
        etShelterLongitude = findViewById(R.id.etShelterLongitude)
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
                        etShelterSupplies.setText(it.supplies.joinToString(", "))
                        etShelterContactInfo.setText(it.contactInfo)
                        etShelterLatitude.setText(it.latitude.toString())
                        etShelterLongitude.setText(it.longitude.toString())
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
        val capacity = etShelterCapacity.text.toString().toIntOrNull() ?: 0
        val currentOccupancy = etShelterCurrentOccupancy.text.toString().toIntOrNull() ?: 0
        val supplies = etShelterSupplies.text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val contactInfo = etShelterContactInfo.text.toString().trim()
        val latitude = etShelterLatitude.text.toString().toDoubleOrNull() ?: 0.0
        val longitude = etShelterLongitude.text.toString().toDoubleOrNull() ?: 0.0

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
            longitude = longitude
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
