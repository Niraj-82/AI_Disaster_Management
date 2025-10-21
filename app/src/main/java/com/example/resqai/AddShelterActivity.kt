package com.example.resqai

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddShelterActivity : AppCompatActivity() {

    private lateinit var editTextShelterName: EditText
    private lateinit var editTextLatitude: EditText
    private lateinit var editTextLongitude: EditText
    private lateinit var editTextCapacity: EditText
    private lateinit var buttonSaveShelter: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shelter)

        editTextShelterName = findViewById(R.id.edit_text_shelter_name)
        editTextLatitude = findViewById(R.id.edit_text_latitude)
        editTextLongitude = findViewById(R.id.edit_text_longitude)
        editTextCapacity = findViewById(R.id.edit_text_capacity)
        buttonSaveShelter = findViewById(R.id.button_save_shelter)

        buttonSaveShelter.setOnClickListener {
            saveShelter()
        }
    }

    private fun saveShelter() {
        val name = editTextShelterName.text.toString().trim()
        val latitude = editTextLatitude.text.toString().toDoubleOrNull()
        val longitude = editTextLongitude.text.toString().toDoubleOrNull()
        val capacity = editTextCapacity.text.toString().toLongOrNull()

        if (name.isEmpty() || latitude == null || longitude == null || capacity == null) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val shelter = hashMapOf(
            "name" to name,
            "latitude" to latitude,
            "longitude" to longitude,
            "capacity" to capacity
        )

        db.collection("shelters")
            .add(shelter)
            .addOnSuccessListener {
                Toast.makeText(this, "Shelter added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding shelter: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
