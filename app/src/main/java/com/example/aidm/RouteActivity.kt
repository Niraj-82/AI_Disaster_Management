package com.example.aidm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aidm.ui.theme.AIDMTheme // Corrected import

class RouteActivity : ComponentActivity() {
    companion object {
        const val EXTRA_ORIGIN = "com.example.aidm.EXTRA_ORIGIN"
        const val EXTRA_DESTINATION = "com.example.aidm.EXTRA_DESTINATION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the origin and destination passed to this activity
        val origin = intent.getStringExtra(EXTRA_ORIGIN) ?: "Unknown Origin"
        val destination = intent.getStringExtra(EXTRA_DESTINATION) ?: "Unknown Destination"

        setContent {
            AIDMTheme {
                // Call the Composable function from your other file
                RouteScreen(originName = origin, destinationName = destination)
            }
        }
    }
}
