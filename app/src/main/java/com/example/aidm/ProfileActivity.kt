package com.example.aidm // Or your app's package name

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// Import your ProfileScreen Composable
import com.example.aidm.ProfileScreen // Make sure this path is correct
// Import your App Theme
import com.example.aidm.AIDMTheme // Replace AIDMTheme with your actual theme name

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apply your app's theme
            AIDMTheme { // Replace AIDMTheme with your actual app theme if different
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Display your ProfileScreen Composable
                    ProfileScreen()
                }
            }
        }
    }
}
