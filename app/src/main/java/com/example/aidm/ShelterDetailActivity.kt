package com.example.aidm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aidm.AIDMTheme // Corrected theme import

class ShelterDetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SHELTER_ID = "com.example.aidm.SHELTER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the shelter ID from the intent extras
        val shelterId = intent.getStringExtra(EXTRA_SHELTER_ID)

        setContent {
            AIDMTheme { // Your theme
                if (shelterId != null) {
                    // Pass the shelterId to your Composable screen using the correct parameter name 'id'
                    ShelterDetailScreen(id = shelterId)
                } else {
                    // It's good practice to handle the case where the ID is missing.
                    // You could show an error message or a default screen.
                    // For now, we can just show a simple text error.
                    // Make sure you have a Composable to handle this state.
                    // For example, in ShelterDetailScreen, we already have a check for a null shelter.
                    // Calling it with a non-existent ID will show the error message.
                    ShelterDetailScreen(id = "INVALID_ID")
                }
            }
        }
    }
}

