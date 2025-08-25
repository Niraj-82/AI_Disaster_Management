package com.example.aidm
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.activity.ComponentActivity
import android.os.Bundle

private val scheme = darkColorScheme()

@Composable
fun AIDMTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, content = content)
}
