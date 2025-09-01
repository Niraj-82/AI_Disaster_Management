package com.example.aidm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aidm.AIDMTheme // Assuming your theme is here

class FirstAidActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // You can wrap your screen in your app's theme
            AIDMTheme { // Replace AIDMTheme with your actual theme if different
                FirstAidScreen() // Calling your Composable function
            }
        }
    }
}