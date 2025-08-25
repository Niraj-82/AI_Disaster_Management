package com.example.aidm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class Loading : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Call your Composable UI directly here
            // You can define it here or call a separate Composable function
            MyLoadingUi()
        }
    }
}

// Define your Composable UI function separately (recommended)
@Composable
fun MyLoadingUi() {
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize(), Alignment.Center) {
        CircularProgressIndicator()
    }
}
    