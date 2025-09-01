package com.example.aidm

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aidm.AIDMTheme // Assuming your theme is here
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen") // Required if not using the Android 12+ Splash Screen API for the main launcher
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDMTheme { // Replace AIDMTheme with your actual theme if different
                SplashScreenContent {
                    // This lambda will be executed after the delay
                    navigateToMainApp()
                }
            }
        }
    }

    private fun navigateToMainApp() {
        // Replace MainActivity::class.java with your actual main activity
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish() // Call finish to remove SplashActivity from the back stack
    }
}

@Composable
fun SplashScreenContent(onTimeout: () -> Unit) {
    // Duration of the splash screen in milliseconds
    val splashScreenDurationMillis = 1500L // 1.5 seconds

    // `LaunchedEffect` will run the block when the composable enters the composition.
    // `Unit` as a key means it runs once.
    // `true` as a key also makes it run once, use `Unit` for better semantics for one-time effects.
    LaunchedEffect(Unit) {
        delay(splashScreenDurationMillis)
        onTimeout() // Call the lambda to navigate
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Replace R.drawable.ic_launcher_foreground with your actual app logo
            // Ensure you have an image named 'ic_launcher_foreground.png' or .xml in your drawable folders
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App Logo",
                modifier = Modifier.size(128.dp) // Adjust size as needed
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "AIDM App", // Replace with your app name
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    AIDMTheme {
        SplashScreenContent {} // Pass an empty lambda for preview as navigation won't work
    }
}
