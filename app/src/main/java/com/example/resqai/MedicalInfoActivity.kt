package com.example.resqai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MedicalInfoActivity : AppCompatActivity() {

    private lateinit var etMedicalConditions: EditText
    private lateinit var etAllergies: EditText
    private lateinit var etEmergencyContactName: EditText
    private lateinit var etEmergencyContactPhone: EditText
    private lateinit var btnSaveMedicalInfo: Button
    private lateinit var btnFirstAidGuide: Button // Added button

    private val sharedPrefsName = "MedicalInfoPrefs"
    private val keyMedicalConditions = "medicalConditions"
    private val keyAllergies = "allergies"
    private val keyEmergencyContactName = "emergencyContactName"
    private val keyEmergencyContactPhone = "emergencyContactPhone"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_info)
        setTitle(getString(R.string.title_activity_medical_info))

        etMedicalConditions = findViewById(R.id.et_medical_conditions)
        etAllergies = findViewById(R.id.et_allergies)
        etEmergencyContactName = findViewById(R.id.et_emergency_contact_name)
        etEmergencyContactPhone = findViewById(R.id.et_emergency_contact_phone)
        btnSaveMedicalInfo = findViewById(R.id.btn_save_medical_info)
        btnFirstAidGuide = findViewById(R.id.btn_first_aid_guide) // Initialized button

        loadMedicalInfo()

        btnSaveMedicalInfo.setOnClickListener {
            saveMedicalInfo()
        }

        btnFirstAidGuide.setOnClickListener { // Added click listener
            val intent = Intent(this, FirstAidGuideActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveMedicalInfo() {
        val sharedPreferences = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(keyMedicalConditions, etMedicalConditions.text.toString())
        editor.putString(keyAllergies, etAllergies.text.toString())
        editor.putString(keyEmergencyContactName, etEmergencyContactName.text.toString())
        editor.putString(keyEmergencyContactPhone, etEmergencyContactPhone.text.toString())
        editor.apply()

        Toast.makeText(this, getString(R.string.medical_info_saved_toast), Toast.LENGTH_SHORT).show()
    }

    private fun loadMedicalInfo() {
        val sharedPreferences = getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        etMedicalConditions.setText(sharedPreferences.getString(keyMedicalConditions, ""))
        etAllergies.setText(sharedPreferences.getString(keyAllergies, ""))
        etEmergencyContactName.setText(sharedPreferences.getString(keyEmergencyContactName, ""))
        etEmergencyContactPhone.setText(sharedPreferences.getString(keyEmergencyContactPhone, ""))
    }
}
