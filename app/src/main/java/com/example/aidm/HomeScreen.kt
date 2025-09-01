package com.example.aidm

import androidx.activity.ComponentActivity // Or import android.app.Activity, etc.
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp // THE IMPORT

@Composable
fun HomeScreen(
    onOpenMap: () -> Unit,
    onOpenRoute: () -> Unit,
    onReport: () -> Unit,
    onShelters: () -> Unit,
    onFirstAid: () -> Unit,
    onAnnouncements: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit
) {
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Disaster Alerts: None nearby", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onOpenMap, modifier = Modifier.fillMaxWidth()) { Text("Live Disaster Map") }
        Button(onClick = onOpenRoute, modifier = Modifier.fillMaxWidth()) { Text("Safe Route Finder") }
        Button(onClick = onShelters, modifier = Modifier.fillMaxWidth()) { Text("Nearby Shelters") }
        Button(onClick = onReport, modifier = Modifier.fillMaxWidth()) { Text("Report Incident") }
        Button(onClick = onFirstAid, modifier = Modifier.fillMaxWidth()) { Text("First Aid Guide") }
        Button(onClick = onAnnouncements, modifier = Modifier.fillMaxWidth()) { Text("Announcements") }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                // Simple SOS via share (no SMS permission needed)
                val msg = "🚨 SOS! I need help. My location: https://maps.google.com/?q=0,0"
                val share = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, msg)
                }
                ctx.startActivity(Intent.createChooser(share, "Send SOS"))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors()
        ) { Text("🚨 SOS (Share Location)") }

        Spacer(Modifier.weight(1f))
        Row {
            OutlinedButton(onClick = onProfile, modifier = Modifier.weight(1f)) { Text("Profile") }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onSettings, modifier = Modifier.weight(1f)) { Text("Settings") }
        }
    }
}
