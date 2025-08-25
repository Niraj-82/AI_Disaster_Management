package com.example.aidm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aidm.ui.theme.AIDMTheme // Assuming your theme is here

class ShelterDetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SHELTER_ID = "com.example.aidm.SHELTER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the shelter ID from the intent extras
        val shelterId = intent.getStringExtra(EXTRA_SHELTER_ID)

        setContent {
            AIDMTheme { // Replace AIDMTheme with your actual theme if different
                // Pass the shelterId to your Composable screen
                ShelterDetailScreen(shelterId = shelterId)
            }
        }
    }
}
