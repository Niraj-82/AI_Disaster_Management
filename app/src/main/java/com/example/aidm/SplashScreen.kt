package com.example.aidm

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import androidx.activity.ComponentActivity
import android.os.Bundle

@Composable
fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) { delay(1000); onDone() }
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text("AIDM • Smart Disaster Response")
    }
}
