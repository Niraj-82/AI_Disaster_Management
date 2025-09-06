package com.example.aidm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity // Ensure this import is present

class SettingsActivity : AppCompatActivity() { // Extends AppCompatActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings) // Uses XML layout
    }
}