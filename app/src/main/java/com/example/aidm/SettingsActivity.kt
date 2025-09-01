package com.example.aidm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aidm.AIDMTheme // Ensure this import path is correct

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No need for enableEdgeToEdge(), setContentView(), or ViewCompat listeners with Compose
        setContent {
            AIDMTheme {
                SettingsScreen()
            }
        }
    }
}
