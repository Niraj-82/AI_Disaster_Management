package com.example.aidm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aidm.ui.theme.AIDMTheme // Assuming your theme is here

class AnnouncementsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // You can wrap your screen in your app's theme
            AIDMTheme { // Replace AIDMTheme with your actual theme if different
                AnnouncementsScreen() // Calling your Composable function
            }
        }
    }
}
