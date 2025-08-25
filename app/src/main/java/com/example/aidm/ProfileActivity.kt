package com.example.aidm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.aidm.ui.theme.AIDMTheme // Assuming your theme is here

// This is the new Activity class
class ProfileActivity : ComponentActivity() { // Renamed from FirstAidActivity in your original file
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDMTheme { // Replace AIDMTheme with your actual theme if different
                ProfileScreen() // Calling your Composable function
            }
        }
    }
}

// This is your Composable UI function, formerly the structure of your ProfileActivity
@Composable
fun ProfileScreen() {
    // Replace this with your actual Profile Screen UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("User Profile Screen", style = MaterialTheme.typography.headlineMedium)
        // Add your profile details, edit buttons, etc. here
    }
}
