package com.example.resqai

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.resqai.databinding.ActivityMedicalInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MedicalInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedicalInfoBinding
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedicalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadMedicalInfo()

        binding.btnSaveMedicalInfo.setOnClickListener {
            saveMedicalInfo()
        }
    }

    private fun loadMedicalInfo() {
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        binding.editTextFullName.setText(document.getString("medical_fullName"))
                        binding.editTextBloodType.setText(document.getString("medical_bloodType"))
                        binding.editTextAllergies.setText(document.getString("medical_allergies"))
                        binding.editTextMedications.setText(document.getString("medical_medications"))
                        binding.editTextMedicalConditions.setText(document.getString("medical_medicalConditions"))
                        binding.editTextEmergencyContact.setText(document.getString("medical_emergencyContact"))
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveMedicalInfo() {
        userId?.let {
            val medicalInfo = hashMapOf(
                "medical_fullName" to binding.editTextFullName.text.toString(),
                "medical_bloodType" to binding.editTextBloodType.text.toString(),
                "medical_allergies" to binding.editTextAllergies.text.toString(),
                "medical_medications" to binding.editTextMedications.text.toString(),
                "medical_medicalConditions" to binding.editTextMedicalConditions.text.toString(),
                "medical_emergencyContact" to binding.editTextEmergencyContact.text.toString()
            )

            db.collection("users").document(it)
                .update(medicalInfo as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Medical information saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving information: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
