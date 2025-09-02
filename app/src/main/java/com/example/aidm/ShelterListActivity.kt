package com.example.aidm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aidm.AIDMTheme // Assuming your theme is here

class ShelterListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDMTheme { // Replace AIDMTheme with your actual theme if different
                ShelterListScreen(onOpenShelter = { shelterId ->
                    // Navigate to ShelterDetailActivity, passing the ID
                    val intent = Intent(this, ShelterDetailActivity::class.java).apply {
                        putExtra("SHELTER_ID", shelterId)
                    }
                    startActivity(intent)
                })
            }
        }
    }
}
